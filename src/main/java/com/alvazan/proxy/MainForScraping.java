package com.alvazan.proxy;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.alvazan.proxy.scraping.WebPageScraper;

public class MainForScraping {

	public static void main(String[] args) {
		Map<String, String> props = new HashMap<String, String>();
		props.put("hibernate.connection.url", "jdbc:h2:mem:test");
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("h2", props);

		String url = "http://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&q=tax+accountants&aq=&sll=39.737567,-104.984718&sspn=0.570256,0.862427&vpsrc=6&g=denver,+CO&ie=UTF8&t=m&fll=39.837014,-105.037537&fspn=0.552562,1.047821&st=113699889012030469848&rq=1&ev=zo&split=1&jsv=408d&vps=11&sa=N&start=40";
		if(args.length > 0)
			url = args[0];
		
		WebPageScraper scraper = new WebPageScraper(factory, url);
		scraper.start();
	}
}
