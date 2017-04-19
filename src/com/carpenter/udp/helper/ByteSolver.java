/**
 * 
 */
package com.carpenter.udp.helper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.carpenter.udp.main.RDT;
//import com.carpenter.udp.ui.Server;

/**
 * @Title: ByteSolver.java
 * @Package com.carpenter.udp.helper
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月3日 下午12:04:56
 * @version V1.0
 */
public class ByteSolver {
	public static void intToByte(int intValue, byte[] data, int index) {
		data[index++] = (byte) ((intValue & 0xFF000000) >> 24);
		data[index++] = (byte) ((intValue & 0x00FF0000) >> 16);
		data[index++] = (byte) ((intValue & 0x0000FF00) >> 8);
		data[index] = (byte) (intValue & 0x000000FF);
	}

	public static int byteToInt(byte[] data, int index) {
		int intValue = 0, intTmp = 0;
		for (int i = 0; i < 4; ++i) {
			if (((int) data[index]) < 0) {
				intTmp = (0x0000007F & ((int) data[index]));
				intTmp += 128;
			} else {
				intTmp = (0x000000FF & ((int) data[index]));
			}
			index++;
			intValue |= intTmp;
			if (i != 3)
				intValue <<= 8;
		}
		return intValue;
	}

	public static void makeSegment(RDTSegment seg, byte[] recData) {
		seg.seqNum = byteToInt(recData, RDTSegment.seqNum_Offset);
		seg.ackNum = byteToInt(recData, RDTSegment.ackNum_Offset);
		seg.checkSum = byteToInt(recData, RDTSegment.checkNum_Offset);
		seg.length = byteToInt(recData, RDTSegment.length_Offset);
		for (int i = 0; i < seg.length; ++i) {
			seg.data[i] = recData[RDTSegment.data_Offset + i];
		}
	}

	public static synchronized void udpSend(DatagramSocket socket, InetAddress ip, int port, RDTSegment seg) {
		double d = RDT.random.nextDouble();
		if (d < RDT.lossRate) {
			if (seg.seqNum == -1) {
				RDT.ackLostSegNum++;
				System.out.println(System.currentTimeMillis() + ": ack send lost " + seg.ackNum + "\n");
				//Server.CentralArea1.append(System.currentTimeMillis() + ": ack send lost " + seg.ackNum + "\n");
			} else {
				RDT.dataLostSegNum++;
				System.out.println(System.currentTimeMillis() + ": udp send lost " + seg.seqNum + " segment!\n");
				//Server.CentralArea1
						//.append(System.currentTimeMillis() + ": udp send lost " + seg.seqNum + " segment!\n");
			}
			return;
		}
		int paySize = seg.length + RDTSegment.data_Offset;
		byte[] tmp = new byte[paySize];
		if (seg.seqNum == -1) {
			seg.makeByteStream(tmp, false);
		} else {
			seg.makeByteStream(tmp, true);
		}

		int delay = RDT.random.nextInt(50);
		try {
			Thread.sleep(delay);
			socket.send(new DatagramPacket(tmp, paySize, ip, port));
			RDT.sndSegNum++;
			
		} catch (Exception e) {
			// Server.CentralArea1.append("udp send error!\n");
			e.printStackTrace();
			return;
		}
	}

}
