package com.alchitry.labs.hardware.ftdi.enums;

public enum MpsseCommand {
	SETB_LOW((byte) 0x80), /* Set Data bits LowByte */
	READB_LOW((byte) 0x81), /* Read Data bits LowByte */
	SETB_HIGH((byte) 0x82), /* Set Data bits HighByte */
	READB_HIGH((byte) 0x83), /* Read data bits HighByte */
	LOOPBACK_EN((byte) 0x84), /* Enable loopback */
	LOOPBACK_DIS((byte) 0x85), /* Disable loopback */
	SET_CLK_DIV((byte) 0x86), /* Set clock divisor */
	FLUSH((byte) 0x87), /* Flush buffer fifos to the PC. */
	WAIT_H((byte) 0x88), /* Wait on GPIOL1 to go high. */
	WAIT_L((byte) 0x89), /* Wait on GPIOL1 to go low. */
	TCK_X5((byte) 0x8A), /* Disable /5 div, enables 60MHz master clock */
	TCK_D5((byte) 0x8B), /* Enable /5 div, backward compat to FT2232D */
	EN_3PH_CLK((byte) 0x8C), /* Enable 3 phase clk, DDR I2C */
	DIS_3PH_CLK((byte) 0x8D), /* Disable 3 phase clk */
	CLK_N((byte) 0x8E), /* Clock every bit, used for JTAG */
	CLK_N8((byte) 0x8F), /* Clock every byte, used for JTAG */
	CLK_TO_H((byte) 0x94), /* Clock until GPIOL1 goes high */
	CLK_TO_L((byte) 0x95), /* Clock until GPIOL1 goes low */
	EN_ADPT_CLK((byte) 0x96), /* Enable adaptive clocking */
	DIS_ADPT_CLK((byte) 0x97), /* Disable adaptive clocking */
	CLK8_TO_H((byte) 0x9C), /* Clock until GPIOL1 goes high, count bytes */
	CLK8_TO_L((byte) 0x9D), /* Clock until GPIOL1 goes low, count bytes */
	TRI((byte) 0x9E), /* Set IO to only drive on 0 and tristate on 1 */
	/* CPU mode commands */
	CPU_RS((byte) 0x90), /* CPUMode read short address */
	CPU_RE((byte) 0x91), /* CPUMode read extended address */
	CPU_WS((byte) 0x92), /* CPUMode write short address */
	CPU_WE((byte) 0x93); /* CPUMode write extended address */

	private byte command;

	private MpsseCommand(byte cmd) {
		command = cmd;
	}

	public byte getCommand() {
		return command;
	}
}
