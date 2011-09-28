package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";

	public CP210x(UsbDevice device) {
		super(device);
	}

	@Override
	protected boolean setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
		
		return false;
	}
	
	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}

}
