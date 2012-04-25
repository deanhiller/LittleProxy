package com.alvazan.proxy.db;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.joda.time.LocalDateTime;

@Entity
@NamedQueries({
@NamedQuery(name="findByIp", query="select u from User u where u.fromAddress = :ip"),
@NamedQuery(name="findAllUsers", query="select u from User u")
})
public class User {
	
	@Column(name="Id")
	@Id()
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length=20, nullable=false, unique=true)
	private String fromAddress;
	
	@Column(length=30, nullable=true)
	private String name;

	@OneToMany(fetch=FetchType.LAZY,mappedBy="user")
	private List<Timeworked> timecard;
	
	@Column(nullable=false)
	private Date lastTimeWorked;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static User findByIp(EntityManager mgr, String hostAddress) {
		Query query = mgr.createNamedQuery("findByIp");
		query.setParameter("ip", hostAddress);
		try {
			return (User) query.getSingleResult();
		} catch(NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<User> findAll(EntityManager mgr) {
		Query query = mgr.createNamedQuery("findAllUsers");
		return query.getResultList();
	}

	public List<Timeworked> getTimecard() {
		return timecard;
	}

	public void setTimecard(List<Timeworked> timecard) {
		this.timecard = timecard;
	}

	public LocalDateTime getLastTimeWorked() {
		return new LocalDateTime(lastTimeWorked);
	}
	public void setLastTimeWorked(LocalDateTime time) {
		this.lastTimeWorked = time.toDate();
	}
	
}
