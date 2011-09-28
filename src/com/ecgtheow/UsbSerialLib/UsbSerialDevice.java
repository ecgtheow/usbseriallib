package com.ecgtheow.UsbSerialLib;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

// Run the data transfers in a background thread
public abstract class UsbSerialDevice implements Runnable {
	private static final String TAG = "UsbSerialDevice";
	
	protected UsbDevice device = null;
	protected UsbSerialDevice serial_device = null; /* convenience for the read_handler */
	protected UsbSerialDeviceDescriptor device_descriptor = null;
	protected UsbDeviceReadEvent read_event = null;
	protected UsbDeviceConnection device_connection = null;
	protected UsbInterface device_interface = null;
	protected UsbEndpoint endpoint_in = null;
	protected UsbEndpoint endpoint_out = null;
	
	protected BaudRate baudrate = null;
	protected DataBits databits = null;
	protected Parity parity = null;
	protected StopBits stopbits = null;
	
	protected boolean keep_running = true;
	protected Handler read_handler = new Handler() {
		public void handleMessage(Message msg) {
			byte[] buf = msg.getData().getByteArray("read_data");
			read_event.onReadData(serial_device, buf);
		}
	};
	
	public UsbSerialDevice(UsbDevice device, UsbSerialDeviceDescriptor device_descriptor, UsbDeviceReadEvent read_event) {
		Log.d(TAG, "Created device");
		this.device = device;
		this.serial_device = this;
		this.device_descriptor = device_descriptor;
		this.read_event = read_event;
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
					}
				}
				
				if(endpoint_out == null || endpoint_in == null) {
					/* Didn't get all endpoints, try the next interface */
					Log.d(TAG, String.format("Didn't get all endpoints from iface %d for %s at %s", i, getName(), device.getDeviceName()));
					endpoint_out = null;
					endpoint_in = null;
					device_connection.releaseInterface(iface);
				} else {
					break;
				}
			} else {
				Log.d(TAG, String.format("claimInterface %d failed for %s at %s", i, getName(), device.getDeviceName()));
			}
		}
		
		/* Tried all the interfaces, make sure we have the endpoints */
		if(endpoint_out == null || endpoint_in == null) {
			device_connection.close();
			return false;
		}
		
		Log.d(TAG, String.format("Claimed interface and found endpoints for %s at %s", getName(), device.getDeviceName()));
		
		/* Run the device-specific setup stuff */
		boolean ret = setup();
		if(ret) {
			getConfig();
			Log.d(TAG, String.format("Initial device configuration is: baud %s, databits %s, parity %s, stopbits %s", baudrate.toString(), databits.toString(), parity.toString(), stopbits.toString()));
			setBaudRate(BaudRate.Baud_9600);
			setDataBits(DataBits.Data_8);
			setParity(Parity.None);
			setStopBits(StopBits.Stop_1);
			getConfig();
			Log.d(TAG, String.format("Device configuration is now: baud %s, databits %s, parity %s, stopbits %s", baudrate.toString(), databits.toString(), parity.toString(), stopbits.toString()));
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

	/* Can't use UsbRequest.queue() here, as apparently no way to tell how many bytes were returned!!!
	 * 
	 * So have to poll in a thread instead.
	 */
	public void run() {
		Thread thr = Thread.currentThread();
		
		Log.d(TAG, String.format("[%s] Running device %s", thr.getName(), getName()));
		
		// Undecided whether to use byte[] or ByteBuffer here, more testing needed!
		byte[] read_buf = new byte[4096];
		//ByteBuffer read_buf = ByteBuffer.allocate(4096);
		
		while(keep_running) {
			try {
				int bytes_read = device_connection.bulkTransfer(endpoint_in, read_buf, read_buf.length, 100);
				//int bytes_read = device_connection.bulkTransfer(endpoint_in, read_buf.array(), 4096, 100);
				if(bytes_read < 0) {
					//Log.w(TAG, String.format("Bulk transfer failed on %s at %s: %d", getName(), device.getDeviceName(), bytes_read));
				} else if(bytes_read > 0) {
					Message msg = read_handler.obtainMessage();
					Bundle bundle = new Bundle();
					byte[] buf = Arrays.copyOf(read_buf, bytes_read);
					//byte[] buf = Arrays.copyOf(read_buf.array(), bytes_read);
					bundle.putByteArray("read_data", buf);
					msg.setData(bundle);

					read_handler.sendMessage(msg);
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.d(TAG, "InterruptedException", e);
				}
			} catch (Exception e) {
				Log.d(TAG, "Exception", e);
			}
		}
		
		Log.d(TAG, String.format("[%s] Stopped device %s", thr.getName(), getName()));
	}
	
	public void write(byte[] buf) {
		ByteBuffer send_buf = ByteBuffer.wrap(buf);
		
		UsbRequest req = new UsbRequest();
		boolean ok = req.initialize(device_connection, endpoint_out);
		if(ok) {
			req.setClientData("Send data");
			req.queue(send_buf, send_buf.capacity());
		}
	}
	
	public BaudRate getBaudRate() {
		return baudrate;
	}
	
	public abstract void setBaudRate(BaudRate rate);
	
	public DataBits getDataBits() {
		return databits;
	}
	
	public abstract void setDataBits(DataBits bits);
	
	public Parity getParity() {
		return parity;
	}
	
	public abstract void setParity(Parity par);
	
	public StopBits getStopBits() {
		return stopbits;
	}
	
	public abstract void setStopBits(StopBits bits);
	
	protected abstract boolean setup();
	protected abstract void getConfig();
	public abstract String getName();
}
