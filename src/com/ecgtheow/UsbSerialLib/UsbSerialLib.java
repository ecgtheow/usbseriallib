package com.ecgtheow.UsbSerialLib;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public final class UsbSerialLib {
	private static UsbSerialLib instance;
	
	private static UsbManager manager;
	private static UsbDevice dev;
	private static PendingIntent permission_intent;
	
	/* See http://developer.android.com/guide/topics/usb/host.html
	 * 
	 * This string is registered with the PendingIntent permission_intent in initialize(), used
	 * in the requestPermission() call, and monitored in the BroadcastReceiver usb_receiver().
	 */
	private static final String ACTION_USB_PERMISSION = "com.ecgtheow.UsbSerialLib.USB_PERMISSION";
	
	private UsbSerialLib() {
	}
	
	public static synchronized UsbSerialLib getInstance() {
		if(instance == null) {
			instance = new UsbSerialLib();
		}
		
		return instance;
	}
	
	public static final void initialize(Context context) {
		manager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
		permission_intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter permission_filter = new IntentFilter();
		permission_filter.addAction(ACTION_USB_PERMISSION);
		permission_filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		permission_filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		context.registerReceiver(usb_receiver, permission_filter);
	}
	
	private static final BroadcastReceiver usb_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ACTION_USB_PERMISSION.equals(action)) {
				synchronized(this) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					
					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						/* Permission has been granted */
						if(device != null) {
							dev = device;
						}
					} else {
						/* Permission not granted */
						dev = null;
					}
				}
			} else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if(device != null) {
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
