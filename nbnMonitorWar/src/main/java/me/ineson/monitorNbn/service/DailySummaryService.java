package me.ineson.monitorNbn.service;

import java.io.File;
import java.time.LocalDate;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import me.ineson.monitorNbn.shared.entity.DailySummary;

@Service
public class DailySummaryService {

    private static final Logger LOG = LogManager.getLogger(DailySummaryService.class);

    private static final Integer ZERO = 0;

	@Value("${dataFileRoot:#{null}}")
	private String dataFileRoot;

    /**
     * @param dailySummary
     * @return
     */
    public File createLogFile( DailySummary dailySummary) {
        if(Objects.isNull(dailySummary) || StringUtils.isBlank( dailySummary.getDatafile())) {
            return null;
		}
		
        File fullFilename = new File(dailySummary.getDatafile());
        if(StringUtils.isBlank(dataFileRoot)) {
        	return fullFilename;
        }
        LOG.info( "fullname {},  name {}", fullFilename, fullFilename.getName());
        
        return new File(dataFileRoot, fullFilename.getName());
	}

    /**
     * Creates a new {@link DailySummary} for today with all the counts set to 0.
     * @return
     */
    public DailySummary createDummyDailySummary() {
    	DailySummary dailySummary = new DailySummary();
    	dailySummary.setDate(LocalDate.now());
    	dailySummary.setOutageCount(ZERO);
    	dailySummary.setFailedTestCount(ZERO);
    	dailySummary.setTestCount(ZERO);

        return dailySummary;
	}

}
