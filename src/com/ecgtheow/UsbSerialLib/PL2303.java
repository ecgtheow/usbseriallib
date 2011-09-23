package com.ecgtheow.UsbSerialLib;

public class PL2303 extends UsbSerialDevice {
	private static final String TAG = "PL2303";

	@Override	
	public String getName() {
		return "Prolific PL2303";
	}
}
