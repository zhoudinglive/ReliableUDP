/**
 * 
 */
package com.carpenter.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.carpenter.http.HttpSegment;
import com.carpenter.http.HttpSegmentSolver;
import com.carpenter.udp.main.RDT;

/**   
* @Title: TinyClient.java 
* @Package com.carpenter.server 
* @Description: TODO 
* @author carpenter   
* @date 2016年12月14日 下午3:09:50 
* @version V1.0   
*/
public class TinyClient {
	public static void main(String[] args) throws UnknownHostException, Exception {
		RDT rdtSocket = new RDT(InetAddress.getLocalHost(), 8000, 8001);
		rdtSocket.setLossRate(0.2);
		HttpSegment requestSeg = new HttpSegment();
		requestSeg.setRequestMethod(HttpSegment.POST);
		requestSeg.setRequestURL("//TinyHttp//Index3.html");
		requestSeg.setVersion(HttpSegment.Version);
		requestSeg.setHost("www.webTest.com");
		byte[] tmp = HttpSegmentSolver.toByteArray(requestSeg);
		rdtSocket.send(tmp, tmp.length);
		byte[] data = new byte[RDT.MSS];
		int size = 0;
		while(size == 0){
			size = rdtSocket.receive(data, RDT.MSS);		
		}
		HttpSegment responseSeg = HttpSegmentSolver.byteArrayToObject(data, HttpSegment.class, size);
		System.out.println(responseSeg.getState());
		System.out.println(responseSeg.getDescribe());
		System.out.println(responseSeg.getData());
	}
}
