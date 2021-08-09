package com.alchitry.labs.widgets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class TabTransfer extends ByteArrayTransfer {
	private static final String MYTYPENAME = "custom_tabs_transfer";

	private static final int MYTYPEID = registerType(MYTYPENAME);

	private static TabTransfer _instance = new TabTransfer();

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
		} catch (IOException e) {
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			Integer index = Integer.valueOf(-1);
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
		if (object == null || !(object instanceof Integer)) {
			return false;
		}
		return true;
	}

	@Override
	protected boolean validate(Object object) {
		return checkStringType(object);
	}
}