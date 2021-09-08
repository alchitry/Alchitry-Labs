package com.alchitry.labs.widgets;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import java.io.*;

public class TabTransfer extends ByteArrayTransfer {
	private static final String MYTYPENAME = "custom_tabs_transfer";

	private static final int MYTYPEID = registerType(MYTYPENAME);

	private static final TabTransfer _instance = new TabTransfer();

	public static TabTransfer getInstance() {
		return _instance;
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (!checkStringType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		Integer index = (Integer) object;
		try {
			// write data to a byte array and then ask super to convert to pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream writeOut = new DataOutputStream(out);
			writeOut.writeInt(index);

			byte[] buffer = out.toByteArray();
			writeOut.close();
			super.javaToNative(buffer, transferData);
		} catch (IOException ignored) {
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			int index = -1;
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);

				index = readIn.readInt();

				readIn.close();
			} catch (IOException ex) {
				return null;
			}
			return index;
		}

		return null;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MYTYPENAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MYTYPEID };
	}

	boolean checkStringType(Object object) {
		return object instanceof Integer;
	}

	@Override
	protected boolean validate(Object object) {
		return checkStringType(object);
	}
}