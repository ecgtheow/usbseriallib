package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";

	public CP210x(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor) {
		super(device, device_descriptor);
	}

	@Override
	protected boolean setup() {
		Log.d(TAG, String.format("Setting up device %s at %s", getName(), device.getDeviceName()));
		
		return false;
	}

	@Override
	protected void getConfig() {
	}

	@Override
	protected void setConfig() {
	}

	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}

}
