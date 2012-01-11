package com.ecgtheow.UsbSerialLib;

import java.util.ArrayList;
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

	private UsbManager manager;
	private PendingIntent permission_intent;
	private UsbDeviceConnectionEvent connection_event;
	private UsbDeviceReadEvent read_event;
	private ArrayList<UsbSerialDevice> connected_devices = new ArrayList<UsbSerialDevice>();
	
	/* See http://developer.android.com/guide/topics/usb/host.html
	 * 
	 * This string is registered with the PendingIntent permission_intent in initialize(), used
	 * in the requestPermission() call, and monitored in the BroadcastReceiver usb_receiver().
	 */
	private static final String ACTION_USB_PERMISSION = "com.ecgtheow.UsbSerialLib.USB_PERMISSION";
	
	public UsbSerialLib(Context context, UsbDeviceConnectionEvent connection_event, UsbDeviceReadEvent read_event) {
		manager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
		permission_intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
		this.connection_event = connection_event;
		this.read_event = read_event;
	}

	public void registerReceiver(Context context) {
		Log.d(TAG, "Registering USB receiver");
		
		IntentFilter permission_filter = new IntentFilter();
		permission_filter.addAction(ACTION_USB_PERMISSION);
		permission_filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		permission_filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		context.registerReceiver(usb_receiver, permission_filter);
	}
	
	public void unregisterReceiver(Context context) {
		Log.d(TAG, "Unregistering USB receiver");
		
		context.unregisterReceiver(usb_receiver);
	}
	
	public final int connectDevices() {
		int connect_count = 0;
		HashMap <String,UsbDevice> device_list = manager.getDeviceList();
		
		Log.d(TAG, String.format("%d devices found", device_list.size()));
		
		Iterator<UsbDevice> iter = device_list.values().iterator();
		while(iter.hasNext()) {
			UsbDevice device = iter.next();
			connectDevice(device);
		}
		
		return connect_count;
	}
	
	public final void connectDevice(UsbDevice device) {
		if(UsbSerialDeviceFactory.knownDevice(device)) {
			Log.d(TAG, String.format("Found a known device [0x%04x:0x%04x]", device.getVendorId(), device.getProductId()));
			manager.requestPermission(device, permission_intent);
		}
	}
	
	public final void disconnectDevice(UsbSerialDevice serial_device) {
		serial_device.disconnect();
	}
	
	private final BroadcastReceiver usb_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, String.format("Received action: %s", action));
			
			synchronized(this) {
				if(ACTION_USB_PERMISSION.equals(action)) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

					if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						/* Permission has been granted */
						if(device != null) {
							UsbSerialDevice serial_device = UsbSerialDeviceFactory.createDevice(device, read_event);
							if(serial_device != null) {
								Log.d(TAG, String.format("Created new serial device: %s at %s", serial_device.getName(), device.getDeviceName()));

								if(serial_device.connect(manager)) {
									Log.d(TAG, String.format("Connected serial device: %s at %s", serial_device.getName(), device.getDeviceName()));
									connected_devices.add(serial_device);

									connection_event.onUsbDeviceConnected(serial_device);

									serial_device.start();
								}
							} else {
								Log.d(TAG, String.format("Failed to create serial device for %s", device.getDeviceName()));
							}
						}
					} else {
						/* Permission not granted */
						Log.d(TAG, String.format("Permission denied for %s!", device.getDeviceName()));
					}
				} else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if(device != null && UsbSerialDeviceFactory.knownDevice(device)) {
						/* Ask for permission to use it */
						Log.d(TAG, String.format("Device attached at %s", device.getDeviceName()));
						manager.requestPermission(device, permission_intent);
					}
				} else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if(device != null) {
						/* clean up */
						for(UsbSerialDevice serial_device : connected_devices) {
							if(device.equals(serial_device.getDevice())) {
								serial_device.stop();
								if (connected_devices.remove(serial_device)) {
									Log.d(TAG, String.format("Removed: %s at %s", serial_device.getName(), device.getDeviceName()));
								}
							}
						}
					}
				}
			}
		}
	};
}
