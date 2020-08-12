/**
 * 
 */
package me.ineson.monitorNbn.controller;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.ineson.monitorNbn.shared.dao.DailySummaryDao;
import me.ineson.monitorNbn.shared.dao.OutageDao;
import me.ineson.monitorNbn.shared.entity.DailySummary;
import me.ineson.monitorNbn.shared.entity.Outage;

/**
 * @author peter
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOG = LogManager.getLogger(ApiController.class);

	@Autowired
	private DailySummaryDao dailySummaryDao;  

	@Autowired
	private OutageDao outageDao;  

	@GetMapping("/dailySummary")
	public Iterable<DailySummary> getDailySummary(
			@RequestParam(name="startDate", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name="endDate", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		if( Objects.isNull(startDate)) {
			startDate = LocalDate.now().minusMonths(1);
		}
		if( Objects.isNull(endDate)) {
			endDate = LocalDate.now();
        }
		
        LOG.info("Getting daily summary date from {} to {}", startDate, endDate);
        return dailySummaryDao.findByDateRange(startDate, endDate);
	}

	@GetMapping("/outage")
	public Iterable<Outage> getOutagesForDate(@RequestParam(name="date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LOG.info("Getting outages for {}", date);
        
        return outageDao.findByDate(date);
	}

}
