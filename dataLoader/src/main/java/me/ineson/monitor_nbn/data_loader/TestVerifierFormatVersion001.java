/**
 * 
 */
package me.ineson.monitor_nbn.data_loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.ineson.monitor_nbn.shared.GeneralUtils;
import me.ineson.monitor_nbn.shared.io.TestSection;


/**
 * @author peter
 *
 */
public class TestVerifierFormatVersion001 extends AbstractTestVerifier {

    private static final Logger LOG = LogManager.getLogger(TestVerifierFormatVersion001.class);
	
    public TestSectionOutcome getTestOutcome( TestSection testSection) {
        TestSectionOutcome outcome = new TestSectionOutcome();

        List<List<String>>individualTests = new ArrayList<List<String>>(); 

        List<String>individualTest = null; 

        for (String line : testSection.getLines()) {
            if( Objects.isNull( outcome.getStartTime())) {
				Matcher versionMatcher = START_LINE.matcher(line);
				if( versionMatcher.matches()) {
					outcome.setStartTime(parseDate( versionMatcher, START_LINE_DATE_POS));
				}
			} else if( Objects.isNull(outcome.getEndTime())) {
				Matcher versionMatcher = END_LINE.matcher(line);
				if( versionMatcher.matches()) {
					outcome.setEndTime(parseDate( versionMatcher, END_LINE_DATE_POS));
                }
            }
			
            if (StringUtils.startsWith(line, START_TEST)) {
            	individualTest = new ArrayList<String>();
            	individualTests.add(individualTest);
            }

            if(Objects.nonNull(individualTest)) {
            	individualTest.add(line);
            }
        }

        LOG.info("Test starting at {} has {} sections", outcome.getStartTime(), individualTests.size());
        for (List<String> singleTest : individualTests) {
            // Check if the returned exit code is 0.
            if( ! GeneralUtils.hasStringContainsIgnoreCase(singleTest, TEST_SUCCESS) ) {
                LOG.debug("Test starting at {} had a command that failed", outcome.getStartTime());
                outcome.setTestSuccessful(false);
                break;
	        }

            // If the test used traceroute then check for a "<syn.ack>" response.
            if( GeneralUtils.hasStringStartingWithPrefix(singleTest, TEST_TRACEROUTE)) {
                if( ! GeneralUtils.hasStringContainsIgnoreCase(singleTest, TEST_TRACEROUTE_SUCCESS)) {
                    LOG.debug("Test starting at {} failed traceroute check", outcome.getStartTime());
                    outcome.setTestSuccessful(false);
					break;
				}
			}

            // If performed a ping then there was no packet loss.
            if( GeneralUtils.hasStringStartingWithPrefix(singleTest, TEST_PING)) {
                if( ! GeneralUtils.hasStringContainsIgnoreCase(singleTest, TEST_PING_SUCCESS)) {
                    LOG.debug("Test starting at {} failed ping check", outcome.getStartTime());
                    outcome.setTestSuccessful(false);
					break;
				}
			}

            // If performed a ping then there was no packet loss.
            if( GeneralUtils.hasStringStartingWithPrefix(singleTest, TEST_MODEN_STATUS)) {
            	//Modem URL:
                String modemStatusLine =  GeneralUtils.getStringStartingWithPrefix(singleTest, TEST_MODEN_STATUS_LINE);
                if(StringUtils.isNotBlank(modemStatusLine)) {
                    outcome.setTestSuccessful(connectedViaNbn(modemStatusLine));
                    LOG.debug("Test starting at {} modem status result is {}", outcome.getStartTime(), outcome.isTestSuccessful());
                } else {
                    LOG.debug("Test starting at {} failed could find modem status result", outcome.getStartTime());
                    outcome.setTestSuccessful(false);
				}
			}
        
        }
		
		return outcome;
	}
	
}
