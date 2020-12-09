package me.ineson.monitor_nbn.service;

import java.time.Duration

import me.ineson.monitor_nbn.shared.entity.DailySummary
import me.ineson.monitor_nbn.thymeleaf.UtilitiesExpression
import spock.lang.*

class DailySummaryServiceSpecification extends Specification {

    def "create a empty Daily Summary record with todays date"() {
        given:
            DailySummaryService service = new DailySummaryService();

        when:
			DailySummary result = service.createDummyDailySummary();
	
        then:
            result != null;
			result.getOutageCount() == 0;
    }		

}
