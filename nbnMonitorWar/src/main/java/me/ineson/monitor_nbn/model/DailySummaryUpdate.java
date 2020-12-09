/**
 * 
 */
package me.ineson.monitor_nbn.model;

import java.time.LocalDate;

/**
 * @author peter
 *
 */
public class DailySummaryUpdate {

	private LocalDate date;

	private String name;

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
