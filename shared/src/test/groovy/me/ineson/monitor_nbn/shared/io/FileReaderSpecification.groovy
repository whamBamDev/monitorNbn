package me.ineson.monitor_nbn.shared.io;

import me.ineson.monitor_nbn.shared.io.FileReader
import me.ineson.monitor_nbn.shared.io.TestSection
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


	def "test reading an empty data file with no results"() {
        given:
            URL testFile = Thread.currentThread().getContextClassLoader()
			    .getResource( "me/ineson/monitor_nbn/shared/io/fileReader_empty.dat");
			FileReader fileReader = new FileReader( new File( testFile.getFile()));
              
        when:
            TestSection testSection = fileReader.getNextTestSection();

        then:
            testSection == null;

        cleanup:
            if( ! Objects.isNull( fileReader)) {
                fileReader.close();
            }
	
    }

    def "test reading a data file with one result"() {
        given:
            URL testFile = Thread.currentThread().getContextClassLoader()
                .getResource( "me/ineson/monitor_nbn/shared/io/fileReader_succuess.dat");
            FileReader fileReader = new FileReader( new File( testFile.getFile()));
              
        when:
            TestSection testSection1 = fileReader.getNextTestSection();
            TestSection testSection2 = fileReader.getNextTestSection();

        then:
            testSection1 != null;
            testSection1.firstLineNumber == 1
            testSection1.lastLineNumber > 50
            testSection1.lines != null
            testSection1.lines.size > 50
            testSection1.lines.size < 70
            testSection2 == null;

        cleanup:
            if( ! Objects.isNull( fileReader)) {
                fileReader.close();
            }
    
    }

    def "test reading a data file with two results"() {
        given:
            URL testFile = Thread.currentThread().getContextClassLoader()
                .getResource( "me/ineson/monitor_nbn/shared/io/fileReader_twoResults.dat");
            System.out.println("File Found2 : " + testFile.getFile())
            FileReader fileReader = new FileReader( new File( testFile.getFile()));
              
        when:
            TestSection testSection1 = fileReader.getNextTestSection();
            System.out.println("Test Section1: " + testSection1)
            TestSection testSection2 = fileReader.getNextTestSection();
            System.out.println("Test Section2: " + testSection2)
           

        then:
            testSection1 != null;
            testSection1.filePosition == 0
            testSection1.firstLineNumber == 1
            testSection1.lastLineNumber > 50
            testSection1.lines != null
            testSection1.lines.size > 50
            testSection1.lines.size < 70
            testSection2 != null;
            testSection2.filePosition > 1000
            testSection2.firstLineNumber > 50
            testSection2.firstLineNumber < 70
            testSection2.lastLineNumber > 100
            testSection2.lines != null
            testSection2.lines.size > 50
            testSection2.lines.size < 70

        cleanup:
            if( ! Objects.isNull( fileReader)) {
                fileReader.close();
            }
    
    }

	def "test seek and getLine success"() {
		given:
			URL testFile = Thread.currentThread().getContextClassLoader()
				.getResource( "me/ineson/monitor_nbn/shared/io/fileReader_lineRead.dat");
			FileReader fileReader = new FileReader( new File( testFile.getFile()));
			  
		when:
			fileReader.seek(4L);
			String line = fileReader.getLine();
		   

		then:
		    line == "22"

		cleanup:
			if( ! Objects.isNull( fileReader)) {
				fileReader.close();
			}
	
	}

	def "test seek and getLines success"() {
		given:
			URL testFile = Thread.currentThread().getContextClassLoader()
				.getResource( "me/ineson/monitor_nbn/shared/io/fileReader_lineRead.dat");
			System.out.println("File Found2 : " + testFile.getFile())
			FileReader fileReader = new FileReader( new File( testFile.getFile()));
			  
		when:
			fileReader.seek(12L);
			def lines = fileReader.getLines(3);
			System.out.println("Lines : " + lines)
			

		then:
			lines == [ "44", "55", "66"];

		cleanup:
			if( ! Objects.isNull( fileReader)) {
				fileReader.close();
			}
	
	}

}