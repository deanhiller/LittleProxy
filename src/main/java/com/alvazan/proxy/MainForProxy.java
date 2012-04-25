package com.alvazan.proxy;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.LocalDateTime;
import org.littleshoot.proxy.DefaultHttpProxyServer;

public class MainForProxy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, String> props = new HashMap<String, String>();
		props.put("hibernate.connection.url", "jdbc:h2:mem:test");
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("h2", props);
		TimeNow timeSystem = new TimeNowImpl();
		VisitedFilter filter = new VisitedFilter(factory, timeSystem );
		DefaultHttpProxyServer server = new DefaultHttpProxyServer(8080, filter);
		server.start();
	}
	
	public static class TimeNowImpl implements TimeNow {
		public LocalDateTime getCurrentTime() {
			return new LocalDateTime();
		}
	}

}
