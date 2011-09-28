package com.ecgtheow.UsbSerialLib;

import java.util.Arrays;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

/* Driver for Prolific PL2303.
 * 
 * Datasheet at http://www.prolific.com.tw/support/files//IO%Cable/PL-2303HX/Documents/Datasheet/ds_pl2303HXD_v1.3.pdf
 * 
 * Other info gleaned from the Linux driver.
 */
public class PL2303 extends UsbSerialDevice {
	private static final String TAG = "PL2303";
	
	private static final int VENDOR_WRITE_REQUEST_TYPE =	0x40;
	private static final int VENDOR_WRITE_REQUEST =			0x01;
	private static final int VENDOR_READ_REQUEST_TYPE =		0xc0;
	private static final int VENDOR_READ_REQUEST =			0x01;
	private static final int GET_LINE_REQUEST_TYPE =		0xa1;
	private static final int GET_LINE_REQUEST =				0x21;
	private static final int SET_LINE_REQUEST_TYPE =		0x21;
	private static final int SET_LINE_REQUEST =				0x20;
	private static final int SET_CONTROL_REQUEST_TYPE =		0x21;
	private static final int SET_CONTROL_REQUEST =			0x22;
	private static final int CONTROL_DTR =					0x01;
	private static final int CONTROL_RTS =					0x02;
	
	private PL2303Type type;
	private byte[] current_config;

	public PL2303(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor, UsbDeviceReadEvent read_event) {
		super(device, device_descriptor, read_event);
	}

	@Override
	protected boolean setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
		
		int device_class = device.getDeviceClass();
		
		Log.d(TAG, String.format("Device class is %d", device_class));
		
		UsbEndpoint endpoint0 = device_interface.getEndpoint(0);
		
		if(device_class == 0x02) {
			type = PL2303Type.Type0;
		} else if(endpoint0.getMaxPacketSize() == 0x40) {
			type = PL2303Type.HX;
		} else if(device_class == 0x00) {
			type = PL2303Type.Type1;
		} else if(device_class == 0xFF) {
			type = PL2303Type.Type1;
		}
		
		Log.d(TAG, String.format("Device type is %s", type.toString()));
		
		byte[] buf = new byte[10];
		
		vendorRead(0x8484, 0, buf);
		vendorWrite(0x0404, 0);
		vendorRead(0x8484, 0, buf);
		vendorRead(0x8383, 0, buf);
		vendorRead(0x8484, 0, buf);
		vendorWrite(0x0404, 1);
		vendorRead(0x8484, 0, buf);
		vendorRead(0x8383, 0, buf);
		vendorWrite(0, 1);
		vendorWrite(1, 0);
		
		if(type == PL2303Type.HX) {
			vendorWrite(2, 0x44);
			
			/* Reset */
			vendorWrite(8, 0);
			vendorWrite(9, 0);
		} else {
			vendorWrite(2, 0x24);
		}
		
