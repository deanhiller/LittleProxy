package com.alvazan.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OurResponse implements HttpResponse {

	private static final Logger log = LoggerFactory.getLogger(OurResponse.class);

	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	
	public String getHeader(String name) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public List<String> getHeaders(String name) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public List<Entry<String, String>> getHeaders() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public Set<String> getHeaderNames() {
		return headers.keySet();
	}

	public HttpVersion getProtocolVersion() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void setProtocolVersion(HttpVersion version) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public ChannelBuffer getContent() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void setContent(ChannelBuffer content) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void addHeader(String name, Object value) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void setHeader(String name, Object value) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void setHeader(String name, Iterable<?> values) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void removeHeader(String name) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void clearHeaders() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public long getContentLength() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public long getContentLength(long defaultValue) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public boolean isChunked() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void setChunked(boolean chunked) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public boolean isKeepAlive() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public HttpResponseStatus getStatus() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public void setStatus(HttpResponseStatus status) {
		throw new UnsupportedOperationException("not implemented yet");
	}
}
