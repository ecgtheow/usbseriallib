package com.ecgtheow.UsbSerialLib;

public abstract class UsbSerialDeviceDescriptor {
	public abstract boolean knownDevice(int vendor_id, int product_id);
	public abstract Class<? extends UsbSerialDevice> driverClass();
}
