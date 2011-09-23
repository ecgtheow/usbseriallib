package com.ecgtheow.UsbSerialLib;

import java.util.ArrayList;

import android.hardware.usb.UsbDevice;

// Run the data transfers in a background thread
public abstract class UsbSerialDevice implements Runnable {
	private static final String TAG = "UsbSerialDevice";
	
	private static final ArrayList<UsbSerialDeviceDescriptor> devices = new ArrayList<UsbSerialDeviceDescriptor>() {{
		add(new PL2303Descriptor());
		add(new CP210xDescriptor());
	}};
	
	public static UsbSerialDevice createDevice(UsbDevice device) {
		for(UsbSerialDeviceDescriptor driver_descriptor : devices) {
			if(driver_descriptor.knownDevice(device.getVendorId(), device.getProductId())) {
				try {
					return driver_descriptor.driverClass().newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

	public void run() {
		// TODO Auto-generated method stub

	}
	
	public abstract String getName();
}
