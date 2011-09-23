package com.ecgtheow.UsbSerialLib;

import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public final class UsbSerialLib {
	private static final String TAG = "UsbSerialLib";
	
	private static UsbSerialLib instance;
	
	private UsbManager manager;
	private PendingIntent permission_intent;
	
	/* See http://developer.android.com/guide/topics/usb/host.html
	 * 
	 * This string is registered with the PendingIntent permission_intent in initialize(), used
	 * in the requestPermission() call, and monitored in the BroadcastReceiver usb_receiver().
	 */
	private static final String ACTION_USB_PERMISSION = "com.ecgtheow.UsbSerialLib.USB_PERMISSION";
	
	private UsbSerialLib() {
	}
	
	public static synchronized UsbSerialLib getInstance(Context context) {
		if(instance == null) {
			instance = new UsbSerialLib();
			
			instance.manager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
			instance.permission_intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
			IntentFilter permission_filter = new IntentFilter();
			permission_filter.addAction(ACTION_USB_PERMISSION);
			permission_filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
			permission_filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
			context.registerReceiver(instance.usb_receiver, permission_filter);
		}
		
		return instance;
	}
	
	public final int connectDevices(UsbDeviceConnectionEvent connection_event) {
		int connect_count = 0;
		HashMap <String,UsbDevice> device_list = manager.getDeviceList();
		
		Log.d(TAG, String.format("%d devices found", device_list.size()));
		
		Iterator<UsbDevice> iter = device_list.values().iterator();
		while(iter.hasNext()) {
			UsbDevice device = iter.next();
			
			UsbSerialDevice serial_device = UsbSerialDevice.createDevice(device);
			if(serial_device != null) {
				Log.d(TAG, String.format("Found a %s [0x%04x:0x%04x]", serial_device.getName(), device.getVendorId(), device.getProductId()));
			}
		}
		
		return connect_count;
	}
	
	private final boolean known_device(UsbDevice device) {
		return false;
	}
	
	private final BroadcastReceiver usb_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_USB_PERMISSION.equals(action)) {
				synchronized(this) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						/* Permission has been granted */
						if(device != null) {
						}
					} else {
						/* Permission not granted */
					}
				}
			} else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if(device != null && known_device(device)) {
					/* Ask for permission to use it */
					manager.requestPermission(device, permission_intent);
				}
			} else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if(device != null) {
					/* clean up */
				}
			}
		}
	};
}
