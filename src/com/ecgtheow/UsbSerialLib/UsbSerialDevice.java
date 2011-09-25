package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

// Run the data transfers in a background thread
public abstract class UsbSerialDevice implements Runnable {
	private static final String TAG = "UsbSerialDevice";
	protected UsbDevice device = null;
	
	private boolean keep_running = true;
	
	public UsbSerialDevice(UsbDevice device) {
		Log.d(TAG, "Created device");
		this.device = device;
		setup();
		start();
		stop();
	}

	public UsbDevice getDevice() {
		return device;
	}
	
	public void start() {
		// Runs run()
		new Thread(this).start();
	}
	
	public void stop() {
		keep_running = false;
	}

	public void run() {
		Log.d(TAG, String.format("Running device %s", getName()));
		
		while(keep_running) {
			
		}
		
		Log.d(TAG, String.format("Stopped device %s", getName()));
	}
	
	protected abstract void setup();
	public abstract String getName();
}
