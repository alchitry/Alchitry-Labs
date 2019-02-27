package com.alchitry.labs.parsers.lucid;

import java.io.Serializable;
import java.util.ArrayList;

import com.alchitry.labs.Named;
import com.alchitry.labs.Util;

public class Struct implements Named, Serializable {
	private static final long serialVersionUID = -1009958205793745457L;
	private String name;
	private ArrayList<Member> members;

	public static class Member implements Named, Serializable {
		private static final long serialVersionUID = 5734287146840459803L;
		public String name;
		public SignalWidth width;
		
		public Member() {
			
		}

		public Member(String name) {
			this.name = name;
			width = new SignalWidth();
		}

		@Override
		public String getName() {
			return name;
		}
		
		public void setName(String n){
			name = n;
		}
		
		public SignalWidth getWidth() {
			return width;
		}
		
		public void setWidth(SignalWidth w){
			width = w;
		}

		@Override
		public String toString() {
			return name + " " + width.toString();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Member) {
				Member m = (Member) o;
				if (!name.equals(m.name))
					return false;
				if (!width.equals(m.width))
					return false;
				return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int h = 0;
			h ^= name.hashCode();
			h ^= width.hashCode();
			return h;
		}
	}

	public Struct(String name) {
		this.name = name;
		members = new ArrayList<>();
	}

	public Struct(Struct s) {
		name = s.name;
		members = new ArrayList<>(s.members);
	}
	
	public Struct() {
		
	}
	
	public void setName(String n){
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	public void addMember(Member m) {
		members.add(m);
	}
	
	public void setMembers(ArrayList<Member> m){
		members = m;
	}
	
	public ArrayList<Member> getMembers() {
		return members;
	}

	public void removeMember(Member m) {
		members.remove(m);
	}
	
	public SignalWidth getWidthOfMember(String name){
		Member m = Util.getByName(members, name);
		if (m != null)
			return m.width;
		return null;
	}
	
	public int getOffsetOfMember(String name) {
		int i = Util.findByName(members, name);
		if (i >= 0) {
			int offset = 0;
			for (int j = 0; j < i; j++){
				offset += members.get(j).width.getTotalWidth();
			}
			return offset;
		}
		return -1;
	}

	public int getWidth() {
		int bits = 0;
		for (Member m : members) {
			bits += m.width.getTotalWidth();
		}
		return bits;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("struct ").append(name).append(" {");
		boolean first = true;
		for (Member m : members) {
			if (!first)
				sb.append(", ");
			first = false;
			sb.append(m.toString());
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Struct) {
			Struct s = (Struct) o;
			if (!name.equals(s.name))
				return false;
			if (members.equals(s.members))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int h = 0;
		h ^= name.hashCode();
		h ^= members.hashCode();
		return h;
	}

}
