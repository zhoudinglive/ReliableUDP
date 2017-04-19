/**
 * 
 */
package com.carpenter.udp.helper;

import com.carpenter.udp.main.RDT;

/**
 * @Title: RDTSegment.java
 * @Package com.carpenter.udp.helper
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月3日 上午11:41:45
 * @version V1.0
 */
public class RDTSegment {
	public int seqNum;
	public int ackNum;
	public int checkSum;
	public int length;
	public byte[] data;

	public boolean ackReceived;

	public TimeoutHandler timeoutHandler;

	public static final int seqNum_Offset = 0;
	public static final int ackNum_Offset = 4;
	public static final int checkNum_Offset = 8;
	public static final int length_Offset = 12;
	public static final int data_Offset = 16;

	public RDTSegment() {
		this.seqNum = 0;
		this.ackNum = -1;
		this.checkSum = 0;
		this.length = 0;
		this.data = new byte[RDT.MSS];
		this.ackReceived = false;
	}

	public boolean containsACK() {
		if (ackNum != -1)
			return true;
		return false;
	}

	public boolean containsData() {
		if (length != 0)
			return true;
		return false;
	}

	public int computeChecksum() {
		byte checksum = 0;
		byte[] seg = new byte[16];
		ByteSolver.intToByte(seqNum, seg, seqNum_Offset);
		ByteSolver.intToByte(ackNum, seg, ackNum_Offset);
		ByteSolver.intToByte(0, seg, checkNum_Offset);
		ByteSolver.intToByte(length, seg, length_Offset);
		for (int i = 0; i < seg.length; ++i) {
			checksum ^= computeChecksumByByte(seg[i]);
		}
		return checksum;
	}

	private byte computeChecksumByByte(Byte b) {
		byte checksum = 0;
		checksum = (byte) ((byte) (b & 0x1) ^ (byte) ((b >> 1) & 0x1) ^ (byte) ((b >> 3) & 0x1)
				^ (byte) ((b >> 3) & 0x1) ^ (byte) ((b >> 4) & 0x1) ^ (byte) ((b >> 5) & 0x1) ^ (byte) ((b >> 6) & 0x1)
				^ (byte) ((b >> 7) & 0x1));
		return checksum;
	}

	public boolean isValid() {
		if (checkSum != this.computeChecksum()) {
			return true;
		}
		return false;
	}

	public void makeByteStream(byte[] seg, boolean flag) {
		ByteSolver.intToByte(seqNum, seg, seqNum_Offset);
		ByteSolver.intToByte(ackNum, seg, ackNum_Offset);
		ByteSolver.intToByte(checkSum, seg, checkNum_Offset);
		ByteSolver.intToByte(length, seg, length_Offset);
		if (flag) {
			for (int i = 0; i < length; ++i) {
				seg[data_Offset + i] = data[i];
			}
		}
	}

	public void printHeader() {
		System.out.println("seqNum = " + seqNum);
		System.out.println("ackNum = " + ackNum);
		System.out.println("checkSum = " + checkSum);
		System.out.println("length = " + length);
		System.out.println("ackReceived = " + ackReceived);
	}

	public void printData() {
		System.out.println("Data :");
		for (int i = 0; i < length; ++i) {
			System.out.print(data[i] + " ");
		}
		System.out.println();
	}

}
