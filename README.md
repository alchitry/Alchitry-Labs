# Lucid HDL

> Lucid is an HDL made specifically for FPGAs and is designed to remove many of the pitfalls that are common with other HDLs like Verilog and VHDL.

This forked repository provides a program that allows you to run only the Lucid HDL to Verilog conversion part from the Alchitry-Labs IDE in the console.
The program has been tested on a macOS, but I believe it will work on Windows and Linux as well.

Initially, I was going to run the screen on macOS, but gave up because of problems with the macOS version of SWT. :innocent:

## Getting Started

### Build a Fat JAR With Gradle
```
gradle build
```

### Run With Gradle
```
gradle run --args="<alp-file>"
```

### Run Executable JAR
```
java -XstartOnFirstThread -jar build/libs/lucid-hdl.jar <alp-file>
```

## Customize Environment

To run the program from another folder, you need to set up a home directory that contains the library folder.
Program will use env LUCIDHDL_HOME if it is set, otherwise it will use the current folder.

## Links

- [Programming an FPGA - lucid](https://learn.sparkfun.com/tutorials/programming-an-fpga/lucid)
- [Lucid - FPGA Tutorials](https://alchitry.com/pages/lucid-fpga-tutorials)
