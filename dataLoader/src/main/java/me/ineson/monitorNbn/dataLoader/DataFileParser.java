/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ineson.monitorNbn.shared.dao.DailySummaryDao;
import me.ineson.monitorNbn.shared.dao.OutageDao;
import me.ineson.monitorNbn.shared.entity.DailySummary;
import me.ineson.monitorNbn.shared.entity.Outage;
import me.ineson.monitorNbn.shared.io.FileReader;
import me.ineson.monitorNbn.shared.io.TestSection;

/**
 * @author peter
 *
 */
public class DataFileParser {

    private static final Logger LOG = LogManager.getLogger(DataFileParser.class);

    private DailySummaryDao dailySummaryDao;

	private OutageDao outageDao;
	
	public void processFile( File dataFile) throws IOException {

		//Get date for file/check DB has stats.
		//save, date, number of failures
        try (FileReader reader = new FileReader(dataFile)) { 
            DailySummary dailySummary = null;
            Outage outage = null;
            int outageFirstLineNumber = 0;

            while( true) {
                TestSection section = reader.getNextTestSection();
                if( Objects.isNull(section)) {
                    // If null is returned then hit the EOF.
                    if( Objects.nonNull(dailySummary)) {
                        dailySummaryDao.update(dailySummary);
    				}

                    if( Objects.nonNull(outage)) {
                        outageDao.update(outage);
			        }

                    return;
	           	}
			
                AbstractTestVerifier verifier = AbstractTestVerifier.getTestVerifier(section.getLines());
                TestSectionOutcome testSuccessful = verifier.getTestOutcome(section);
			
                if(dailySummary == null) {
				    if(testSuccessful.getStartTime() == null) {
					    throw new IllegalStateException("Error - unable to read start time");
				    }
				 
                    LocalDate date = testSuccessful.getStartTime().toLocalDate();
                    dailySummaryDao.delete(date);
                    outageDao.delete(date);

                    dailySummary = new DailySummary();
                    dailySummary.setDate(date);
                    dailySummary.setDatafile(dataFile.getAbsolutePath());
                    dailySummary.setOutageCount(0);
                    dailySummary.setFailedTestCount(0);
                    dailySummary.setTestCount(0);
                    dailySummary = dailySummaryDao.add(dailySummary);
                }

                dailySummary.setTestCount(dailySummary.getTestCount().intValue() + 1);

                if( !testSuccessful.isTestSuccessful()) {
                    dailySummary.setFailedTestCount(dailySummary.getFailedTestCount().intValue() + 1);
                	

                	if(Objects.isNull(outage)) {
                        outageFirstLineNumber = section.getFirstLineNumber();

                        outage = new Outage();
                        outage.setStartTime(testSuccessful.getStartTime());
                        outage.setEndTime(testSuccessful.getEndTime());
                        outage.setStartFilePosition(section.getFilePosition());
                    
                        outage.setNumberOfLines(section.getLastLineNumber() - outageFirstLineNumber);
                    
                        outage = outageDao.add(outage);
                        LOG.info("Added new outage {}" , outage);
                    
                        dailySummary.setOutageCount(dailySummary.getOutageCount().intValue() + 1);
                        dailySummaryDao.update(dailySummary);
    				}

                    outage.setNumberOfLines(section.getLastLineNumber() - outageFirstLineNumber);
                    outage.setEndTime(testSuccessful.getEndTime());
                
                } else if(Objects.nonNull(outage)) {
                    outageDao.update(outage);
                    outage = null;
                    outageFirstLineNumber = 0;
                }
            }
        }
	}

	public void setDailySummaryDao(DailySummaryDao dailySummaryDao) {
		this.dailySummaryDao = dailySummaryDao;
	}

	public void setOutageDao(OutageDao outageDao) {
		this.outageDao = outageDao;
	}
			
}
