package com.alchitry.labs.lucid;

import java.io.Serializable;

import org.antlr.v4.runtime.ParserRuleContext;

import com.alchitry.labs.language.ConstValue;
import com.alchitry.labs.lucid.parser.LucidParser.ConnectionContext;

public class Connection implements Serializable {
	private static final long serialVersionUID = 4176189071613372727L;
	public final String port;
	public final String signal;
	public final boolean param;
	public final ConstValue value;
	transient public final ParserRuleContext portNode;
	transient public final ParserRuleContext signalNode;
	transient public final ConnectionContext connectionNode;

	public Connection(String port, String signal, boolean param, ConstValue cv) {
		this(port, signal, param, cv, null, null, null);
	}

	public Connection(String port, String signal, boolean param, ConstValue cv, ParserRuleContext portN, ParserRuleContext sigN, ConnectionContext conN) {
		this.port = port;
		this.signal = signal;
		this.param = param;
		value = cv;
		portNode = portN;
		signalNode = sigN;
		connectionNode = conN;
	}

	public String toString() {
		return "." + port + "(" + signal + ") is parameter, " + param;
	}
}
