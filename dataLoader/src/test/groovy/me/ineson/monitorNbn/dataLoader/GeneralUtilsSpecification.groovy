package me.ineson.monitorNbn.dataLoader;

import spock.lang.*

class GeneralUtilsSpecification extends Specification {

    @Unroll
    def "checkStartStringExists tests"(String searchString, List<String> testLines, boolean expectedResult) {
		expect:
		    GeneralUtils.checkStartStringExists(searchString, testLines) == expectedResult
	
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
    def "checkStringContainsIgnoreCase tests"(String searchString, List<String> testLines, boolean expectedResult) {
		expect:
		    GeneralUtils.checkStringContainsIgnoreCase(searchString, testLines) == expectedResult
	
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
