/**
 * 
 */
package com.carpenter.http;

import com.google.gson.*;

/**
 * @Title: httpSegment.java
 * @Package com.carpenter.http
 * @Description: TODO
 * @author carpenter
 * @date 2016年12月12日 下午8:00:02
 * @version V1.0
 */

public class HttpSegment {
	private String requestMethod, requestURL, version, host, userAgent, accept, acceptLanguage, acceptEncoding, referer,
			state, describe, date, server, lastModified, contentLength, contentType, data;

	public static final boolean Request = true;
	public static final boolean Response = false;
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String Version = "HTTP/1.1";
	public static final String Server = "Carpenter/1.0.0(Windows Server)";
	public static final String UserAgent = "Mozilla/5.0";

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public String getAcceptEncoding() {
		return acceptEncoding;
	}

	public void setAcceptEncoding(String acceptEncoding) {
		this.acceptEncoding = acceptEncoding;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getContentLength() {
		return contentLength;
	}

	public void setContentLength(String contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

}
