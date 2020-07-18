/**
 * 
 */
package me.ineson.monitorNbn.controller;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.ineson.monitorNbn.model.OutageResults;
import me.ineson.monitorNbn.shared.dao.DailySummaryDao;
import me.ineson.monitorNbn.shared.dao.OutageDao;
import me.ineson.monitorNbn.shared.entity.DailySummary;
import me.ineson.monitorNbn.shared.entity.Outage;
import me.ineson.monitorNbn.shared.io.FileReader;

/**
 * @author peter
 *
 */
@Controller
public class OutageController {

    private static final Logger LOG = LogManager.getLogger(OutageController.class);

	@Autowired
	private DailySummaryDao dailySummaryDao;  

	@Autowired
	private OutageDao outageDao;
	
	@Value("${dataFileRoot:#{null}}")
	private String dataFileRoot;

    @GetMapping("/outage")
    public String getOutagesForDate(
    		@RequestParam(name="date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    		Model model) {
    	LOG.warn("Fetching outage date for {}", date);
    	System.out.println("Fetching outage date for " + date);
	    model.addAttribute("date", date);

	    List<OutageResults>outageResults = new ArrayList<>();
	    model.addAttribute("outages", outageResults);
	    
	    DailySummary dailySummary = dailySummaryDao.findByDate(date);
	    if(Objects.nonNull(dailySummary)) {
	    	LOG.warn("Found dailySummary {}", dailySummary);
		    model.addAttribute("dailySummary", dailySummary);

	    	System.out.println("dataFileRoot: " + dataFileRoot);
	    	LOG.warn("dataFileRoot: {}", dataFileRoot);
		    FileReader file = null;
		    File datafile = new File(dailySummary.getDatafile());
		    try {
		    	if( StringUtils.isNotBlank(dataFileRoot)) {
		    		datafile = new File(dataFileRoot, datafile.getName()); 
		    	} else {
		    		
		    	}
		    	file = new FileReader( datafile);
		    } catch (Exception e) {
				LOG.error("Problem opening file {}, no test log will be displayed", datafile, e);
			}
		    
		    Iterable<Outage>outages = outageDao.findByDate(date);
		    
		    for (Outage outage : outages) {
			    OutageResults outageResult = new OutageResults();
			    outageResults.add(outageResult);
				
			    outageResult.setOutage(outage);
			    if(Objects.nonNull(outage.getStartTime()) && Objects.nonNull(outage.getEndTime())) {
			    	outageResult.setDuration(Duration.between(outage.getStartTime(), outage.getEndTime()));
			    }

			    if( Objects.nonNull(file) && Objects.nonNull(outage.getStartFilePosition()) && Objects.nonNull(outage.getNumberOfLines())) {
			    	try {
						file.seek(outage.getStartFilePosition().longValue());
						outageResult.setTestOutput(file.getLines(outage.getNumberOfLines().intValue()));
					} catch (IOException e) {
						LOG.error("Problem reading file {}, reading from postion {}, {} lines",
								datafile, outage.getStartFilePosition(), outage.getNumberOfLines(), e);
					}
			    	
			    }
			    
			}

		    if( Objects.nonNull(file)) {
		    	try {
					file.close();
				} catch (Exception e) {
					LOG.info("Problem closing file {}", datafile, e);
				}
		    }
	    }
    	
    	
        return "outage";
    }
	
}
