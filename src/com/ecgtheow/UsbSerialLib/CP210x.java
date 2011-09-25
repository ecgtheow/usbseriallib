package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";

	public CP210x(UsbDevice device) {
		super(device);
	}

	@Override
	protected void setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
	}
	
	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}

}
