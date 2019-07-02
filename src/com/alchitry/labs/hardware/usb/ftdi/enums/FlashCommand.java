package com.alchitry.labs.hardware.usb.ftdi.enums;

public enum FlashCommand {
	WE((byte) 0x06), /* Write Enable */
	SRWE((byte) 0x50), /* Volatile SR Write Enable */
	WD((byte) 0x04), /* Write Disable */
	RPD((byte) 0xAB), /* Release Power-Down, returns Device ID */
	MFGID((byte) 0x90), /* Read Manufacturer/Device ID */
	JEDECID((byte) 0x9F), /* Read JEDEC ID */
	UID((byte) 0x4B), /* Read Unique ID */
	RD((byte) 0x03), /* Read Data */
	FR((byte) 0x0B), /* Fast Read */
	PP((byte) 0x02), /* Page Program */
	SE((byte) 0x20), /* Sector Erase 4kb */
	BE32((byte) 0x52), /* Block Erase 32kb */
	BE64((byte) 0xD8), /* Block Erase 64kb */
	CE((byte) 0xC7), /* Chip Erase */
	RSR1((byte) 0x05), /* Read Status Register 1 */
	WSR1((byte) 0x01), /* Write Status Register 1 */
	RSR2((byte) 0x35), /* Read Status Register 2 */
	WSR2((byte) 0x31), /* Write Status Register 2 */
	RSR3((byte) 0x15), /* Read Status Register 3 */
	WSR3((byte) 0x11), /* Write Status Register 3 */
	RSFDP((byte) 0x5A), /* Read SFDP Register */
	ESR((byte) 0x44), /* Erase Security Register */
	PSR((byte) 0x42), /* Program Security Register */
	RSR((byte) 0x48), /* Read Security Register */
	GBL((byte) 0x7E), /* Global Block Lock */
	GBU((byte) 0x98), /* Global Block Unlock */
	RBL((byte) 0x3D), /* Read Block Lock */
	RPR((byte) 0x3C), /* Read Sector Protection Registers (adesto) */
	IBL((byte) 0x36), /* Individual Block Lock */
	IBU((byte) 0x39), /* Individual Block Unlock */
	EPS((byte) 0x75), /* Erase / Program Suspend */
	EPR((byte) 0x7A), /* Erase / Program Resume */
	PD((byte) 0xB9), /* Power-down */
	QPI((byte) 0x38), /* Enter QPI mode */
	ERESET((byte) 0x66), /* Enable Reset */
	RESET((byte) 0x99); /* Reset Device */

	private byte command;

	private FlashCommand(byte cmd) {
		command = cmd;
	}

	public byte getCommand() {
		return command;
	}
}
