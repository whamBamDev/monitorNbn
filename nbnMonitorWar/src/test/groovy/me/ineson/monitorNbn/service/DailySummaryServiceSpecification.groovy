package me.ineson.monitorNbn.service;

import me.ineson.monitorNbn.shared.entity.DailySummary
import me.ineson.monitorNbn.thymeleaf.UtilitiesExpression
import java.time.Duration
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
