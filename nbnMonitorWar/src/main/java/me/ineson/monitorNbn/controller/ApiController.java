/**
 * 
 */
package me.ineson.monitorNbn.controller;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.ineson.monitorNbn.service.DailySummaryService;
import me.ineson.monitorNbn.shared.dao.DailySummaryDao;
import me.ineson.monitorNbn.shared.dao.OutageDao;
import me.ineson.monitorNbn.shared.entity.DailySummary;
import me.ineson.monitorNbn.shared.entity.Outage;
import me.ineson.monitorNbn.shared.io.FileReader;

/**
 * @author peter
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOG = LogManager.getLogger(ApiController.class);

	@Autowired
	private DailySummaryService dailySummaryService;

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
        Iterable<DailySummary> results = dailySummaryDao.findByDateRange(startDate, endDate);
        
        LocalDate today = LocalDate.now();
        if( today.equals(startDate)
        		|| today.equals(endDate)
        		|| (today.isAfter(startDate) && today.isBefore(endDate))) {
        	Iterator<DailySummary>resultsIterator = results.iterator();
        	DailySummary firstDailySummary = resultsIterator.hasNext() ? resultsIterator.next() : null;

        	if( Objects.isNull(firstDailySummary)
        		|| Objects.isNull(firstDailySummary.getDate())
        		|| today.isAfter(firstDailySummary.getDate())) {
        		List<DailySummary>updatedResults = new ArrayList<>();
        		updatedResults.add(dailySummaryService.createDummyDailySummary());
        		results.forEach(updatedResults::add);
        		results = updatedResults;
       		}
        }

        return results;
	}

	@GetMapping("/outage")
	public Iterable<Outage> getOutagesForDate(@RequestParam(name="date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LOG.info("Getting outages for {}", date);
        
        return outageDao.findByDate(date);
	}

	@GetMapping("/outage/{startDateTime}/log")
	public List<String> getOutageLog(@PathVariable("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime) {
		List<String>consoleLog = new ArrayList<>();
		
        LOG.info("Getting outage log for {}", startDateTime);
        DailySummary dailySummary = dailySummaryDao.findByDate(startDateTime.toLocalDate());
        Outage outage = outageDao.find(startDateTime);

        if (Objects.nonNull(dailySummary) && Objects.nonNull(outage)) {
        	if(Objects.nonNull(dailySummary.getDatafile())
        			&& Objects.nonNull(outage.getStartFilePosition())
        			&& Objects.nonNull(outage.getNumberOfLines())) {
    		    File datafile =dailySummaryService.createLogFile(dailySummary);
        		try (FileReader file = new FileReader( datafile)) {
					file.seek(outage.getStartFilePosition().longValue());
					consoleLog = file.getLines(outage.getNumberOfLines().intValue());
        		} catch (Exception e) {
					LOG.error("Problem reading file {}, reading from postion {}, {} lines",
							datafile, outage.getStartFilePosition(), outage.getNumberOfLines(), e);
	            	consoleLog.add("Error: failed to read log from file " + datafile
	            			+ ", message: " + e.getMessage());
				}
        			
        	} else {
            	consoleLog.add("Error: Incomplete outage data for " + startDateTime
            			+ " (file name: " + dailySummary.getDatafile()
            			+ ", start position: " + outage.getStartFilePosition()
            			+ ", number of lines: " + outage.getNumberOfLines() + ")");
        	}
        } else {
        	consoleLog.add("Error: Get data (daily summary: " + dailySummary
        			+ ", outage: " + outage + ") for " + startDateTime);
        }
	
        return consoleLog;
	}
}
