package com.ecgtheow.UsbSerialLib;

public class UsbDeviceId {
	private int vendor_id;
	private int product_id;
	
	public UsbDeviceId(int vendor_id, int product_id) {
		this.vendor_id = vendor_id;
		this.product_id = product_id;
	}
	
	public int getVendorId() {
		return vendor_id;
	}
	
	public int getProductId() {
		return product_id;
	}
}
