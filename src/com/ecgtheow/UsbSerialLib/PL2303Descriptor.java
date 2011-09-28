package com.ecgtheow.UsbSerialLib;

import java.util.ArrayList;

public class PL2303Descriptor extends UsbSerialDeviceDescriptor {

	private ArrayList<UsbDeviceId> id_table = new ArrayList<UsbDeviceId>() {
		private static final long serialVersionUID = 615147424349140335L; /* Shut eclipse up */

	{
		add(new UsbDeviceId(0x067b, 0x2303)); /* Prolific PL2303 */
		add(new UsbDeviceId(0x067b, 0x04bb)); /* Prolific RSAQ2 */
		add(new UsbDeviceId(0x067b, 0x1234)); /* Prolific DCU11 */
		add(new UsbDeviceId(0x067b, 0xaaa2)); /* Prolific RSAQ3 */
		add(new UsbDeviceId(0x067b, 0xaaa0)); /* Prolific Pharos */
		add(new UsbDeviceId(0x067b, 0x0611)); /* Prolific ALDIGA */
		add(new UsbDeviceId(0x067b, 0x0612)); /* Prolific MMX */
		add(new UsbDeviceId(0x067b, 0x0609)); /* Prolific GPRS */
		add(new UsbDeviceId(0x067b, 0x331a)); /* Prolific HCR331 */
		add(new UsbDeviceId(0x067b, 0x0307)); /* Prolific Motorola */
		add(new UsbDeviceId(0x04bb, 0x0a03)); /* IOData */
		add(new UsbDeviceId(0x04bb, 0x0a0e)); /* IOData RSAQ5 */
		add(new UsbDeviceId(0x0557, 0x2008)); /* Aten */
		add(new UsbDeviceId(0x0547, 0x2008)); /* Aten */
		add(new UsbDeviceId(0x056e, 0x5003)); /* Elcom */
		add(new UsbDeviceId(0x056e, 0x5004)); /* Elcom UCSGT */
		add(new UsbDeviceId(0x0eba, 0x1080)); /* Itegno */
		add(new UsbDeviceId(0x0eba, 0x2080)); /* Itegno 2080 */
		add(new UsbDeviceId(0x0df7, 0x0620)); /* MA620 */
		add(new UsbDeviceId(0x0584, 0xb000)); /* Ratoc */
		add(new UsbDeviceId(0x2478, 0x2008)); /* Tripp */
		add(new UsbDeviceId(0x1453, 0x4026)); /* Radioshack */
		add(new UsbDeviceId(0x0731, 0x0528)); /* DCU10 */
		add(new UsbDeviceId(0x6189, 0x2068)); /* Sitecom */
		add(new UsbDeviceId(0x11f7, 0x02df)); /* Alcatel OT535/735 USB cable */
		add(new UsbDeviceId(0x04e8, 0x8001)); /* Samsung I330 phone cradle */
		add(new UsbDeviceId(0x11f5, 0x0001)); /* Siemens SX1 */
		add(new UsbDeviceId(0x11f5, 0x0003)); /* Siemens X65 */
		add(new UsbDeviceId(0x11f5, 0x0004)); /* Siemens X75 */
		add(new UsbDeviceId(0x11f5, 0x0005)); /* Siemens EF81 */
		add(new UsbDeviceId(0x04a5, 0x4027)); /* Benq/Siemens S81 */
		add(new UsbDeviceId(0x0745, 0x0001)); /* Syntech */
		add(new UsbDeviceId(0x078b, 0x1234)); /* Nokia CA-42 Cable */
		add(new UsbDeviceId(0x10b5, 0xac70)); /* CA-42 clone cable */
		add(new UsbDeviceId(0x079b, 0x0027)); /* Sagem */
		add(new UsbDeviceId(0x0413, 0x2101)); /* Leadtek GPS 9531 */
		add(new UsbDeviceId(0x0e55, 0x110b)); /* Speed Dragon USB GSM Cable */
		add(new UsbDeviceId(0x0731, 0x2003)); /* Datapilot Universal-2 Phone Cable */
		add(new UsbDeviceId(0x050d, 0x0257)); /* Belkin "F5U257" Serial Adapter */
		add(new UsbDeviceId(0x058f, 0x9720)); /* Alcor Micro Corp. USB 2.0 to RS-232 */
		add(new UsbDeviceId(0x11f6, 0x2001)); /* Willcom WS002IN Data Driver */
		add(new UsbDeviceId(0x07aa, 0x002a)); /* Corega CG-USBRS232R Serial Adapter */
		add(new UsbDeviceId(0x05ad, 0x0fba)); /* Y.C. Cable U.S.A., Inc - USB to RS-232 */
		add(new UsbDeviceId(0x5372, 0x2303)); /* "Superial" USB - Serial */
		add(new UsbDeviceId(0x03f0, 0x3524)); /* Hewlett-Packard LD220-HP POS Pole Display */
		add(new UsbDeviceId(0x04b8, 0x0521)); /* Cressi Edy (diving computer) PC interface */
		add(new UsbDeviceId(0x04b8, 0x0522)); /* Zeagle dive computer interface */
		add(new UsbDeviceId(0x054c, 0x0437)); /* Sony, USB data cable for CMD-Jxx mobile phones */
		add(new UsbDeviceId(0x11ad, 0x0001)); /* Sanwa KB-USB2 multimeter cable */
		add(new UsbDeviceId(0x0b63, 0x6530)); /* Adlink ND-6530 RS232, RS485 and RS422 adapter */
		add(new UsbDeviceId(0x4348, 0x5523)); /* WinChipHead USB->RS 232 adapter */
	}};

