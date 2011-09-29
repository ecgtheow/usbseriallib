package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

/* Driver for Silicon Labs CP210x.
 * 
 * Datasheet at http://www.silabs.com/pages/DownloadDoc.aspx?FILEURL=Support%20Documents/TechnicalDocs/AN571.pdf
 * 
 * Other info gleaned from the Linux cp210x driver.
 */
public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";
	
	/* Request types */
	private static final int HOST_TO_DEVICE_REQUEST_TYPE = 	0x41;
	private static final int DEVICE_TO_HOST_REQUEST_TYPE =	0xc1;

	/* Request codes */
	private static final int IFC_ENABLE =					0x00;
	private static final int SET_BAUDDIV =					0x01;
	private static final int GET_BAUDDIV =					0x02;
	private static final int SET_LINE_CTL =					0x03;
	private static final int GET_LINE_CTL =					0x04;
	//private static final int SET_BREAK =					0x05;
	//private static final int IMM_CHAR =						0x06;
	//private static final int SET_MHS =						0x07;
	//private static final int GET_MDMSTS =					0x08;
	//private static final int SET_XON =						0x09;
	//private static final int SET_XOFF =						0x0A;
	//private static final int SET_EVENTMASK =				0x0B;
	//private static final int GET_EVENTMASK =				0x0C;
	//private static final int GET_EVENTSTATE =				0x16;
	//private static final int SET_CHAR =						0x0D;
	//private static final int GET_CHARS =					0x0E;
	//private static final int GET_PROPS =					0x0F;
	//private static final int GET_COMM_STATUS =				0x10;
	//private static final int RESET =						0x11;
	//private static final int PURGE =						0x12;
	//private static final int SET_FLOW =						0x13;
	private static final int GET_FLOW =						0x14;
	//private static final int EMBED_EVENTS =					0x15;
	//private static final int GET_BAUDRATE =					0x1D;
	//private static final int SET_BAUDRATE =					0x1E;
	//private static final int SET_CHARS =					0x19;
	//private static final int VENDOR_SPECIFIC =				0xFF;
	
	/* IFC_ENABLE */
	private static final int UART_ENABLE =					0x0001;
	private static final int UART_DISABLE =					0x0000;
	
	/* SET_BAUDDIV / GET_BAUDDIV */
	private static final int BAUD_RATE_GEN_FREQ =			0x384000;
	
	public CP210x(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor, UsbDeviceReadEvent read_event) {
		super(device, device_descriptor, read_event);
	}

	@Override
	protected boolean setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
		
		vendorWriteSingle(IFC_ENABLE, UART_ENABLE);
		
		return true;
	}

	@Override
	protected void takedown() {
		Log.d(TAG, String.format("Taking down device %s at %s", getName(), device.getDeviceName()));
		
		vendorWriteSingle(IFC_ENABLE, UART_DISABLE);
	}

	@Override
	protected void getConfig() {
		byte[] buf = new byte[2];
		
		vendorRead(GET_BAUDDIV, buf);
		Log.d(TAG, String.format("Read bauddiv bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		int baud = ((int)buf[0] & 0xff) +
				(((int)buf[1] << 8) & 0xff00);
		if(baud > 0) {
			int real_baud = BAUD_RATE_GEN_FREQ / baud;
			baudrate = device_descriptor.quantise_baudrate(real_baud);
			Log.d(TAG, String.format("baud %d real_baud %d baudrate %s", baud, real_baud, baudrate.toString()));
		}
		
		vendorRead(GET_LINE_CTL, buf);
		Log.d(TAG, String.format("Read line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		
		byte b = buf[1];
		switch(b) {
		case 5:
			databits = DataBits.Data_5;
			break;
		case 6:
			databits = DataBits.Data_6;
			break;
		case 7:
			databits = DataBits.Data_7;
			break;
		case 8:
			databits = DataBits.Data_8;
			break;
		default:
			Log.w(TAG, String.format("Unknown or unsupported data bit value: %d", b));
			databits = DataBits.Data_8;
			buf[1] = 8;
			vendorWrite(SET_LINE_CTL, buf);
		}
		
		b = (byte) ((buf[0] & 0xF0) >> 4);
		switch(b) {
		case 0:
			parity = Parity.None;
			break;
		case 1:
			parity = Parity.Odd;
			break;
		case 2:
			parity = Parity.Even;
			break;
		case 3:
			parity = Parity.Mark;
			break;
		case 4:
			parity = Parity.Space;
			break;
		default:
			Log.w(TAG, String.format("Unknown parity value: %d", b));
			parity = Parity.None;
			buf[0] &= 0x0F;
			vendorWrite(SET_LINE_CTL, buf);
		}
		
		b = (byte)(buf[0] & 0x0F);
		switch(b) {
		case 0:
			stopbits = StopBits.Stop_1;
			break;
		case 1:
			stopbits = StopBits.Stop_1_5;
			break;
		case 2:
			stopbits = StopBits.Stop_2;
			break;
		default:
			Log.w(TAG, String.format("Unknown stop bit value: %d", b));
			stopbits = StopBits.Stop_1;
			buf[0] &= 0xF0;
			vendorWrite(SET_LINE_CTL, buf);
		}
		
		byte[] modem_buf = new byte[16];
		vendorRead(GET_FLOW, modem_buf);
		if((modem_buf[0] & 0x08) > 0) {
			Log.d(TAG, "Using CRTSCTS");
		} else {
			Log.d(TAG, "Not using CRTSCTS");
		}
	}

	@Override
	public void setBaudRate(BaudRate rate) {
		if(baudrate == rate) {
			return;
		}
		
		BaudRate real_rate = device_descriptor.quantise_baudrate(rate.getRate());
		int baud_div = BAUD_RATE_GEN_FREQ / real_rate.getRate();
		vendorWriteSingle(SET_BAUDDIV, baud_div);
		
		baudrate = real_rate;
		Log.d(TAG, String.format("set baud rate to %s for %s at %s", baudrate.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public void setDataBits(DataBits bits) {
		if(databits == bits) {
			return;
		}
		
		byte[] buf = new byte[2];
		
		vendorRead(GET_LINE_CTL, buf);
		Log.d(TAG, String.format("Read line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		
		if(bits == DataBits.Data_5) {
			buf[1] = 5;
		} else if(bits == DataBits.Data_6) {
			buf[1] = 6;
		} else if(bits == DataBits.Data_7) {
			buf[1] = 7;
		} else {
			buf[1] = 8;
		}
		
		Log.d(TAG, String.format("Writing line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		vendorWrite(SET_LINE_CTL, buf);
		
		databits = bits;
		Log.d(TAG, String.format("set databits to %s for %s at %s", databits.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public void setParity(Parity par) {
		if(parity == par) {
			return;
		}
		
		byte[] buf = new byte[2];
		
		vendorRead(GET_LINE_CTL, buf);
		Log.d(TAG, String.format("Read line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		
		buf[0] &= ~0xF0;
		if(par == Parity.Odd){
			buf[0] |= (1 << 4);
		} else if(par == Parity.Even) {
			buf[0] |= (2 << 4);
		} else if(par == Parity.Mark) {
			buf[0] |= (3 << 4);
		} else if(par == Parity.Space) {
			buf[0] |= (4 << 4);
		}
		
		Log.d(TAG, String.format("Writing line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		vendorWrite(SET_LINE_CTL, buf);
		
		parity = par;
		Log.d(TAG, String.format("set parity to %s for %s at %s", parity.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public void setStopBits(StopBits bits) {
		if(stopbits == bits) {
			return;
		}
		
		byte[] buf = new byte[2];
		
		vendorRead(GET_LINE_CTL, buf);
		Log.d(TAG, String.format("Read line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		
		buf[0] &= ~0x0F;
		if(bits == StopBits.Stop_1_5) {
			buf[0] |= 1;
		} else if(bits == StopBits.Stop_2) {
			buf[0] |= 2;
		}
		
		Log.d(TAG, String.format("Writing line ctl bytes 0x%02x 0x%02x", (int)buf[0], (int)buf[1]));
		vendorWrite(SET_LINE_CTL, buf);
		
		stopbits = bits;
		Log.d(TAG, String.format("set stopbits to %s for %s at %s", stopbits.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}
	
	private int vendorRead(int request, byte[] buf) {
		return device_connection.controlTransfer(DEVICE_TO_HOST_REQUEST_TYPE, request, 0, 0, buf, buf.length, 300);
	}
	
	private int vendorWrite(int request, byte[] buf) {
		if(buf.length > 2) {
			return device_connection.controlTransfer(HOST_TO_DEVICE_REQUEST_TYPE, request, 0, 0, buf, buf.length, 300);
		} else {
			int value = ((int)buf[0] & 0xff) +
					(((int)buf[1] << 8) & 0xff00);
			
			Log.d(TAG, String.format("Using direct write, value 0x%04x", value));
			
			return vendorWriteSingle(request, value);
		}
	}
	
	private int vendorWriteSingle(int request, int value) {
		return device_connection.controlTransfer(HOST_TO_DEVICE_REQUEST_TYPE, request, value, 0, null, 0, 300);
	}
}
