package com.alchitry.labs.hardware.ftdi;

import java.util.LinkedList;
import java.util.Queue;

public enum JtagState {
	TEST_LOGIC_RESET, RUN_TEST_IDLE, SELECT_DR_SCAN, CAPTURE_DR, SHIFT_DR, EXIT1_DR, PAUSE_DR, EXIT2_DR, UPDATE_DR, SELECT_IR_SCAN, CAPTURE_IR, SHIFT_IR, EXIT1_IR, PAUSE_IR, EXIT2_IR, UPDATE_IR;

	public static class Transistions {
		private JtagState currentState;
		public int tms;
		public int moves;

		public Transistions() {
			currentState = JtagState.TEST_LOGIC_RESET;
			tms = 0;
			moves = 0;
		}

		public Transistions(Transistions t) {
			currentState = t.currentState;
			tms = t.tms;
			moves = t.moves;
		}
		
		public String toString() {
			String str = Integer.toBinaryString(tms);
			return str.substring(str.length()-moves, str.length());
		}
	}

	public JtagState next(boolean tms) {
		switch (this) {
		case TEST_LOGIC_RESET:
			return tms ? JtagState.TEST_LOGIC_RESET : JtagState.RUN_TEST_IDLE;
		case RUN_TEST_IDLE:
			return tms ? JtagState.SELECT_DR_SCAN : JtagState.RUN_TEST_IDLE;
		case SELECT_DR_SCAN:
			return tms ? JtagState.SELECT_IR_SCAN : JtagState.CAPTURE_DR;
		case CAPTURE_DR:
			return tms ? JtagState.EXIT1_DR : JtagState.SHIFT_DR;
		case SHIFT_DR:
			return tms ? JtagState.EXIT1_DR : JtagState.SHIFT_DR;
		case EXIT1_DR:
			return tms ? JtagState.UPDATE_DR : JtagState.PAUSE_DR;
		case PAUSE_DR:
			return tms ? JtagState.EXIT2_DR : JtagState.PAUSE_DR;
		case EXIT2_DR:
			return tms ? JtagState.UPDATE_DR : JtagState.SHIFT_DR;
		case UPDATE_DR:
			return tms ? JtagState.SELECT_DR_SCAN : JtagState.RUN_TEST_IDLE;
		case SELECT_IR_SCAN:
			return tms ? JtagState.TEST_LOGIC_RESET : JtagState.CAPTURE_IR;
		case CAPTURE_IR:
			return tms ? JtagState.EXIT1_IR : JtagState.SHIFT_IR;
		case SHIFT_IR:
			return tms ? JtagState.EXIT1_IR : JtagState.SHIFT_IR;
		case EXIT1_IR:
			return tms ? JtagState.UPDATE_IR : JtagState.PAUSE_IR;
		case PAUSE_IR:
			return tms ? JtagState.EXIT2_IR : JtagState.PAUSE_IR;
		case EXIT2_IR:
			return tms ? JtagState.UPDATE_IR : JtagState.SHIFT_IR;
		case UPDATE_IR:
			return tms ? JtagState.SELECT_DR_SCAN : JtagState.RUN_TEST_IDLE;
		}

		return JtagState.TEST_LOGIC_RESET;
	}
	
	public Transistions getTransitions(JtagState end) {
		return getTransitions(this, end);
	}

	public static Transistions getTransitions(JtagState start, JtagState end) {
		Queue<Transistions> queue = new LinkedList<>();
		Transistions t = new Transistions();
		t.currentState = start;
		t.moves = 0;
		t.tms = 0;
		queue.add(t);
		while (!queue.isEmpty()) {
			t = queue.remove();
			if (t.currentState == end)
				break;

			JtagState s0 = t.currentState.next(false);
			JtagState s1 = t.currentState.next(true);

			t.moves++;
			t.tms &= ~(1 << (t.moves - 1));
			t.currentState = s0;
			queue.add(new Transistions(t));

			t.tms |= (1 << (t.moves - 1));
			t.currentState = s1;
			queue.add(t);
		}
		return t;
	}
}
