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
			testOutcome.getEndTime() != null
			
	
        where:
            testfile                        | result | startTime | endTime
            "succuess.dat"                  | true   | "" | ""
            "disconnected.dat"              | false  | "" | ""
            "connectedVia4G.dat"            | false  | "" | ""
            "failed_traceRouteTimeout.dat"  | false  | "" | ""
            "failure_DNSlookup.dat"         | false  | "" | ""
            "failure_pingPacketLoss.dat"    | false  | "" | ""
			
    }


}
