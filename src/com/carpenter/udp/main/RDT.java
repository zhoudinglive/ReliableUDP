/**
 * 
 */
package com.carpenter.udp.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.carpenter.udp.helper.ByteSolver;
import com.carpenter.udp.helper.RDTBuffer;
import com.carpenter.udp.helper.RDTSegment;
import com.carpenter.udp.helper.TimeoutHandler;
//import com.carpenter.udp.ui.Server;

/**
 * @Title: RDT.java
 * @Package com.carpenter.udp.main
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月3日 下午1:15:14
 * @version V1.0
 */
public class RDT {
	public static final int MSS = 60000;
	public static final int RetransmitTimeout = 200;
	public static final int MaxBuffSize = 50;
	public static final int MaxIndexSize = 100;

	public static double lossRate = 0.0;
	public static Random random = new Random();
	public static Timer timer = new Timer();
	public static boolean done = false;
	public static int sndSegNum = 0, rcvSegNum = 0, ackLostSegNum = 0, retranSegNum = 0, dataLostSegNum = 0;

	private DatagramSocket socket;
	private InetAddress dstIp;
	private int dstPort;
	private int localPort;
	public int index = 0;

	private RDTBuffer sndBuff;
	private RDTBuffer rcvBuff;
	public ReceiverThread recThread;
	private ConcurrentLinkedDeque<RDTSegment> rcvSeg;

	public RDT(InetAddress dstIp, int dstPort, int localPort) throws Exception {
		this.localPort = localPort;
		this.dstPort = dstPort;
		this.dstIp = dstIp;
		this.socket = new DatagramSocket(localPort);
		this.sndBuff = new RDTBuffer(MaxBuffSize);
		this.rcvBuff = new RDTBuffer(MaxBuffSize);
		this.rcvSeg = new ConcurrentLinkedDeque<RDTSegment>();
		this.recThread = new ReceiverThread(socket, sndBuff, rcvBuff, dstIp, dstPort, rcvSeg);
		this.recThread.start();
	}

	public void setLossRate(double lossRate) {
		if (lossRate < 0 || lossRate > 0.8) {
			System.out.println("lossRate set error!\n");
			return;
		}
		this.lossRate = lossRate;
	}

	public int send(byte[] data, int size) {
		RDTSegment seg = new RDTSegment();
		seg.length = size;
		seg.data = data.clone();
		seg.seqNum = index % RDT.MaxIndexSize;
		index++;
		seg.checkSum = seg.computeChecksum();
		seg.timeoutHandler = new TimeoutHandler(sndBuff, seg, socket, dstIp, dstPort);
		sndBuff.putNext(seg);
		//Server.CentralArea1.append(System.currentTimeMillis() + ": send seg: " + seg.seqNum + "\n");
		ByteSolver.udpSend(socket, dstIp, dstPort, seg);
		System.out.println(System.currentTimeMillis() + "send" + seg.seqNum + "\n");
		timer.schedule(seg.timeoutHandler, 0, RetransmitTimeout);
		return size;
	}

	public int receive(byte[] data, int size) {
		try {
			if (!rcvSeg.isEmpty()) {
				RDTSegment receive = rcvSeg.pop();
				//Server.CentralArea2.append("receive run = " + receive.seqNum + " data_size = " + receive.length + "\n");
				System.out.println("receive run = " + receive.seqNum + " data_size = " + receive.length + "\n");
				for (int i = 0; i < receive.length; ++i) {
					data[i] = receive.data[i];
				}
				return receive.length;
			}
			return 0;
		} catch (Exception e) {
			//Server.CentralArea2.append("data receive failed!\n");
			//e.printStackTrace();
			return 0;
		}
	}
}

class ReceiverThread extends Thread {
	ConcurrentLinkedDeque<RDTSegment> rcvSeg;
	RDTBuffer sndBuffer, rcvBuffer;
	DatagramSocket socket;
	InetAddress ip;
	int port;
	

	public ReceiverThread(DatagramSocket socket, RDTBuffer sndBuffer, RDTBuffer rcvBuffer, 
			InetAddress ip, int port, ConcurrentLinkedDeque<RDTSegment> rcvSeg) {
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
			while (true) {
				socket.receive(pkt);
				buf = pkt.getData();
				RDTSegment seg = new RDTSegment();
				ByteSolver.makeSegment(seg, buf);

				if (seg.isValid()) {
					System.out.println("not equal" + seg.computeChecksum() + " " + seg.checkSum);
					continue;
				}

				if (seg.containsACK() && !seg.containsData()) {
					System.out.println("receive ack = " + seg.ackNum);
					int ack = seg.ackNum;
					sndBuffer.setACK(ack % RDT.MaxBuffSize, true);
					if (ack == sndBuffer.snd_base) {
						sndBuffer.removeACKReceived();
					}
				}
				System.out.println("arrival seqNum = " + seg.seqNum + " rcv_base= " + rcvBuffer.rcv_base);
				if (seg.containsData()) {
					if (((rcvBuffer.rcv_base + RDT.MaxBuffSize) % RDT.MaxIndexSize) < rcvBuffer.rcv_base) {
						if (seg.seqNum < ((rcvBuffer.rcv_base + RDT.MaxBuffSize) % RDT.MaxIndexSize)
								|| seg.seqNum >= rcvBuffer.rcv_base) {
							rcvBuffer.putBySeqNum(seg);
//							System.out.println("+++++++++++++++++++++++++++++++++");
//							System.out.println("receive seqNum = " + seg.seqNum);
//							System.out.println("+++++++++++++++++++++++++++++++++");
							RDTSegment removeSeg;
							while((removeSeg = rcvBuffer.removeNotNull()) != null){
								rcvSeg.add(removeSeg);
							}
							ackTrans(seg);
						} else {
							ackTrans(seg);
						}
					} else {
						if (seg.seqNum >= rcvBuffer.rcv_base && seg.seqNum < (rcvBuffer.rcv_base + RDT.MaxBuffSize)) {
							rcvBuffer.putBySeqNum(seg);
//							System.out.println("+++++++++++++++++++++++++++++++++");
//							System.out.println("receive seqNum = " + seg.seqNum);
//							System.out.println("+++++++++++++++++++++++++++++++++");
							RDTSegment removeSeg;
							while((removeSeg = rcvBuffer.removeNotNull()) != null){
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
			System.out.println("receiverThread error!");
			e.printStackTrace();
		}
	}

	public void ackTrans(RDTSegment seg) throws IOException {
		RDTSegment rcvSeg = new RDTSegment();
		rcvSeg.seqNum = -1;
		rcvSeg.ackNum = seg.seqNum;
		rcvSeg.checkSum = rcvSeg.computeChecksum();
		ByteSolver.udpSend(this.socket, this.ip, this.port, rcvSeg);
	}
}

