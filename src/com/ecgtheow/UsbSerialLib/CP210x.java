package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;

public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";

	public CP210x(UsbDevice device) {
		super(device);
	}

	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}

}
