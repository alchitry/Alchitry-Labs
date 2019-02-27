set_property BITSTREAM.GENERAL.COMPRESS TRUE [current_design]
set_property BITSTREAM.CONFIG.CONFIGRATE 33 [current_design]
set_property CONFIG_VOLTAGE 3.3 [current_design]
set_property CFGBVS VCCO [current_design]
set_property BITSTREAM.CONFIG.SPI_32BIT_ADDR NO [current_design]
set_property BITSTREAM.CONFIG.SPI_BUSWIDTH 1 [current_design]
set_property BITSTREAM.CONFIG.SPI_FALL_EDGE YES [current_design]

create_clock -period 10.000 -name clk -waveform {0.000 5.000} [get_ports clk]
set_property PACKAGE_PIN N14 [get_ports clk]
set_property IOSTANDARD LVCMOS33 [get_ports clk]

set_property PACKAGE_PIN P6 [get_ports rst_n]
set_property IOSTANDARD LVCMOS33 [get_ports rst_n]

set_property PACKAGE_PIN K13 [get_ports {led[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[0]}]

set_property PACKAGE_PIN K12 [get_ports {led[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[1]}]

set_property PACKAGE_PIN L14 [get_ports {led[2]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[2]}]

set_property PACKAGE_PIN L13 [get_ports {led[3]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[3]}]

set_property PACKAGE_PIN M16 [get_ports {led[4]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[4]}]

set_property PACKAGE_PIN M14 [get_ports {led[5]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[5]}]

set_property PACKAGE_PIN M12 [get_ports {led[6]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[6]}]

set_property PACKAGE_PIN N16 [get_ports {led[7]}]
set_property IOSTANDARD LVCMOS33 [get_ports {led[7]}]

# serial names are flipped in the schematic (named for the FTDI chip)
set_property PACKAGE_PIN P16 [get_ports {usb_tx}]
set_property IOSTANDARD LVCMOS33 [get_ports {usb_tx}]

set_property PACKAGE_PIN P15 [get_ports {usb_rx}]
set_property IOSTANDARD LVCMOS33 [get_ports {usb_rx}]

# LED Bank 1
set_property -dict { PACKAGE_PIN B6 IOSTANDARD LVCMOS33 } [get_ports {io_led[0]}];

set_property -dict { PACKAGE_PIN B5 IOSTANDARD LVCMOS33 } [get_ports {io_led[1]}];

set_property -dict { PACKAGE_PIN A5 IOSTANDARD LVCMOS33 } [get_ports {io_led[2]}];

set_property -dict { PACKAGE_PIN A4 IOSTANDARD LVCMOS33 } [get_ports {io_led[3]}];

set_property -dict { PACKAGE_PIN B4 IOSTANDARD LVCMOS33 } [get_ports {io_led[4]}];

set_property -dict { PACKAGE_PIN A3 IOSTANDARD LVCMOS33 } [get_ports {io_led[5]}];

set_property -dict { PACKAGE_PIN F4 IOSTANDARD LVCMOS33 } [get_ports {io_led[6]}];

set_property -dict { PACKAGE_PIN F3 IOSTANDARD LVCMOS33 } [get_ports {io_led[7]}];

# LED Bank 2
set_property -dict { PACKAGE_PIN F2 IOSTANDARD LVCMOS33 } [get_ports {io_led[8]}];

set_property -dict { PACKAGE_PIN E1 IOSTANDARD LVCMOS33 } [get_ports {io_led[9]}];

set_property -dict { PACKAGE_PIN B2 IOSTANDARD LVCMOS33 } [get_ports {io_led[10]}];

set_property -dict { PACKAGE_PIN A2 IOSTANDARD LVCMOS33 } [get_ports {io_led[11]}];

set_property -dict { PACKAGE_PIN E2 IOSTANDARD LVCMOS33 } [get_ports {io_led[12]}];

set_property -dict { PACKAGE_PIN D1 IOSTANDARD LVCMOS33 } [get_ports {io_led[13]}];

set_property -dict { PACKAGE_PIN E6 IOSTANDARD LVCMOS33 } [get_ports {io_led[14]}];

set_property -dict { PACKAGE_PIN K5 IOSTANDARD LVCMOS33 } [get_ports {io_led[15]}];

# LED Bank 3
set_property -dict { PACKAGE_PIN G2 IOSTANDARD LVCMOS33 } [get_ports {io_led[16]}];

set_property -dict { PACKAGE_PIN G1 IOSTANDARD LVCMOS33 } [get_ports {io_led[17]}];

set_property -dict { PACKAGE_PIN H2 IOSTANDARD LVCMOS33 } [get_ports {io_led[18]}];

set_property -dict { PACKAGE_PIN H1 IOSTANDARD LVCMOS33 } [get_ports {io_led[19]}];

set_property -dict { PACKAGE_PIN K1 IOSTANDARD LVCMOS33 } [get_ports {io_led[20]}];

set_property -dict { PACKAGE_PIN J1 IOSTANDARD LVCMOS33 } [get_ports {io_led[21]}];

set_property -dict { PACKAGE_PIN L3 IOSTANDARD LVCMOS33 } [get_ports {io_led[22]}];

set_property -dict { PACKAGE_PIN L2 IOSTANDARD LVCMOS33 } [get_ports {io_led[23]}];

# 7 Seg LEDs
# This is numbered as top (A) = 0, center (G) = 6, dot (dp) = 7, Clockwise for all others.
set_property -dict { PACKAGE_PIN T5 IOSTANDARD LVCMOS33 } [get_ports {io_seg[0]}];

set_property -dict { PACKAGE_PIN R5 IOSTANDARD LVCMOS33 } [get_ports {io_seg[1]}];

set_property -dict { PACKAGE_PIN T9 IOSTANDARD LVCMOS33 } [get_ports {io_seg[2]}];

set_property -dict { PACKAGE_PIN R6 IOSTANDARD LVCMOS33 } [get_ports {io_seg[3]}];

set_property -dict { PACKAGE_PIN R7 IOSTANDARD LVCMOS33 } [get_ports {io_seg[4]}];

set_property -dict { PACKAGE_PIN T7 IOSTANDARD LVCMOS33 } [get_ports {io_seg[5]}];

set_property -dict { PACKAGE_PIN T8 IOSTANDARD LVCMOS33 } [get_ports {io_seg[6]}];

set_property -dict { PACKAGE_PIN T10 IOSTANDARD LVCMOS33 } [get_ports {io_seg[7]}];

# 7 Seg Selector
set_property -dict { PACKAGE_PIN P9 IOSTANDARD LVCMOS33 } [get_ports {io_sel[0]}];

set_property -dict { PACKAGE_PIN N9 IOSTANDARD LVCMOS33 } [get_ports {io_sel[1]}];

set_property -dict { PACKAGE_PIN R8 IOSTANDARD LVCMOS33 } [get_ports {io_sel[2]}];

set_property -dict { PACKAGE_PIN P8 IOSTANDARD LVCMOS33 } [get_ports {io_sel[3]}];

# Buttons, Top = 0, Center = 1, Bottom = 2, Left = 3, Right = 4 (From Schematic)
set_property -dict { PACKAGE_PIN C6 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_button[0]}];

set_property -dict { PACKAGE_PIN C7 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_button[1]}];

