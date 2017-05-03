/**
 * 
 */
package com.carpenter.udp.helper;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.carpenter.udp.main.RDT;
//import com.carpenter.udp.ui.Server;

/**
 * @Title: RDTBuffer.java
 * @Package com.carpenter.udp.helper
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月3日 下午1:15:36
 * @version V1.0
 */
public class RDTBuffer {
	RDTSegment[] buffSeg;
	int buffSize;
	int base;
	public int snd_base;
	public int rcv_base;
	private int next;
	private Semaphore semEmpty;

	public RDTBuffer(int buffSize) throws FileNotFoundException {
		this.buffSize = buffSize;
		this.buffSeg = new RDTSegment[buffSize];
		for (int i = 0; i < buffSize; ++i)
			this.buffSeg[i] = null;
		this.base = next = snd_base = rcv_base = 0;
		this.semEmpty = new Semaphore(buffSize, true);
	}

	public void putNext(RDTSegment seg) {
		try {
			semEmpty.acquire();
			System.out.println("at i = " + (next % buffSize) + " save " + seg.seqNum);
			synchronized (buffSeg) {
				buffSeg[next % buffSize] = seg;
			}
			next++;
		} catch (Exception e) {
			System.out.println("Buffer put exception:" + e);
			e.printStackTrace();
		}
	}

	public void putBySeqNum(RDTSegment seg) {
		try {
			synchronized (buffSeg) {
				buffSeg[seg.seqNum % buffSize] = seg;
			}
		} catch (Exception e) {
			// Server.CentralArea1.append("putBySeqNum error!");
		}
	}

	public void removeACKReceived() throws IOException {
		synchronized (buffSeg) {
			while (buffSeg[base] != null && buffSeg[base].ackReceived) {
				System.out.println("base = " + base + " snd_ base = " + snd_base);
				System.out.println("remove seqNum = " + buffSeg[base].seqNum + " length = " + buffSeg[base].length);
				buffSeg[base] = null;
				base = (base + 1) % buffSize;
				snd_base = (snd_base + 1) % RDT.MaxIndexSize;
				semEmpty.release();
			}
		}
	}

	public RDTSegment removeNotNull() {
		synchronized (buffSeg) {
			RDTSegment forReturn = null;
			if (buffSeg[base] != null) {
				forReturn = buffSeg[base];
				buffSeg[base] = null;
				base = (base + 1) % buffSize;
				rcv_base = (rcv_base + 1) % RDT.MaxIndexSize;
			}
			return forReturn;
		}
	}

	public void setACK(int offset, boolean flag) {
		synchronized (buffSeg) {
			if (buffSeg[offset] != null) {
				buffSeg[offset].ackReceived = flag;
				if(flag){
					RDT.rcvSegNum++;
				}
				
			}
			
		}
	}

}
