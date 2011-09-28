package com.ecgtheow.UsbSerialLib;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum BaudRate {
	Baud_0 (0),
	Baud_75 (75),
	Baud_150 (150),
	Baud_300 (300),
	Baud_600 (600),
	Baud_1200 (1200),
	Baud_1800 (1800),
	Baud_2400 (2400),
	Baud_3600 (3600),
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
	Baud_614400 (614400),
	Baud_921600 (921600),
	Baud_1228800 (1228800),
	Baud_1843200 (1843200),
	Baud_2457600 (2457600),
	Baud_3000000 (3000000),
	Baud_3686400 (3686400),
	Baud_6000000 (6000000),
	Baud_12000000 (12000000);
	
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
}
