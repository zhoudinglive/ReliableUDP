/**
 * 
 */
package com.carpenter.http;

/**
 * @Title: TinySeg.java
 * @Package com.carpenter.http
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月13日 下午11:29:08
 * @version V1.0
 */
public class TinySeg {
	private int length;
	private byte[] data;

	public TinySeg(byte[] data, int length) {
		if(length > data.length) {
			System.out.println("length not right");
			return;
		}
		this.setData(data);
		this.setLength(length);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
