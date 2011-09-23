package com.ecgtheow.UsbSerialLib;

public abstract interface UsbDeviceConnectionEvent {
	public abstract void onUsbDeviceConnected(UsbSerialDevice device);
}
