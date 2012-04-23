package com.alvazan.proxy.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class MockHttpRequest implements HttpRequest {

	private Map<String, String> headers = new HashMap<String, String>();
	private HttpMethod method;
	private String uri;

	public void addHeader(String key, Object value) {
		headers.put(key, (String) value);
	}

	public void clearHeaders() {
	}

	public boolean containsHeader(String arg0) {
		return false;
	}

	public ChannelBuffer getContent() {
		return null;
	}

	public long getContentLength() {
		return 0;
	}

	public long getContentLength(long arg0) {
		return 0;
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public Set<String> getHeaderNames() {
		return null;
	}

	public List<Entry<String, String>> getHeaders() {
		return null;
	}

	public List<String> getHeaders(String name) {
		return null;
	}

	public HttpVersion getProtocolVersion() {
		return null;
	}

	public boolean isChunked() {
		return false;
	}

	public boolean isKeepAlive() {
		return false;
	}

	public void removeHeader(String arg0) {
	}

	public void setChunked(boolean arg0) {
	}

	public void setContent(ChannelBuffer arg0) {
	}

	public void setHeader(String arg0, Object arg1) {
	}

	public void setHeader(String arg0, Iterable<?> arg1) {
	}

	public void setProtocolVersion(HttpVersion arg0) {
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public void setMethod(HttpMethod m) {
		this.method = m;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
