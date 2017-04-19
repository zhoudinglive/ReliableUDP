/**
 * 
 */
package com.carpenter.udp.helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import com.carpenter.udp.main.RDT;

/**
 * @Title: TimeoutHandler.java
 * @Package com.carpenter.udp.helper
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月3日 下午1:13:23
 * @version V1.0
 */
public class TimeoutHandler extends TimerTask {
	private RDTBuffer sndBuff;
	private RDTSegment seg;
	private DatagramSocket socket;
	private InetAddress ip;
	private int port;

	public TimeoutHandler(RDTBuffer sndBuff, RDTSegment seg, DatagramSocket socket, InetAddress ip, int port) {
		this.sndBuff = sndBuff;
		this.seg = seg;
		this.socket = socket;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void run() {
		if (seg.ackReceived)
			return;
		// System.out.println(System.currentTimeMillis() + ": Timeout for seg: "
		// + seg.seqNum);
		byte[] tmp = new byte[RDTSegment.data_Offset + seg.length];
		seg.makeByteStream(tmp, true);
		try {
			Thread.sleep(50);
			socket.send(new DatagramPacket(tmp, tmp.length, ip, port));
			System.out.println("Retransmit the segment " + seg.seqNum);
			RDT.retranSegNum++;
		} catch (Exception e) {
			System.out.println("Timeout retransmit" + seg.seqNum + "failed!");
		}

	}

}
