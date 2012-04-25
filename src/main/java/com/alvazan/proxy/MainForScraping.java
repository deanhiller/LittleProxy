package com.alvazan.proxy;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.littleshoot.proxy.DefaultHttpProxyServer;

import com.alvazan.proxy.MainForProxy.TimeNowImpl;
import com.alvazan.proxy.scraping.WebPageScraper;

public class MainForScraping {

	public static void main(String[] args) {
		Map<String, String> props = new HashMap<String, String>();
		props.put("hibernate.connection.url", "jdbc:h2:mem:test");
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("h2", props);

		WebPageScraper scraper = new WebPageScraper(factory, args[0]);
		scraper.start();
	}
}
