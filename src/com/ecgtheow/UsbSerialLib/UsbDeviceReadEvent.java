package com.ecgtheow.UsbSerialLib;

public abstract interface UsbDeviceReadEvent {
	public abstract void onReadData(UsbSerialDevice device, byte[] data);
}
