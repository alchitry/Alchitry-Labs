package com.alchitry.labs.parsers.lucid.toVerilog;

public class ReservedWords {

	public static boolean check(String s) {
		switch (s) {
		case "always":
		case "assign":
		case "automatic":
		case "begin":
		case "case":
		case "casex":
		case "casez":
		case "cell":
		case "config":
		case "deassign":
		case "default":
		case "defparam":
		case "design":
		case "disable":
		case "edge":
		case "else":
		case "end":
		case "endcase":
		case "endconfig":
		case "endfunction":
		case "endgenerate":
		case "endmodule":
		case "endprimitive":
		case "endspecify":
		case "endtable":
		case "endtask":
		case "event":
		case "for":
		case "force":
		case "foreve":
		case "fork":
		case "function":
		case "generate":
		case "genvar":
		case "if":
		case "ifnone":
		case "incdir":
		case "include":
		case "initial":
		case "inout":
		case "instance":
		case "join":
		case "liblist":
		case "library":
		case "localparam":
		case "macromodule":
		case "module":
		case "negedge":
		case "noshowcancelled":
		case "output":
		case "parameter":
		case "posedge":
		case "primitive":
		case "pulsestyle_ondetect":
		case "pulsestyle_onevent":
		case "reg":
		case "release":
		case "repeat":
		case "scalared":
		case "showcancelled":
		case "signed":
		case "specify":
		case "specparam":
		case "strength":
		case "table":
		case "task":
		case "tri":
		case "tri0":
		case "tri1":
		case "triand":
		case "wand":
		case "trior":
		case "wor":
		case "trireg":
		case "unsigned":
		case "use":
		case "vectored":
		case "wait":
		case "while":
		case "wire":
			return true;
		}
		return false;
	}

}
