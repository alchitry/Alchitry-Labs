<?xml version="1.0" encoding="UTF-8"?>
<project name="SDRAM Tester" board="Mojo V3" language="Lucid">
  <files>
    <src>ram_test.luc</src>
    <src top="true">mojo_top.luc</src>
    <ucf lib="true">sdram_shield.ucf</ucf>
    <ucf lib="true">mojo.ucf</ucf>
    <component>pn_gen.luc</component>
    <component>sdram.luc</component>
    <component>reset_conditioner.luc</component>
    <component>memory_bus.luc</component>
    <core name="clk_wiz">
      <src>clk_wiz.v</src>
    </core>
  </files>
</project>
