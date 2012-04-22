package com.alvazan.proxy.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.alvazan.proxy.AlreadyVisitedException;
import com.alvazan.proxy.VisitedFilter;
import com.alvazan.proxy.db.Domain;
import com.alvazan.proxy.db.User;

public class TestBasicFilter {

	@Test
	public void testBasic() throws UnknownHostException {
		Map<String, String> props = new HashMap<String, String>();
		props.put("hibernate.connection.url", "jdbc:h2:mem:test");
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("h2", props);
		MockNow mockNow = new MockNow();
		VisitedFilter filter = new VisitedFilter(factory, mockNow);
		String domain = "www.something.com";
		String url = "http://"+domain+"/someurl/someurl";
		MockChannel mockChannel = new MockChannel();
		mockChannel.setRemoteAddress(createUser1Address());
		MockHttpRequest req = new MockHttpRequest();
		req.addHeader("Host", domain);
		req.setUri(url);
		req.setMethod(HttpMethod.GET);
		
		LocalDateTime time = new LocalDateTime();
		mockNow.addNextNowTime(time);
		filter.filter(req, mockChannel);

		//Now, check the database for a single entry user and single entry VisitedSite
		validateEntries(factory, domain, mockChannel);
		
		//Now do another request and fake that the current time is
		//8 minutes in the future now.
		LocalDateTime newNowTime = time.plusMinutes(8);
		mockNow.addNextNowTime(newNowTime);
		req.setUri("/something/asdf");
		//If this request happens again...
		filter.filter(req, mockChannel);

		//Now, check the database for a single entry user and two VisistedSites AND the times will not
		//change within the 10 minute window!!!!
		validateEntries2(factory, domain, mockChannel, time, time);
		
		LocalDateTime date3 = time.plusMinutes(11);
		//now do another webpage
		try {
			req.setUri("/qwer/qwer");
			mockNow.addNextNowTime(date3);
			filter.filter(req, mockChannel);
			Assert.fail("Should have thrown exception");
		} catch(AlreadyVisitedException e) {
		}
		
		//Now, check the database for a single entry user and two VisistedSites
		validateEntries3(factory, domain, mockChannel, time, date3);
	}

	private void validateEntries3(EntityManagerFactory factory, String domain,
			MockChannel mockChannel, LocalDateTime time,
			LocalDateTime newNowTime) {
		validateEntries(factory, domain, mockChannel);
		EntityManager mgr = factory.createEntityManager();
		List<Domain> domains = Domain.findAll(mgr);
		Assert.assertEquals(1, domains.size());
		Domain d = domains.get(0);
		Assert.assertEquals(3, d.getUrls().size());
		
		Assert.assertEquals(2, d.getNumberTimesRequested());
		Assert.assertEquals(time, d.getFirstTimeAccessed());
		Assert.assertEquals(newNowTime, d.getLastTimeAccessed());
		Assert.assertEquals(true, d.isWasVisited());
		Assert.assertEquals(false, d.isComplete());
		mgr.close();		
	}

	private void validateEntries2(EntityManagerFactory factory, String domain,
			MockChannel mockChannel, LocalDateTime firstNow, LocalDateTime secondNow) {
		//still have one user so validate that...
		validateEntries(factory, domain, mockChannel);
		EntityManager mgr = factory.createEntityManager();
		List<Domain> domains = Domain.findAll(mgr);
		Assert.assertEquals(1, domains.size());
		Domain d = domains.get(0);
		Assert.assertEquals(2, d.getUrls().size());
		
		Assert.assertEquals(1, d.getNumberTimesRequested());
		Assert.assertEquals(firstNow, d.getFirstTimeAccessed());
		Assert.assertEquals(secondNow, d.getLastTimeAccessed());
		Assert.assertEquals(true, d.isWasVisited());
		
		mgr.close();
	}

	private void validateEntries(EntityManagerFactory factory, String domain, MockChannel mockChannel) {
		EntityManager mgr = factory.createEntityManager();
		List<User> users = User.findAll(mgr);
		Assert.assertEquals(1, users.size());
		InetSocketAddress remoteAddress = (InetSocketAddress) mockChannel.getRemoteAddress();
		String addr = remoteAddress.getAddress().getHostAddress();
		Assert.assertEquals(addr, users.get(0).getFromAddress());
		
		List<Domain> domains = Domain.findAll(mgr);
		Assert.assertEquals(1, domains.size());
		User fromUser = domains.get(0).getFromUser();
		Assert.assertEquals(users.get(0).getId(), fromUser.getId());
		mgr.close();
	}

	private SocketAddress createUser1Address() throws UnknownHostException {
		InetAddress local = InetAddress.getLocalHost();
		InetSocketAddress addr = new InetSocketAddress(local, 5555);
		return addr;
	}
	
	/**
	 * This is a test for when our screen scraper fill in the table with domains that are likely
	 * targets for our people to hit to gather urls and information
	 * @throws UnknownHostException 
	 */
	@Test
	public void testForExistingDomainAlreadyIsHit() throws UnknownHostException {
		String jdbc = "jdbc:h2:mem:test2";
		runTest(jdbc);
	}

	private EntityManagerFactory runTest(String jdbc) throws UnknownHostException {
		Map<String, String> props = new HashMap<String, String>();
		props.put("hibernate.connection.url", jdbc);
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("h2", props);
		Domain domain = createDomain(factory);
		
		MockNow mockNow = new MockNow();
		VisitedFilter filter = new VisitedFilter(factory, mockNow);
		String url = "http://"+domain.getDomain()+"/someurl/someurl";
		MockChannel mockChannel = new MockChannel();
		mockChannel.setRemoteAddress(createUser1Address());
		MockHttpRequest req = new MockHttpRequest();
		req.addHeader("Host", domain.getDomain());
		req.setUri(url);
		req.setMethod(HttpMethod.GET);
		
		validateDomainExists(factory);
		
		LocalDateTime time = new LocalDateTime();
		mockNow.addNextNowTime(time);
		filter.filter(req, mockChannel);
		
		validateDomainFilledIn(factory);
		return factory;
	}
	
	private void validateDomainFilledIn(EntityManagerFactory factory) {
		EntityManager mgr = factory.createEntityManager();
		List<Domain> domains = Domain.findAll(mgr);
		Assert.assertEquals(1, domains.size());
		
		Domain domain = domains.get(0);
		Assert.assertEquals(true, domain.isWasVisited());
		Assert.assertEquals(1, domain.getNumberTimesRequested());
		Assert.assertEquals(false, domain.isComplete());
		Assert.assertNotNull(domain.getFromUser());
		
		mgr.close();
	}

	private void validateDomainExists(EntityManagerFactory factory) {
		EntityManager mgr = factory.createEntityManager();
		List<Domain> domains = Domain.findAll(mgr);
		Assert.assertEquals(1, domains.size());
		Domain domain = domains.get(0);
		Assert.assertEquals(false, domain.isComplete());
		Assert.assertEquals(false, domain.isWasPostedToWithEmail());
		Assert.assertEquals(false, domain.isWasVisited());
		mgr.close();
	}

	private Domain createDomain(EntityManagerFactory factory) {
		EntityManager mgr = factory.createEntityManager();
		mgr.getTransaction().begin();
		
		Domain domain = new Domain();
		domain.setDomain("www.mydean.com");
		mgr.persist(domain);
		
		mgr.getTransaction().commit();
		mgr.close();
		return domain;
	}

	@Test
	public void testForPostNoEmail() {
		
	}
	
	@Test
	public void testForPostHasEmail() {
		
	}
	
	
}
