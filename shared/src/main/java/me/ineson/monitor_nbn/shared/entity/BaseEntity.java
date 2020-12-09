package me.ineson.monitor_nbn.shared.entity;

import org.bson.types.ObjectId;

/**
 * @author peter
 *
 */
public abstract class BaseEntity {

	private ObjectId id;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
}
