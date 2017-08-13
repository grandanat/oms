package org.mst.ubs.oms.ds.model;

import javax.persistence.*;

@Entity
public class Product {

	@Id
	private String id;

	@Column(nullable = false)
	private String name;

	public Product(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
