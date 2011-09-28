package com.ecgtheow.UsbSerialLib;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.hardware.usb.UsbDevice;
import android.util.Log;

public class UsbSerialDeviceFactory {
	private static final String TAG = "UsbSerialDeviceFactory";
	
	private static final ArrayList<UsbSerialDeviceDescriptor> devices = new ArrayList<UsbSerialDeviceDescriptor>() {
		private static final long serialVersionUID = 4274183412791649462L; /* Shut eclipse up */

	{
		add(new PL2303Descriptor());
		add(new CP210xDescriptor());
	}};
	
	public static UsbSerialDevice createDevice(UsbDevice device, UsbDeviceReadEvent read_event) {
		for(UsbSerialDeviceDescriptor driver_descriptor : devices) {
			if(driver_descriptor.knownDevice(device.getVendorId(), device.getProductId())) {
				try {
					UsbSerialDevice driver = driver_descriptor.driverClass().getConstructor(UsbDevice.class, UsbSerialDeviceDescriptor.class, UsbDeviceReadEvent.class).newInstance(device, driver_descriptor, read_event);
					return driver;
				} catch (InstantiationException e) {
					Log.d(TAG, "Exception thrown", e);
					return null;
				} catch (IllegalAccessException e) {
					Log.d(TAG, "Exception thrown", e);
					return null;
				} catch (IllegalArgumentException e) {
					Log.d(TAG, "Exception thrown", e);
					return null;
				} catch (SecurityException e) {
					Log.d(TAG, "Exception thrown", e);
					return null;
				} catch (InvocationTargetException e) {
					Log.d(TAG, "Exception thrown", e);
					return null;
				} catch (NoSuchMethodException e) {
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
}
