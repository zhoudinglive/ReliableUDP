/**
 * 
 */
package com.carpenter.udp.main;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Title: JustTest.java
 * @Package com.carpenter.udp.main
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月4日 下午5:42:48
 * @version V1.0
 */
public class JustTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		FileOutputStream out = new FileOutputStream("F:\\西部世界.rmvb");
		BufferedOutputStream out2 = new BufferedOutputStream(out);
		DataOutputStream out3 = new DataOutputStream(out2);
		RDT server = new RDT(InetAddress.getLocalHost(), 8000, 8001);
		byte[] data = new byte[RDT.MSS];
		boolean flag = true;
		while (true) {
			int size = server.receive(data, RDT.MSS);
			if (size != 0) {
				// for(int i =0;i<size;++i){
				// System.out.print(data[i]+" ");
				//
				// }
				// System.out.println();
				out.write(data, 0, size);
				// out.flush();
				// flag = false;
			}
		}

	}

}
