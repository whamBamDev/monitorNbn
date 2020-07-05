package me.ineson.monitorNbn.shared;

import me.ineson.monitorNbn.shared.GeneralUtils
import spock.lang.*

class GeneralUtilsSpecification extends Specification {

    @Unroll
    def "checkStartStringExists tests"(String searchString, List<String> testLines, boolean expectedResult) {
		expect:
		    GeneralUtils.hasStringStartingWithPrefix(testLines, searchString) == expectedResult
	
		where:
			searchString |  testLines                                 | expectedResult
			null         |  null                                      | false
			"aa"         |  null                                      | false
			null         |  [ "str" ]                                 | false
			"aa"         |  [ "aa bb" ]                               | true
			"bb"         |  [ "aa bb" ]                               | false
			"bb"         |  [ "aaa", null, "bbb", "ccc" ]             | true
			
	}
	
	@Unroll
	def "getStringStartingWithPrefix tests"(String searchString, List<String> testLines, String expectedResult) {
		expect:
			GeneralUtils.getStringStartingWithPrefix(testLines, searchString) == expectedResult
	
		where:
			searchString |  testLines                                 | expectedResult
			null         |  null                                      | null
			"aa"         |  null                                      | null
			null         |  [ "str" ]                                 | null
			"aa"         |  [ "aa bb" ]                               | "aa bb"
			"bb"         |  [ "aa bb" ]                               | null
			"bb"         |  [ "aaa", null, "bbb", "ccc" ]             | "bbb"
			
	}

    @Unroll
    def "hasStringContainsIgnoreCase tests"(String searchString, List<String> testLines, boolean expectedResult) {
		expect:
		    GeneralUtils.hasStringContainsIgnoreCase(testLines, searchString) == expectedResult
	
		where:
			searchString |  testLines                                 | expectedResult
			null         |  null                                      | false
			"aa"         |  null                                      | false
			null         |  [ "str" ]                                 | false
			"aa"         |  [ "aa bb" ]                               | true
			"bb"         |  [ "aa bb" ]                               | true
			"bb"         |  [ "aaa", null, "bbb", "ccc" ]             | true
			"xx"         |  [ "aaa", null, "bbb", "ccc" ]             | false
			"BbB"        |  [ "aaa", null, "bbb", "ccc" ]             | true
			
	}
}
