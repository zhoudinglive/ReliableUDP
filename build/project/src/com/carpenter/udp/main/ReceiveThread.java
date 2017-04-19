/**
 * 
 */
package com.carpenter.udp.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.carpenter.udp.helper.ByteSolver;
import com.carpenter.udp.helper.RDTBuffer;
import com.carpenter.udp.helper.RDTSegment;
//import com.carpenter.udp.ui.Server;

/**
 * @Title: ReceiveThread.java
 * @Package com.carpenter.udp.main
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月10日 上午11:15:46
 * @version V1.0
 */
public class ReceiveThread extends Thread {
	public static boolean flag = true;
	ConcurrentLinkedDeque<RDTSegment> rcvSeg;
	RDTBuffer sndBuffer, rcvBuffer;
	DatagramSocket socket;
	InetAddress ip;
	int port;

	public ReceiveThread(DatagramSocket socket, RDTBuffer sndBuffer, RDTBuffer rcvBuffer, InetAddress ip, int port,
			ConcurrentLinkedDeque<RDTSegment> rcvSeg) {
		this.socket = socket;
		this.sndBuffer = sndBuffer;
		this.rcvBuffer = rcvBuffer;
		this.ip = ip;
		this.port = port;
		this.rcvSeg = rcvSeg;
	}

	public void run() {
		try {
			byte[] buf = new byte[RDT.MSS + RDTSegment.data_Offset];
			DatagramPacket pkt = new DatagramPacket(buf, RDT.MSS + RDTSegment.data_Offset);
			while (flag) {
				socket.receive(pkt);
				buf = pkt.getData();
				RDTSegment seg = new RDTSegment();
				ByteSolver.makeSegment(seg, buf);

				if (seg.isValid()) {
					//Server.CentralArea2.append("not equal " + seg.computeChecksum() + " " + seg.checkSum);
					continue;
				}

				if (seg.containsACK() && !seg.containsData()) {
					//Server.CentralArea2.append("receive ack = " + seg.ackNum + "\n");
					System.out.println("receive ack = " + seg.ackNum + " snd_base = " + sndBuffer.snd_base + "\n");
					int ack = seg.ackNum;
					sndBuffer.setACK(ack % RDT.MaxBuffSize, true);
					if (ack == sndBuffer.snd_base) {
						sndBuffer.removeACKReceived();
					}

				}
				if (seg.containsData()) {
					if (((rcvBuffer.rcv_base + RDT.MaxBuffSize) % RDT.MaxIndexSize) < rcvBuffer.rcv_base) {
						if (seg.seqNum < ((rcvBuffer.rcv_base + RDT.MaxBuffSize) % RDT.MaxIndexSize)
								|| seg.seqNum >= rcvBuffer.rcv_base) {
							rcvBuffer.putBySeqNum(seg);
							//Server.CentralArea2.append("receive seqNum = " + seg.seqNum + "\n");
							RDTSegment removeSeg;
							while ((removeSeg = rcvBuffer.removeNotNull()) != null) {
								rcvSeg.add(removeSeg);
							}
							ackTrans(seg);
						} else {
							ackTrans(seg);
						}
					} else {
						if (seg.seqNum >= rcvBuffer.rcv_base && seg.seqNum < (rcvBuffer.rcv_base + RDT.MaxBuffSize)) {
							rcvBuffer.putBySeqNum(seg);
							//Server.CentralArea2.append("receive seqNum = " + seg.seqNum + "\n");
							RDTSegment removeSeg;
							while ((removeSeg = rcvBuffer.removeNotNull()) != null) {
								rcvSeg.add(removeSeg);
							}
							ackTrans(seg);
						} else {
							ackTrans(seg);
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//Server.CentralArea2.append("receiverThread error!\n");
		}
	}

	public void ackTrans(RDTSegment seg) throws IOException {
		RDTSegment rcvSeg = new RDTSegment();
		rcvSeg.seqNum = -1;
		rcvSeg.ackNum = seg.seqNum;
		rcvSeg.checkSum = rcvSeg.computeChecksum();
		ByteSolver.udpSend(this.socket, this.ip, this.port, rcvSeg);
		//Server.CentralArea1.append("to sender ack = " + rcvSeg.ackNum + "\n");
	}
}