set_property -dict { PACKAGE_PIN A7 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_button[2]}];

set_property -dict { PACKAGE_PIN B7 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_button[3]}];

set_property -dict { PACKAGE_PIN P11 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_button[4]}];

# Dip switches
# Bank 1
set_property -dict { PACKAGE_PIN C4 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[0]}];

set_property -dict { PACKAGE_PIN D4 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[1]}];

set_property -dict { PACKAGE_PIN G4 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[2]}];

set_property -dict { PACKAGE_PIN G5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[3]}];

set_property -dict { PACKAGE_PIN E5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[4]}];

set_property -dict { PACKAGE_PIN F5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[5]}];

set_property -dict { PACKAGE_PIN D5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[6]}];

set_property -dict { PACKAGE_PIN D6 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[7]}];

# Bank 2
set_property -dict { PACKAGE_PIN N6 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[8]}];

set_property -dict { PACKAGE_PIN M6 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[9]}];

set_property -dict { PACKAGE_PIN B1 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[10]}];

set_property -dict { PACKAGE_PIN C1 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[11]}];

set_property -dict { PACKAGE_PIN C2 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[12]}];

set_property -dict { PACKAGE_PIN C3 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[13]}];

set_property -dict { PACKAGE_PIN D3 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[14]}];

set_property -dict { PACKAGE_PIN E3 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[15]}];

# Bank 3
set_property -dict { PACKAGE_PIN K2 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[16]}];

set_property -dict { PACKAGE_PIN K3 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[17]}];

set_property -dict { PACKAGE_PIN J4 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[18]}];

set_property -dict { PACKAGE_PIN J5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[19]}];

set_property -dict { PACKAGE_PIN H3 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[20]}];

set_property -dict { PACKAGE_PIN J3 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[21]}];

set_property -dict { PACKAGE_PIN H4 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[22]}];

set_property -dict { PACKAGE_PIN H5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_dip[23]}];

set_property -dict { PACKAGE_PIN H5 IOSTANDARD LVCMOS33 PULLDOWN TRUE } [get_ports {io_other}];