		return true;
	}

	@Override
	protected void takedown() {
		/* Nothing to do */
	}

	@Override
	protected void getConfig() {
		byte[] buf = new byte[7];

		getLine(buf);
		
		/* Buffer holds:
		 * 
		 * byte		value
		 * 0-3		baud rate value
		 * 4		stop bits (0 for 1 stop bit, 1 for 1.5 stop bits, 2 for 2 stop bits)
		 * 5		parity (0 for none, 1 for odd, 2 for even, 3 for mark, 4 for space) 
		 * 6		data bits
		 */
		
		int rate = ((int)buf[0] & 0xff) +
				(((int)buf[1] << 8) & 0xff00) +
				(((int)buf[2] << 16) & 0xff0000) +
				(((int)buf[3] << 24) & 0xff000000);
		baudrate = device_descriptor.quantise_baudrate(rate);
		Log.d(TAG, String.format("Read baud rate bytes as %d [%02x %02x %02x %02x], set baudrate to %s", rate, buf[0], buf[1], buf[2], buf[3], baudrate.toString()));
		
		byte b = buf[4];
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
		}
		
		b = buf[5];
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
		}
		
		b = buf[6];
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
			Log.w(TAG, String.format("Unknown data bit value: %d", b));
			databits = DataBits.Data_8;
		}
	}

	/*
	 * In the config set methods do an array compare of the existing device
	 * config with the proposed config, rather then comparing BaudRate etc
	 * settings, as my device at least starts up with a bogus setting for
	 * DataBits (it always powers up with 0 in byte 6).
	 */

	@Override
	public void setBaudRate(BaudRate rate) {
		byte[] buf = buildConfig();
		
		if(Arrays.equals(current_config, buf)) {
			return;
		}

		BaudRate real_rate = device_descriptor.quantise_baudrate(rate.getRate());
		setBaudBytes(buf, real_rate.getRate());
		setLine(buf);
		
		baudrate = real_rate;
		Log.d(TAG, String.format("set baud rate to %s for %s at %s", baudrate.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public void setDataBits(DataBits bits) {
		byte[] buf = buildConfig();		
		
		if(Arrays.equals(current_config, buf)) {
			return;
		}
		
		setDataByte(buf, bits);
		setLine(buf);
		
		databits = bits;
		Log.d(TAG, String.format("set databits to %s for %s at %s", databits.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public void setParity(Parity par) {
		byte[] buf = buildConfig();
		
		if(Arrays.equals(current_config, buf)) {
			return;
		}
		
		setParityByte(buf, par);
		setLine(buf);
		
		parity = par;
		Log.d(TAG, String.format("set parity to %s for %s at %s", parity.toString(), getName(), device.getDeviceName()));
	}

	@Override
	public void setStopBits(StopBits bits) {
		byte[] buf = buildConfig();
		
		if(Arrays.equals(current_config, buf)) {
			return;
		}
		
		setStopByte(buf, bits);
		setLine(buf);
		
		stopbits = bits;
		Log.d(TAG, String.format("set stopbits to %s for %s at %s", stopbits.toString(), getName(), device.getDeviceName()));
	}

	@Override	
	public String getName() {
		return "Prolific PL2303";
	}
	
	private byte[] buildConfig() {
		byte[] buf = new byte[7];
		
		int rate = baudrate.getRate();
		setBaudBytes(buf, rate);
		setDataByte(buf, databits);
		setParityByte(buf, parity);
		setStopByte(buf, stopbits);
		
		return buf;
	}
	
	private void setBaudBytes(byte[] buf, int rate) {
		buf[0] = (byte)(rate & 0xff);
		buf[1] = (byte)((rate >> 8) & 0xff);
		buf[2] = (byte)((rate >> 16) & 0xff);
		buf[3] = (byte)((rate >> 24) & 0xff);
	}
	
	private void setDataByte(byte[] buf, DataBits bits) {
		if(bits == DataBits.Data_5) {
			buf[6] = 5;
		} else if(bits == DataBits.Data_6) {
			buf[6] = 6;
		} else if(bits == DataBits.Data_7) {
			buf[6] = 7;
		} else {
			buf[6] = 8;
		}
	}
	
	private void setParityByte(byte[] buf, Parity par) {
		if(par == Parity.None){
			buf[5] = 0;
		} else if(par == Parity.Odd){
			buf[5] = 1;
		} else if(par == Parity.Even) {
			buf[5] = 2;
		} else if(par == Parity.Mark) {
			buf[5] = 3;
		} else if(par == Parity.Space) {
			buf[5] = 4;
		} else {
			buf[5] = 0;
		}
	}
	
	private void setStopByte(byte[] buf, StopBits bits) {
		if(bits == StopBits.Stop_1) {
			buf[4] = 0;
		} else if(bits == StopBits.Stop_1_5) {
			buf[4] = 1;
		} else if(bits == StopBits.Stop_2) {
			buf[4] = 2;
		} else {
			buf[4] = 0;
		}
	}
	
	private int vendorRead(int value, int index, byte[] buf) {
		return device_connection.controlTransfer(VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, value, index, buf, 1, 100);
	}
	
	private int vendorWrite(int value, int index) {
		return device_connection.controlTransfer(VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, value, index, null, 0, 100);
	}
	
	private int getLine(byte[] buf) {
		int ret = device_connection.controlTransfer(GET_LINE_REQUEST_TYPE, GET_LINE_REQUEST, 0, 0, buf, buf.length, 100);
		current_config = buf;

		Log.d(TAG, String.format("Get line: 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x 0x%02x (%d)", buf[0], buf[1], buf[2], buf[3], buf[4], buf[5], buf[6], ret));
				
		return ret;
	}
	
	private int setLine(byte[] buf) {
		int ret = device_connection.controlTransfer(SET_LINE_REQUEST_TYPE, SET_LINE_REQUEST, 0, 0, buf, buf.length, 100);
		current_config = buf;
		
		setControl(CONTROL_DTR | CONTROL_RTS);
		
		/* Clear RTSCTS */
		vendorWrite(0, 0);
		
		return ret;
	}
	
	private int setControl(int value) {
		return device_connection.controlTransfer(SET_CONTROL_REQUEST_TYPE, SET_CONTROL_REQUEST, value, 0, null, 0, 100);
	}
}
