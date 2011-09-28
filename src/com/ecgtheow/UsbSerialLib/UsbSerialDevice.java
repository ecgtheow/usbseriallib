package com.ecgtheow.UsbSerialLib;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

// Run the data transfers in a background thread
public abstract class UsbSerialDevice implements Runnable {
	private static final String TAG = "UsbSerialDevice";
	
	protected UsbDevice device = null;
	protected UsbSerialDeviceDescriptor device_descriptor = null;
	protected UsbDeviceConnection device_connection = null;
	protected UsbInterface device_interface = null;
	protected UsbEndpoint endpoint_in_int = null;
	protected UsbEndpoint endpoint_in = null;
	protected UsbEndpoint endpoint_out = null;
	
	protected BaudRate baudrate = BaudRate.Baud_9600;
	protected DataBits databits = DataBits.Data_8;
	protected Parity parity = Parity.None;
	protected StopBits stopbits = StopBits.Stop_0;
	
	private boolean keep_running = true;
	
	public UsbSerialDevice(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor) {
		Log.d(TAG, "Created device");
		this.device = device;
		this.device_descriptor = device_descriptor;
	}
	
	public boolean connect(UsbManager manager)
	{
		device_connection = manager.openDevice(device);
		if(device_connection == null) {
			Log.d(TAG, String.format("openDevice failed for %s at %s", getName(), device.getDeviceName()));
			return false;
		}
		
		for(int i = 0; i < device.getInterfaceCount(); i++) {
			UsbInterface iface = device.getInterface(i);
		
			if(device_connection.claimInterface(iface, true)) {
				device_interface = iface;
				
				for(int j = 0; j < iface.getEndpointCount(); j++) {
					UsbEndpoint ep = iface.getEndpoint(j);
					
					if(ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
						if(ep.getDirection() == UsbConstants.USB_DIR_OUT) {
							Log.d(TAG, String.format("Got output endpoint %d for %s at %s", j, getName(), device.getDeviceName()));
							endpoint_out = ep;
						} else {
							Log.d(TAG, String.format("Got input endpoint %d for %s at %s", j, getName(), device.getDeviceName()));
							endpoint_in = ep;
						}
					} else if(ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT && ep.getDirection() == UsbConstants.USB_DIR_IN) {
						Log.d(TAG, String.format("Got interrupt input endpoint %d for %s at %s", j, getName(), device.getDeviceName()));
						endpoint_in_int = ep;
					}
				}
				
				if(endpoint_out == null || endpoint_in_int == null || endpoint_in == null) {
					/* Didn't get all endpoints, try the next interface */
					Log.d(TAG, String.format("Didn't get all endpoints from iface %d for %s at %s", i, getName(), device.getDeviceName()));
					endpoint_out = null;
					endpoint_in = null;
					endpoint_in_int = null;
					device_connection.releaseInterface(iface);
				} else {
					break;
				}
			} else {
				Log.d(TAG, String.format("claimInterface %d failed for %s at %s", i, getName(), device.getDeviceName()));
			}
		}
		
		/* Tried all the interfaces, make sure we have the endpoints */
		if(endpoint_out == null || endpoint_in_int == null || endpoint_in == null) {
			device_connection.close();
			return false;
		}
		
		Log.d(TAG, String.format("Claimed interface and found endpoints for %s at %s", getName(), device.getDeviceName()));
		
		/* Run the device-specific setup stuff */
		boolean ret = setup();
		if(ret) {
			try {
			getConfig();
			Log.d(TAG, String.format("Device configuration is: baud %s, databits %s, parity %s, stopbits %s", baudrate.toString(), databits.toString(), parity.toString(), stopbits.toString()));
			} catch (Exception e) {
	        	Log.e(TAG, "Exception thrown", e);
			}
		}
		return ret;
	}

	public UsbDevice getDevice() {
		return device;
	}
	
	public void start() {
		// Runs run()
		new Thread(this).start();
	}
	
	public void stop() {
		keep_running = false;
	}

	public void run() {
		Log.d(TAG, String.format("Running device %s", getName()));
		
		while(keep_running) {
			
		}
		
		Log.d(TAG, String.format("Stopped device %s", getName()));
	}
	
	protected abstract boolean setup();
	protected abstract void getConfig();
	protected abstract void setConfig();
	public abstract String getName();
}
