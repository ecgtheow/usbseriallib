package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbDevice;
import android.util.Log;

public class CP210x extends UsbSerialDevice {
	private static final String TAG = "CP210x";

	public CP210x(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor, UsbDeviceReadEvent read_event) {
		super(device, device_descriptor, read_event);
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
	public void setBaudRate(BaudRate rate) {
	}

	@Override
	public void setDataBits(DataBits bits) {
	}

	@Override
	public void setParity(Parity par) {
	}

	@Override
	public void setStopBits(StopBits bits) {
	}

	@Override
	public String getName() {
		return "Silicon Labs CP210x";
	}

}
