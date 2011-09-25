package com.ecgtheow.UsbSerialLib;

import java.util.ArrayList;

import android.hardware.usb.UsbDevice;
import android.util.Log;

// Run the data transfers in a background thread
public abstract class UsbSerialDevice implements Runnable {
	private static final String TAG = "UsbSerialDevice";
	private UsbDevice device = null;
	
	private static final ArrayList<UsbSerialDeviceDescriptor> devices = new ArrayList<UsbSerialDeviceDescriptor>() {
		private static final long serialVersionUID = 4274183412791649462L; /* Shut eclipse up */

	{
		add(new PL2303Descriptor());
		add(new CP210xDescriptor());
	}};
	
	public static UsbSerialDevice createDevice(UsbDevice device) {
		for(UsbSerialDeviceDescriptor driver_descriptor : devices) {
			if(driver_descriptor.knownDevice(device.getVendorId(), device.getProductId())) {
				try {
					UsbSerialDevice driver = driver_descriptor.driverClass().newInstance();
					driver.device = device;
					return driver;
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Log.d(TAG, "Exception thrown", e);
					return null;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Log.d(TAG, "Exception thrown", e);
					return null;
				}
			}
		}
		return null;
	}
	
	public static boolean knownDevice(UsbDevice device) {
		for(UsbSerialDeviceDescriptor driver_descriptor : devices) {
			if(driver_descriptor.knownDevice(device.getVendorId(), device.getProductId())) {
				return true;
			}
		}
		
		return false;
	}

	public UsbDevice getDevice() {
		return device;
	}

	public void run() {
		// TODO Auto-generated method stub

	}
	
	public abstract String getName();
}
