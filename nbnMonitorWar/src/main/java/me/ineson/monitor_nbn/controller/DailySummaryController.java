/**
 * 
 */
package me.ineson.monitor_nbn.controller;

import java.time.LocalDate;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import me.ineson.monitor_nbn.service.DailySummaryService;
import me.ineson.monitor_nbn.shared.dao.DailySummaryDao;
import me.ineson.monitor_nbn.shared.entity.DailySummary;

/**
 * @author peter
 *
 */
@Controller
public class DailySummaryController {

    private static final Logger LOG = LogManager.getLogger(DailySummaryController.class);

	@Autowired
	private DailySummaryService dailySummaryService;

	@Autowired
	private DailySummaryDao dailySummaryDao;  

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedDelay=60000)
    public void publishUpdates(){
    	DailySummary dailySummary = dailySummaryDao.findByDate(LocalDate.now());
    	if( Objects.isNull(dailySummary)) {
    		dailySummary = dailySummaryService.createDummyDailySummary();
    	}
    	
        template.convertAndSend("/topic/dailySummary", dailySummary);
    }

}
