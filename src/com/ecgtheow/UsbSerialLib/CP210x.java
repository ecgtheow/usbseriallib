package com.ecgtheow.UsbSerialLib;

public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";

	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}

}
