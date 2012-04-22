package com.alvazan.proxy;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.LocalDateTime;
import org.littleshoot.proxy.DefaultHttpProxyServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("h2");
		TimeNow timeSystem = new TimeNowImpl();
		VisitedFilter filter = new VisitedFilter(factory, timeSystem );
		DefaultHttpProxyServer server = new DefaultHttpProxyServer(8080, filter);
		server.start();
	}
	
	private static class TimeNowImpl implements TimeNow {
		public LocalDateTime getCurrentTime() {
			return new LocalDateTime();
		}
	}

}
