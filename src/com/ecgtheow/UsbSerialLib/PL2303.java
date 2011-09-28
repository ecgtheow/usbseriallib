package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
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
	
	private PL2303Type type;

	public PL2303(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor) {
		super(device, device_descriptor);
	}

	@Override
	protected boolean setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
		
		int device_class = device.getDeviceClass();
		
		Log.d(TAG, String.format("Device class is %d", device_class));
		
		if(device_class == 0x02) {
			type = PL2303Type.Type0;
		} else if(endpoint_in_int.getMaxPacketSize() == 0x40) {
			type = PL2303Type.HX;
		} else if(device_class == 0x00) {
			type = PL2303Type.Type1;
		} else if(device_class == 0xFF) {
			type = PL2303Type.Type1;
		}
		
		Log.d(TAG, String.format("Device type is %s", type.toString()));
		
		byte[] buf = new byte[10];
		
		readData(0x8484, 0, buf);
		writeData(0x0404, 0);
		readData(0x8484, 0, buf);
		readData(0x8383, 0, buf);
		readData(0x8484, 0, buf);
		writeData(0x0404, 1);
		readData(0x8484, 0, buf);
		readData(0x8383, 0, buf);
		writeData(0, 1);
		writeData(1, 0);
		
		if(type == PL2303Type.HX) {
			writeData(2, 0x44);
			
			/* Reset */
			writeData(8, 0);
			writeData(9, 0);
		} else {
			writeData(2, 0x24);
		}
		
		return true;
	}

	@Override
	protected void getConfig() {
		byte[] buf = new byte[7];
		device_connection.controlTransfer(GET_LINE_REQUEST_TYPE, GET_LINE_REQUEST, 0, 0, buf, buf.length, 100);
		
		/* Buffer holds:
		 * 
		 * byte		value
		 * 0-3		baud rate value
		 * 4		stop bits (0 for 1 stop bit, 1 for 1.5 stop bits, 2 for 2 stop bits)
		 * 5		parity (0 for none, 1 for odd, 2 for even, 3 for mark, 4 for space) 
		 * 6		data bits
		 * 
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

	@Override
	protected void setConfig() {
	}

	@Override	
	public String getName() {
		return "Prolific PL2303";
	}
	
	private int readData(int value, int index, byte[] buf) {
		return device_connection.controlTransfer(VENDOR_READ_REQUEST_TYPE, VENDOR_READ_REQUEST, value, index, buf, 1, 100);
	}
	
	private int writeData(int value, int index) {
		return device_connection.controlTransfer(VENDOR_WRITE_REQUEST_TYPE, VENDOR_WRITE_REQUEST, value, index, null, 0, 100);
	}
}
