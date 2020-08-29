/**
 * 
 */
package me.ineson.monitorNbn.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import me.ineson.monitorNbn.model.OutageResults;
import me.ineson.monitorNbn.service.DailySummaryService;
import me.ineson.monitorNbn.shared.dao.DailySummaryDao;
import me.ineson.monitorNbn.shared.dao.OutageDao;
import me.ineson.monitorNbn.shared.entity.DailySummary;
import me.ineson.monitorNbn.shared.entity.Outage;

/**
 * @author peter
 *
 */
@Controller
public class OutageController {

    private static final Logger LOG = LogManager.getLogger(OutageController.class);

	@Autowired
	private DailySummaryService dailySummaryService;

	@Autowired
	private DailySummaryDao dailySummaryDao;  

	@Autowired
	private OutageDao outageDao;
	
    @GetMapping("/outage")
    public String getOutagesForDate(
    		@RequestParam(name="date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    		Model model) {
    	LOG.warn("Fetching outage date for {}", date);
	    model.addAttribute("date", date);

	    List<OutageResults>outageResults = new ArrayList<>();
	    model.addAttribute("outages", outageResults);
	    
	    DailySummary dailySummary = dailySummaryDao.findByDate(date);
	    if(Objects.nonNull(dailySummary)) {
	    	LOG.warn("Found dailySummary {}", dailySummary);
		    model.addAttribute("dailySummary", dailySummary);
		    File consoleLogFile = dailySummaryService.createLogFile(dailySummary);
		    LOG.info("File {} exits: {}, readable: {}", consoleLogFile, consoleLogFile.exists(), consoleLogFile.canRead());
		    model.addAttribute("consoleLogExists", (consoleLogFile.exists() && consoleLogFile.canRead()));

		    Iterable<Outage>outages = outageDao.findByDate(date);

		    for (Outage outage : outages) {
			    OutageResults outageResult = new OutageResults();
			    outageResults.add(outageResult);
				
			    outageResult.setOutage(outage);
			    if(Objects.nonNull(outage.getStartTime()) && Objects.nonNull(outage.getEndTime())) {
			    	outageResult.setDuration(Duration.between(outage.getStartTime(), outage.getEndTime()));
			    }
			}

		    List<Map<?,?>>dayOutageData = new ArrayList<>(3600);
		    model.addAttribute("outageData", dayOutageData);

		    LocalDateTime daySpan = date.atStartOfDay();
		    ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getOffset(daySpan);

		    int day = daySpan.getDayOfYear();
		    long outageStartTime = -1L;
		    long outageEndTime = -1L;
		    Iterator<Outage>outageIterator = outages.iterator();
	    	Outage outage = null;
            Map<String, Object>tooltipData = null;
		    while(daySpan.getDayOfYear() == day) {
			    while (outageStartTime == -1 && outageIterator.hasNext() ) {
			    	outage = outageIterator.next();
					if (Objects.nonNull(outage.getStartTime()) && Objects.nonNull(outage.getEndTime())) {
						outageStartTime = outage.getStartTime().truncatedTo(ChronoUnit.MINUTES).getLong(ChronoField.MINUTE_OF_DAY);
						outageEndTime = outage.getEndTime().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1).get(ChronoField.MINUTE_OF_DAY);

						tooltipData = new HashMap<String, Object>();
						tooltipData.put("startTime", outage.getStartTime().toEpochSecond(zoneOffset) * 1000L);
						tooltipData.put("duration", (outage.getEndTime().toEpochSecond(zoneOffset) - outage.getStartTime().toEpochSecond(zoneOffset)) * 1000L);
					}
			    } 

		    	Map<String, Object>outageData = new HashMap<String, Object>();
			    dayOutageData.add(outageData);
			    
		    	int minute = daySpan.get(ChronoField.MINUTE_OF_DAY);
				outageData.put("t", daySpan.toEpochSecond(zoneOffset) * 1000L);
				if(outageStartTime > -1L && minute >= outageStartTime && minute <= outageEndTime) {
					outageData.put("y", 1);
					outageData.put("tooltipData", tooltipData);
				} else {
					outageData.put("y", null);
				}

				daySpan = daySpan.plusMinutes(1L);
				if(outageStartTime > -1L && minute > outageEndTime) {
					outageStartTime = -1;
					outageEndTime = -1;
				}
		    }

		    Map<String, Object>outageData = new HashMap<String, Object>();
		    dayOutageData.add(outageData);
			outageData.put("t", daySpan.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(daySpan)) * 1000L);
		    
			LOG.info("EndOfDay: {}", daySpan);
	    }
    	
        return "outage";
    }


    @RequestMapping(value = "/outage/file", method = RequestMethod.GET)
    public void getFile(
   		@RequestParam(name="date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        HttpServletResponse response) {

	    DailySummary dailySummary = dailySummaryDao.findByDate(date);
	    if(Objects.isNull(dailySummary)) {
            throw new IllegalStateException("Found dailySummary for " + date);
	    }

	    File datafile =dailySummaryService.createLogFile(dailySummary);
	    if( ! datafile.exists() || ! datafile.canRead()) {
            throw new IllegalStateException("Console log " + datafile + " cannot be read.");
	    }

    	try {
          FileCopyUtils.copy( new FileInputStream( datafile), response.getOutputStream());
          response.flushBuffer();
        } catch (IOException ex) {
          LOG.info("Error reading file to output stream. Filename was '{}'", datafile, ex);
          throw new RuntimeException("IOError sending file  " + datafile + " to output stream", ex);
        }

    }
}
