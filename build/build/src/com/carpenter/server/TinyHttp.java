/**
 * 
 */
package com.carpenter.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.carpenter.udp.main.RDT;

/**   
* @Title: TinyHttp.java 
* @Package com.carpenter.server 
* @Description: TODO 
* @author carpenter   
* @date 2016年12月13日 下午10:28:33 
* @version V1.0   
*/
public class TinyHttp extends Thread{
	private int dstPort;
	private int localPort;
	private InetAddress dstIp;
	private RDT rdtSocket;
	private int solverNum = 50;
	
	public TinyHttp(int dstPort, InetAddress dstIp, int localPort) {
		this.dstIp = dstIp;
		this.dstPort = dstPort;
		this.localPort = localPort;
		try {
			this.rdtSocket = new RDT(dstIp, dstPort, localPort);
			rdtSocket.setLossRate(0.2);
		} catch (Exception e) {
			System.out.println("RDT scoket error" + e);
			return;
		}
	}
	
	public void run() {
		//for(int i = 0; i < solverNum; ++i){
			Thread tmp = new TinyHttpSolver(rdtSocket);
			tmp.start();
	//	}
		
		System.out.println("Accepting request on port : " + localPort);
		byte[] data = new byte[RDT.MSS];
		while(true) {
			
			int size = rdtSocket.receive(data, RDT.MSS);
			if(size == 0)
				continue;
			System.out.println("送入线程处理池...");
			TinyHttpSolver.poolReceive(data, size);
		}
	}
	
	public static void main(String[] args) throws UnknownHostException {
		Thread tinyHttp = new TinyHttp(8001, InetAddress.getLocalHost(), 8000);
		tinyHttp.start();
	}
	
}
