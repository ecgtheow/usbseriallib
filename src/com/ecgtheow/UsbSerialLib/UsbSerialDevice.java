package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

// Run the data transfers in a background thread
public abstract class UsbSerialDevice implements Runnable {
	private static final String TAG = "UsbSerialDevice";
	private UsbDevice device = null;
	
	public UsbSerialDevice(UsbDevice device) {
		this.device = device;
		Log.d(TAG, "Created device");
	}

	public UsbDevice getDevice() {
		return device;
	}

	public void run() {
		// TODO Auto-generated method stub

	}
	
	public abstract String getName();
}
