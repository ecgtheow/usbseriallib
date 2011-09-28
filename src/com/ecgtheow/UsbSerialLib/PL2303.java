package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.util.Log;

public class PL2303 extends UsbSerialDevice {
	private static final String TAG = "PL2303";
	
	private static final int VENDOR_WRITE_REQUEST_TYPE =	0x40;
	private static final int VENDOR_WRITE_REQUEST =			0x01;
	private static final int VENDOR_READ_REQUEST_TYPE =		0xc0;
	private static final int VENDOR_READ_REQUEST =			0x01;
	private static final int GET_LINE_REQUEST_TYPE =		0xa1;
	private static final int GET_LINE_REQUEST =				0x21;
	
	private PL2303Type type;

	public PL2303(UsbDevice device) {
		super(device);
	}

	@Override
	protected boolean setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
		
		int device_class = device.getDeviceClass();
		
		Log.d(TAG, String.format("Device class is %d", device_class));
		
		if(device_class == 0x02) {
			type = PL2303Type.Type0;
		} else if(endpoint_out.getMaxPacketSize() == 0x40) {
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
		} else {
			writeData(2, 0x24);
		}
		
		return true;
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
