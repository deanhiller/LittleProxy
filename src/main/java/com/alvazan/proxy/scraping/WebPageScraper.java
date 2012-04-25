package com.alvazan.proxy.scraping;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.alvazan.proxy.VisitedFilter;
import com.alvazan.proxy.db.Domain;

public class WebPageScraper {

	private EntityManagerFactory factory;
	private String urlToStart;

	public WebPageScraper(EntityManagerFactory factory, String urlToStart) {
		this.factory = factory;
		this.urlToStart = urlToStart;
	}

	public void start() {
		String url = urlToStart;
		
		while(url != null) {
			ScrapedInformation info = findPagesListOfDomains(url);
			saveDomainsToDatabase(info.getDomains());
			url = info.getNextPageUrl();
		}
		
	}

	private void saveDomainsToDatabase(List<String> domains) {
		EntityManager mgr = factory.createEntityManager();
		mgr.getTransaction().begin();
		try {
			for(String d : domains) {
				Domain domain = new Domain();
				domain.setDomain(d);
				mgr.persist(domain);
			}
		} finally {
			VisitedFilter.silentCommit(mgr);
			VisitedFilter.silentClose(mgr);
		}
	}

	//Main screen scrape goes here..and add each domain to the ScrapeInformation.java object
	//and then add the next pages url as well
	private ScrapedInformation findPagesListOfDomains(String url) {
		ScrapedInformation info = new ScrapedInformation();
		//TODO: fix this..
		info.getDomains().add("somedomain that was scraped");
		return null;
	}

}
