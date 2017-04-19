/**
 * 
 */
package com.carpenter.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;

import com.carpenter.http.HttpSegment;
import com.carpenter.http.HttpSegmentSolver;
import com.carpenter.http.TinySeg;
import com.carpenter.udp.main.RDT;

/**
 * @Title: TinyHttpSolver.java
 * @Package com.carpenter.server
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月13日 下午10:39:41
 * @version V1.0
 */
public class TinyHttpSolver extends Thread {
	private static LinkedList<TinySeg> pool = new LinkedList<TinySeg>();
	private RDT rdtSocket;

	public TinyHttpSolver(RDT serverRDT) {
		this.rdtSocket = serverRDT;
	}

	public static void poolReceive(byte[] data, int length) {
		synchronized (pool) {
			TinySeg tinySeg = new TinySeg(data, length);
			pool.add(tinySeg);
			pool.notifyAll();
		}
	}

	public void run() {
		TinySeg tinySeg;
		while (true) {
			
			synchronized (pool) {
				while (pool.isEmpty()) {
					try {
						pool.wait();
					} catch (Exception e) {
						System.out.println("Thread Pool error : " + e);
					}
				}
				tinySeg = pool.removeFirst();
			}

			try {
				HttpSegment httpSeg = HttpSegmentSolver.byteArrayToObject(tinySeg.getData(), HttpSegment.class,
						tinySeg.getLength());
				HttpSegment returnSeg = new HttpSegment();
				if (!httpSeg.getHost().equals("www.webTest.com")) {
					System.out.println(1);
					badRequestHandler();
					return;
				}
				if (httpSeg != null && httpSeg.getRequestMethod() != null && httpSeg.getRequestURL() != null) {
					if (httpSeg.getRequestMethod().equals(HttpSegment.GET)) {
						String Url = httpSeg.getRequestURL();
						if (Url.matches("(.*)Index3.html(.*)")) {
							System.out.println(2);
							badRequestHandler();
							return;
						}
						byte[] tmp = Files.readAllBytes(Paths.get("../../../resources" + Url));
						acceptRequest(tmp);
					} else if (httpSeg.getRequestMethod().equals(HttpSegment.POST)) {
						String Url = httpSeg.getRequestURL();
						if (Url.matches("(.*)Index3.html(.*)")) {
							byte[] tmp = Files.readAllBytes(Paths.get("../../../resources" + Url));
							acceptRequest(tmp);
						} else {
							System.out.println(3);
							badRequestHandler();
							return;
						}
					}
				} else {
					System.out.println(4);
					badRequestHandler();
					return;
				}
			} catch (Exception e) {
				System.out.println(5);
				e.printStackTrace();
				badRequestHandler();
				return;
			}
		}
	}

	private void acceptRequest(byte[] data) {
		HttpSegment returnSeg = new HttpSegment();
		returnSeg.setData(new String(data));
		returnSeg.setVersion(HttpSegment.Version);
		returnSeg.setState("200");
		returnSeg.setDescribe("OK");
		returnSeg.setServer(HttpSegment.Server);
		returnSeg.setContentLength(String.valueOf(data.length));
		returnSeg.setContentType("text/html;");
		synchronized (rdtSocket) {
			System.out.println("http response");
			byte[] tmp = HttpSegmentSolver.toByteArray(returnSeg);
			rdtSocket.send(tmp, tmp.length);
			rdtSocket.notifyAll();
		}
	}

	private void badRequestHandler() {
		HttpSegment returnSeg = new HttpSegment();
		returnSeg.setState("404");
		returnSeg.setDescribe("Not Found");
		returnSeg.setServer(HttpSegment.Server);
		returnSeg.setVersion(HttpSegment.Version);
		returnSeg.setContentLength("0");
		returnSeg.setContentType("text/html;");
		synchronized (rdtSocket) {
			byte[] tmp = HttpSegmentSolver.toByteArray(returnSeg);
			rdtSocket.send(tmp, tmp.length);
			rdtSocket.notifyAll();
		}
	}


}
