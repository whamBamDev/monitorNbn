package me.ineson.monitor_nbn.thymeleaf;

import java.time.Duration

import me.ineson.monitor_nbn.thymeleaf.UtilitiesExpression
import spock.lang.*

class UtilitiesExpressionSpecification extends Specification {

    @Unroll
    def "output duration tests"(String testDuration, String expectedResult) {
        when:
			def expression = new UtilitiesExpression();
			Duration duration = null;
			if (Objects.nonNull(testDuration)) {
				duration = Duration.parse(testDuration);
			}
	
        then:
			expression.format(duration) == expectedResult

        where:
			testDuration           | expectedResult
			null                   | "--:--:--"
			"PT3H4M22S"            | "03:04:22"
			"PT15H34M6S"           | "15:34:06"
			"P10DT10H11M7S"        | "250:11:07"
			
		}
	
	
    @Unroll
    def "output iterable tests"(List<String> testLines, String expectedResult) {
        when:
		    def expression = new UtilitiesExpression();

		then:
		    expression.outputIterable(testLines) == expectedResult
	
		where:
			testLines               | expectedResult
			null                    | ""
			[ ]                     | ""
			[ "aa" ]                | "aa"
			[ "aaa", "bbb", "ccc" ] | "aaa\nbbb\nccc"
			
	}
	

	@Unroll
	def "output iterable with defined line end tests"(List<String> testLines, String lineEnd, String expectedResult) {
		when:
			def expression = new UtilitiesExpression();

		then:
			expression.outputIterable(testLines, lineEnd) == expectedResult
	
		where:
			testLines               | lineEnd | expectedResult
			null                    | "<br>"  | ""
			[ ]                     | "<br>"  | ""
			[ "aa" ]                | "<br>"  | "aa"
			[ "aaa", "bbb", "ccc" ] | "<br>"  | "aaa<br>bbb<br>ccc"
			[ "aaa", "bbb", "ccc" ] | null    | "aaabbbccc"
			[ "aaa", "bbb", "ccc" ] | ""      | "aaabbbccc"
			
	}

}
