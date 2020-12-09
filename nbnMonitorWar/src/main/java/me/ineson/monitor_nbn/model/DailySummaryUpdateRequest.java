/**
 * 
 */
package me.ineson.monitor_nbn.model;

import java.time.LocalDate;

/**
 * @author peter
 *
 */
public class DailySummaryUpdateRequest {

	private LocalDate date;

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

}
