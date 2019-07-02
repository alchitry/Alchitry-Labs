package com.alchitry.labs.hardware.usb.ftdi;

public class FtdiEeprom {
	public static final int FTDI_MAX_EEPROM_SIZE = 256;
	public static final int MAX_POWER_MILLIAMP_PER_UNIT = 2;
	
	   /** vendor id */
    public int vendor_id;
    /** product id */
    public int product_id;

    /** Was the eeprom structure initialized for the actual
        connected device? **/
    public int initialized_for_connected_device;

    /** self powered */
    public int self_powered;
    /** remote wakeup */
    public int remote_wakeup;

    public int is_not_pnp;

    /* Suspend on DBUS7 Low */
    public int suspend_dbus7;

    /** input in isochronous transfer mode */
    public int in_is_isochronous;
    /** output in isochronous transfer mode */
    public int out_is_isochronous;
    /** suspend pull downs */
    public int suspend_pull_downs;

    /** use serial */
    public int use_serial;
    /** usb version */
    public int usb_version;
    /** Use usb version on FT2232 devices*/
    public int use_usb_version;
    /** maximum power */
    public int max_power;

    /** manufacturer name */
    public String manufacturer;
    /** product name */
    public String product;
    /** serial number */
    public String serial;

    /* 2232D/H specific */
    /* Hardware type, 0 = RS232 Uart, 1 = 245 FIFO, 2 = CPU FIFO,
       4 = OPTO Isolate */
    public int channel_a_type;
    public int channel_b_type;
    /*  Driver Type, 1 = VCP */
    public int channel_a_driver;
    public int channel_b_driver;
    public int channel_c_driver;
    public int channel_d_driver;
    /* 4232H specific */
    public int channel_a_rs485enable;
    public int channel_b_rs485enable;
    public int channel_c_rs485enable;
    public int channel_d_rs485enable;

    /* Special function of FT232R/FT232H devices (and possibly others as well) */
    /** CBUS pin function. See CBUS_xxx defines. */
    public int[] cbus_function = new int[10];
    /** Select hight current drive on R devices. */
    public int high_current;
    /** Select hight current drive on A channel (2232C */
    public int high_current_a;
    /** Select hight current drive on B channel (2232C). */
    public int high_current_b;
    /** Select inversion of data lines (bitmask). */
    public int invert;
    /** Enable external oscillator. */
    public int external_oscillator;

    /*2232H/4432H Group specific values */
    /* Group0 is AL on 2322H and A on 4232H
       Group1 is AH on 2232H and B on 4232H
       Group2 is BL on 2322H and C on 4232H
       Group3 is BH on 2232H and C on 4232H*/
    public int group0_drive;
    public int group0_schmitt;
    public int group0_slew;
    public int group1_drive;
    public int group1_schmitt;
    public int group1_slew;
    public int group2_drive;
    public int group2_schmitt;
    public int group2_slew;
    public int group3_drive;
    public int group3_schmitt;
    public int group3_slew;

    public int powersave;

    public int clock_polarity;
    public int data_order;
    public int flow_control;

    /** user data **/
    public int user_data_addr;
    public int user_data_size;
    public byte[] user_data;

    /** eeprom size in bytes. This doesn't get stored in the eeprom
        but is the only way to pass it to ftdi_eeprom_build. */
    public int size;
    /* EEPROM Type 0x46 for 93xx46, 0x56 for 93xx56 and 0x66 for 93xx66*/
    public int chip;
    public byte[] buf = new byte[FTDI_MAX_EEPROM_SIZE];

    /** device release number */
    public int release_number;
}
