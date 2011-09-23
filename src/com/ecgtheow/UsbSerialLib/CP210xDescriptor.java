package com.ecgtheow.UsbSerialLib;

import java.util.ArrayList;

public class CP210xDescriptor extends UsbSerialDeviceDescriptor {

	private ArrayList<UsbDeviceId> id_table = new ArrayList<UsbDeviceId>() {{
        add(new UsbDeviceId(0x045B, 0x0053)); /* Renesas RX610 RX-Stick */
        add(new UsbDeviceId(0x0471, 0x066A)); /* AKTAKOM ACE-1001 cable */
        add(new UsbDeviceId(0x0489, 0xE000)); /* Pirelli Broadband S.p.A, DP-L10 SIP/GSM Mobile */
        add(new UsbDeviceId(0x0745, 0x1000)); /* CipherLab USB CCD Barcode Scanner 1000 */
        add(new UsbDeviceId(0x08e6, 0x5501)); /* Gemalto Prox-PU/CU contactless smartcard reader */
        add(new UsbDeviceId(0x08FD, 0x000A)); /* Digianswer A/S , ZigBee/802.15.4 MAC Device */
        add(new UsbDeviceId(0x0BED, 0x1100)); /* MEI (TM) Cashflow-SC Bill/Voucher Acceptor */
        add(new UsbDeviceId(0x0BED, 0x1101)); /* MEI series 2000 Combo Acceptor */
        add(new UsbDeviceId(0x0FCF, 0x1003)); /* Dynastream ANT development board */
        add(new UsbDeviceId(0x0FCF, 0x1004)); /* Dynastream ANT2USB */
        add(new UsbDeviceId(0x0FCF, 0x1006)); /* Dynastream ANT development board */
        add(new UsbDeviceId(0x10A6, 0xAA26)); /* Knock-off DCU-11 cable */
        add(new UsbDeviceId(0x10AB, 0x10C5)); /* Siemens MC60 Cable */
        add(new UsbDeviceId(0x10B5, 0xAC70)); /* Nokia CA-42 USB */
        add(new UsbDeviceId(0x10C4, 0x0F91)); /* Vstabi */
        add(new UsbDeviceId(0x10C4, 0x1101)); /* Arkham Technology DS101 Bus Monitor */
        add(new UsbDeviceId(0x10C4, 0x1601)); /* Arkham Technology DS101 Adapter */
        add(new UsbDeviceId(0x10C4, 0x800A)); /* SPORTident BSM7-D-USB main station */
        add(new UsbDeviceId(0x10C4, 0x803B)); /* Pololu USB-serial converter */
        add(new UsbDeviceId(0x10C4, 0x8044)); /* Cygnal Debug Adapter */
        add(new UsbDeviceId(0x10C4, 0x804E)); /* Software Bisque Paramount ME build-in converter */
        add(new UsbDeviceId(0x10C4, 0x8053)); /* Enfora EDG1228 */
        add(new UsbDeviceId(0x10C4, 0x8054)); /* Enfora GSM2228 */
        add(new UsbDeviceId(0x10C4, 0x8066)); /* Argussoft In-System Programmer */
        add(new UsbDeviceId(0x10C4, 0x806F)); /* IMS USB to RS422 Converter Cable */
        add(new UsbDeviceId(0x10C4, 0x807A)); /* Crumb128 board */
        add(new UsbDeviceId(0x10C4, 0x80CA)); /* Degree Controls Inc */
        add(new UsbDeviceId(0x10C4, 0x80DD)); /* Tracient RFID */
        add(new UsbDeviceId(0x10C4, 0x80F6)); /* Suunto sports instrument */
        add(new UsbDeviceId(0x10C4, 0x8115)); /* Arygon NFC/Mifare Reader */
        add(new UsbDeviceId(0x10C4, 0x813D)); /* Burnside Telecom Deskmobile */
        add(new UsbDeviceId(0x10C4, 0x813F)); /* Tams Master Easy Control */
        add(new UsbDeviceId(0x10C4, 0x814A)); /* West Mountain Radio RIGblaster P&P */
        add(new UsbDeviceId(0x10C4, 0x814B)); /* West Mountain Radio RIGtalk */
        add(new UsbDeviceId(0x10C4, 0x8156)); /* B&G H3000 link cable */
        add(new UsbDeviceId(0x10C4, 0x815E)); /* Helicomm IP-Link 1220-DVM */
        add(new UsbDeviceId(0x10C4, 0x818B)); /* AVIT Research USB to TTL */
        add(new UsbDeviceId(0x10C4, 0x819F)); /* MJS USB Toslink Switcher */
        add(new UsbDeviceId(0x10C4, 0x81A6)); /* ThinkOptics WavIt */
        add(new UsbDeviceId(0x10C4, 0x81AC)); /* MSD Dash Hawk */
        add(new UsbDeviceId(0x10C4, 0x81AD)); /* INSYS USB Modem */
        add(new UsbDeviceId(0x10C4, 0x81C8)); /* Lipowsky Industrie Elektronik GmbH, Baby-JTAG */
        add(new UsbDeviceId(0x10C4, 0x81E2)); /* Lipowsky Industrie Elektronik GmbH, Baby-LIN */
        add(new UsbDeviceId(0x10C4, 0x81E7)); /* Aerocomm Radio */
        add(new UsbDeviceId(0x10C4, 0x81E8)); /* Zephyr Bioharness */
        add(new UsbDeviceId(0x10C4, 0x81F2)); /* C1007 HF band RFID controller */
        add(new UsbDeviceId(0x10C4, 0x8218)); /* Lipowsky Industrie Elektronik GmbH, HARP-1 */
        add(new UsbDeviceId(0x10C4, 0x822B)); /* Modem EDGE(GSM) Comander 2 */
        add(new UsbDeviceId(0x10C4, 0x826B)); /* Cygnal Integrated Products, Inc., Fasttrax GPS demostration module */
        add(new UsbDeviceId(0x10C4, 0x8293)); /* Telegesys ETRX2USB */
        add(new UsbDeviceId(0x10C4, 0x82F9)); /* Procyon AVS */
        add(new UsbDeviceId(0x10C4, 0x8341)); /* Siemens MC35PU GPRS Modem */
        add(new UsbDeviceId(0x10C4, 0x8382)); /* Cygnal Integrated Products, Inc. */
        add(new UsbDeviceId(0x10C4, 0x83A8)); /* Amber Wireless AMB2560 */
        add(new UsbDeviceId(0x10C4, 0x83D8)); /* DekTec DTA Plus VHF/UHF Booster/Attenuator */
        add(new UsbDeviceId(0x10C4, 0x8411)); /* Kyocera GPS Module */
        add(new UsbDeviceId(0x10C4, 0x8418)); /* IRZ Automation Teleport SG-10 GSM/GPRS Modem */
        add(new UsbDeviceId(0x10C4, 0x846E)); /* BEI USB Sensor Interface (VCP) */
        add(new UsbDeviceId(0x10C4, 0x8477)); /* Balluff RFID */
        add(new UsbDeviceId(0x10C4, 0x85EA)); /* AC-Services IBUS-IF */
        add(new UsbDeviceId(0x10C4, 0x85EB)); /* AC-Services CIS-IBUS */
        add(new UsbDeviceId(0x10C4, 0x8664)); /* AC-Services CAN-IF */
        add(new UsbDeviceId(0x10C4, 0x8665)); /* AC-Services OBD-IF */
        add(new UsbDeviceId(0x10C4, 0xEA60)); /* Silicon Labs factory default */
        add(new UsbDeviceId(0x10C4, 0xEA61)); /* Silicon Labs factory default */
        add(new UsbDeviceId(0x10C4, 0xEA71)); /* Infinity GPS-MIC-1 Radio Monophone */
        add(new UsbDeviceId(0x10C4, 0xF001)); /* Elan Digital Systems USBscope50 */
        add(new UsbDeviceId(0x10C4, 0xF002)); /* Elan Digital Systems USBwave12 */
        add(new UsbDeviceId(0x10C4, 0xF003)); /* Elan Digital Systems USBpulse100 */
        add(new UsbDeviceId(0x10C4, 0xF004)); /* Elan Digital Systems USBcount50 */
        add(new UsbDeviceId(0x10C5, 0xEA61)); /* Silicon Labs MobiData GPRS USB Modem */
        add(new UsbDeviceId(0x10CE, 0xEA6A)); /* Silicon Labs MobiData GPRS USB Modem 100EU */
        add(new UsbDeviceId(0x13AD, 0x9999)); /* Baltech card reader */
        add(new UsbDeviceId(0x1555, 0x0004)); /* Owen AC4 USB-RS485 Converter */
        add(new UsbDeviceId(0x166A, 0x0303)); /* Clipsal 5500PCU C-Bus USB interface */
        add(new UsbDeviceId(0x16D6, 0x0001)); /* Jablotron serial interface */
        add(new UsbDeviceId(0x16DC, 0x0010)); /* W-IE-NE-R Plein & Baus GmbH PL512 Power Supply */
        add(new UsbDeviceId(0x16DC, 0x0011)); /* W-IE-NE-R Plein & Baus GmbH RCM Remote Control for MARATON Power Supply */
        add(new UsbDeviceId(0x16DC, 0x0012)); /* W-IE-NE-R Plein & Baus GmbH MPOD Multi Channel Power Supply */
        add(new UsbDeviceId(0x16DC, 0x0015)); /* W-IE-NE-R Plein & Baus GmbH CML Control, Monitoring and Data Logger */
        add(new UsbDeviceId(0x17F4, 0xAAAA)); /* Wavesense Jazz blood glucose meter */
        add(new UsbDeviceId(0x1843, 0x0200)); /* Vaisala USB Instrument Cable */
        add(new UsbDeviceId(0x18EF, 0xE00F)); /* ELV USB-I2C-Interface */
        add(new UsbDeviceId(0x1BE3, 0x07A6)); /* WAGO 750-923 USB Service Cable */
        add(new UsbDeviceId(0x413C, 0x9500)); /* DW700 GPS USB interface */
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
	public Class<? extends UsbSerialDevice> driverClass() {
		return CP210x.class;
	}
}
