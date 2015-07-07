package com.ssmomonga.ssflicker.etc;

public class TAT {
	
	private long time;
	
	public TAT() {
		time = System.currentTimeMillis();
	}

	public long tat() {
		long newTime = System.currentTimeMillis();
		long tat = newTime - time;
		time = newTime;
		return tat;
	}

}