	@Override
	public boolean knownDevice(int vendor_id, int product_id) {
		for(UsbDeviceId dev : id_table) {
			if(dev.getVendorId() == vendor_id && dev.getProductId() == product_id) {
				return true;
			}
		}
		return false;
	}

	@Override
	public BaudRate quantise_baudrate(int rate) {
		BaudRate ret;
		
		if(rate < 150) {
			ret = BaudRate.Baud_75;
		} else if(rate < 300) {
			ret = BaudRate.Baud_150;
		} else if(rate < 600) {
			ret = BaudRate.Baud_300;
		} else if(rate < 1200) {
			ret = BaudRate.Baud_600;
		} else if(rate < 1800) {
			ret = BaudRate.Baud_1200;
		} else if(rate < 2400) {
			ret = BaudRate.Baud_1800;
		} else if(rate < 3600) {
			ret = BaudRate.Baud_2400;
		} else if(rate < 4800) {
			ret = BaudRate.Baud_3600;
		} else if(rate < 7200) {
			ret = BaudRate.Baud_4800;
		} else if(rate < 9600) {
			ret = BaudRate.Baud_7200;
		} else if(rate < 14400) {
			ret = BaudRate.Baud_9600;
		} else if(rate < 19200) {
			ret = BaudRate.Baud_14400;
		} else if(rate < 28800) {
			ret = BaudRate.Baud_19200;
		} else if(rate < 38400) {
			ret = BaudRate.Baud_28800;
		} else if(rate < 57600) {
			ret = BaudRate.Baud_38400;
		} else if(rate < 115200) {
			ret = BaudRate.Baud_57600;
		} else if(rate < 230400) {
			ret = BaudRate.Baud_115200;
		} else if(rate < 460800) {
			ret = BaudRate.Baud_230400;
		} else if(rate < 614400) {
			ret = BaudRate.Baud_460800;
		} else if(rate < 921600) {
			ret = BaudRate.Baud_614400;
		} else if(rate < 1228800) {
			ret = BaudRate.Baud_921600;
		} else if(rate < 2457600) {
			ret = BaudRate.Baud_1228800;
		} else if(rate < 3000000) {
			ret = BaudRate.Baud_2457600;
		} else if(rate < 6000000) {
			ret = BaudRate.Baud_3000000;
		} else {
			/* HX rev D can do 12000000 too, maybe return it here if we can tell
			 * its a rev D chip somehow
			 */
			ret = BaudRate.Baud_6000000;
		}
		
		return ret;
	}

	@Override
	public Class<? extends UsbSerialDevice> driverClass() {
		return PL2303.class;
	}
}
