package me.ineson.monitorNbn.dataLoader;

import spock.lang.*

class TestVerifierFormatVersion001Specification extends Specification {

    @Unroll
    def "factory method should return proper verifier for a specific version"(String testfile, boolean result, String startTime, String endTime) {
		AbstractTestVerifier verifier = new TestVerifierFormatVersion001();
		
        given:
            URL testFile = Thread.currentThread().getContextClassLoader()
                .getResource( "me/ineson/monitorNbn/dataLoader/fileFormat/v001/" + testfile);
            FileReader fileReader = new FileReader( new File( testFile.getFile()));
			TestSection testSection = fileReader.getNextTestSection();
			
        when:
			TestSectionOutcome testOutcome = verifier.getTestOutcome(testSection);

        then:
            testOutcome != null
			testOutcome.isTestSuccessful() == result
			testOutcome.getStartTime() != null
			testOutcome.getStartTime().toString() == startTime
			testOutcome.getEndTime() != null
			testOutcome.getEndTime().toString() == endTime
	
        where:
            testfile                        | result | startTime | endTime
            "succuess.dat"                  | true   | "2019-12-05T00:01:02" | "2019-12-05T00:01:30"
            "disconnected.dat"              | false  | "2020-06-04T00:19:01" | "2020-06-04T00:19:27"
            "connectedVia4G.dat"            | false  | "2020-06-06T09:51:01" | "2020-06-06T09:51:30"
            "failed_traceRouteTimeout.dat"  | false  | "2020-06-04T21:12:01" | "2020-06-04T21:13:22"
            "failure_DNSlookup.dat"         | false  | "2020-06-04T00:19:01" | "2020-06-04T00:19:27"
            "failure_pingPacketLoss.dat"    | false  | "2020-06-04T00:19:01" | "2020-06-04T00:19:27"
			
    }


}
