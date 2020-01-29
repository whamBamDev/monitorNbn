package me.ineson.monitorNbn.dataLoader;

import spock.lang.*

class FileReaderSpecification extends Specification {

    def "test check for the start of a test"(String inputLine, boolean result) {

		expect:
			FileReader.isStartOfTestBlock( inputLine) == result
			
        where:
            inputLine                               | result
			null                                    | false
            ""                                      | false
			"bcjhbeqr"                              | false
			"==== start"                            | true
			"==== start(001): 05-12-2019 00:00:01"  | true
			"==== end"                              | false
			"==== end: 05-12-2019 00:00:34"         | false
    }

	def "test check for the end of a test"(String inputLine, boolean result) {

		expect:
			FileReader.isEndOfTestBlock( inputLine) == result
			
		where:
			inputLine                               | result
			null                                    | false
			""                                      | false
			"bcjhbeqr"                              | false
			"==== start"                            | false
			"==== start(001): 05-12-2019 00:00:01"  | false
			"==== end"                              | true
			"==== end: 05-12-2019 00:00:34"         | true
	}

}
