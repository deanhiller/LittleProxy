package com.alvazan.proxy.db;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
	
	@Column(length=20, nullable=false)
	private String fromAddress;
	
	@Column(length=30, nullable=true)
	private String name;

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
	
}
