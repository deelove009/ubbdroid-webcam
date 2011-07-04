package com.android.mw;

import java.io.Serializable;

public class Package implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int type;
	private String data;
	public Package(int type, String data) {
		super();
		this.type = type;
		this.data = data;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
