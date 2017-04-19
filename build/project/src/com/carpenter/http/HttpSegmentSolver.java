/**
 * 
 */
package com.carpenter.http;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @Title: HttpSegmentSolver.java
 * @Package com.carpenter.http
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月13日 上午11:18:28
 * @version V1.0
 */
public class HttpSegmentSolver {
	public static String toJson(Object obj) {
		try {
			Gson gson = new Gson();
			return gson.toJson(obj);
		} catch (Exception e) {
			return null;
		}

	}
	
	public static <T> T fromJson(String str, Type type) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(str, type);
		} catch (Exception e) {
			return null;
		}

	}

	public static byte[] toByteArray(Object obj) {
		String str = HttpSegmentSolver.toJson(obj);
		if(str == null){
			return null;
		} else {
			return str.getBytes();
		}
		
	}

	public static <T> T byteArrayToObject(byte[] in, Type type, int offset) {
		String str = new String(in, 0, offset);
		try {
			return HttpSegmentSolver.fromJson(str, type);
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		String s = "GET /somedir/page.html Http/1.1";
		byte[] data = s.getBytes();
		System.out.println(new String(data));

		String ss = "{\"requestMethod\":GET, \"requestURL\":www.carpenterx.cn,\"version\":\"http1.1\"}";
		System.out.println();
		HttpSegment seg = HttpSegmentSolver.fromJson(ss, HttpSegment.class);
		System.out.println(seg.getRequestMethod());
		System.out.println(seg.getRequestURL());
		System.out.println(seg.getVersion());
		System.out.println(HttpSegmentSolver.toJson(seg));

	}
}
