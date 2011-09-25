package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

public class PL2303 extends UsbSerialDevice {
	private static final String TAG = "PL2303";

	public PL2303(UsbDevice device) {
		super(device);
	}

	@Override
	protected void setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
	}
	
	@Override	
	public String getName() {
		return "Prolific PL2303";
	}
}
