package com.ecgtheow.UsbSerialLib;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum BaudRate {
	Baud_0 (0),
	Baud_300 (300),
	Baud_600 (600),
	Baud_1200 (1200),
	Baud_1800 (1800),
	Baud_2400 (2400),
	Baud_4000 (4000),
	Baud_4800 (4800),
	Baud_7200 (7200),
	Baud_9600 (9600),
	Baud_14400 (14400),
	Baud_16000 (16000),
	Baud_19200 (19200),
	Baud_28800 (28800),
	Baud_38400 (38400),
	Baud_51200 (51200),
	Baud_56000 (56000),
	Baud_57600 (57600),
	Baud_64000 (64000),
	Baud_76800 (76800),
	Baud_115200 (115200),
	Baud_128000 (128000),
	Baud_153600 (153600),
	Baud_230400 (230400),
	Baud_250000 (250000),
	Baud_256000 (256000),
	Baud_460800 (460800),
	Baud_500000 (500000),
	Baud_576000 (576000),
	Baud_921600 (921600),
	Baud_1228800 (1228800),
	Baud_1843200 (1843200),
	Baud_3686400 (3686400);
	
	private static final Map<Integer,BaudRate> lookup = new HashMap<Integer,BaudRate>();
	
	static {
		for(BaudRate b : EnumSet.allOf(BaudRate.class)) {
			lookup.put(b.getRate(), b);
		}
	}
	
	private int rate;
	
	private BaudRate(int rate) {
		this.rate = rate;
	}
	
	public int getRate() {
		return rate;
	}
	
	public static BaudRate get(int rate) {
		return lookup.get(rate);
	}
	
	/* Per CP210x AN205 Table 1 http://www.silabs.com/Support%20Documents/TechnicalDocs/an205.pdf
	 * 
	 * Added to the BaudRate enum as it's a generally useful conversion from any integer to a
	 * baud rate value.
	 */
	public final static BaudRate quantise(int rate) {
		BaudRate ret;
		
		if(rate <= 56) {
			ret = BaudRate.Baud_0;
		} else if(rate <= 300) {
			ret = BaudRate.Baud_300;
		} else if(rate <= 600) {
			ret = BaudRate.Baud_600;
		} else if(rate <= 1200) {
			ret = BaudRate.Baud_1200;
		} else if(rate <= 1800) {
			ret = BaudRate.Baud_1800;
		} else if(rate <= 2400) {
			ret = BaudRate.Baud_2400;
		} else if(rate <= 4000) {
			ret = BaudRate.Baud_4000;
		} else if(rate <= 4803) {
			ret = BaudRate.Baud_4800;
		} else if(rate <= 7207) {
			ret = BaudRate.Baud_7200;
		} else if(rate <= 9612) {
			ret = BaudRate.Baud_9600;
		} else if(rate <= 14428) {
			ret = BaudRate.Baud_14400;
		} else if(rate <= 16062) {
			ret = BaudRate.Baud_16000;
		} else if(rate <= 19250) {
			ret = BaudRate.Baud_19200;
		} else if(rate <= 28912) {
			ret = BaudRate.Baud_28800;
		} else if(rate <= 38601) {
			ret = BaudRate.Baud_38400;
		} else if(rate <= 51558) {
			ret = BaudRate.Baud_51200;
		} else if(rate <= 56280) {
			ret = BaudRate.Baud_56000;
		} else if(rate <= 58053) {
			ret = BaudRate.Baud_57600;
		} else if(rate <= 64111) {
			ret = BaudRate.Baud_64000;
		} else if(rate <= 77608) {
			ret = BaudRate.Baud_76800;
		} else if(rate <= 117028) {
			ret = BaudRate.Baud_115200;
		} else if(rate <= 129347) {
			ret = BaudRate.Baud_128000;
		} else if(rate <= 156868) {
			ret = BaudRate.Baud_153600;
		} else if(rate <= 237832) {
			ret = BaudRate.Baud_230400;
		} else if(rate <= 254234) {
			ret = BaudRate.Baud_250000;
		} else if(rate <= 273066) {
			ret = BaudRate.Baud_256000;
		} else if(rate <= 491520) {
			ret = BaudRate.Baud_460800;
		} else if(rate <= 567138) {
			ret = BaudRate.Baud_500000;
		} else if(rate <= 670254) {
			ret = BaudRate.Baud_576000;
		} else if(rate <= 1053257) {
			ret = BaudRate.Baud_921600;
		} else if(rate <= 1474560) {
			ret = BaudRate.Baud_1228800;
		} else if(rate <= 2457600) {
			ret = BaudRate.Baud_1843200;
		} else {
			ret = BaudRate.Baud_3686400;
		}
		
		return ret;
	}
}
