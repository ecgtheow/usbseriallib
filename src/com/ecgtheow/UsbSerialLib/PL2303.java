package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;

public class PL2303 extends UsbSerialDevice {
	private static final String TAG = "PL2303";

	public PL2303(UsbDevice device) {
		super(device);
	}

	@Override	
	public String getName() {
		return "Prolific PL2303";
	}
}
