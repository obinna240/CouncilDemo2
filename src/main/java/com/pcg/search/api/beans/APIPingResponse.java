package com.pcg.search.api.beans;

public class APIPingResponse {
	
	private long counter;
	private String value;

	public APIPingResponse(long c, String v) {
		
		setCounter(c);
		setValue(v);
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
