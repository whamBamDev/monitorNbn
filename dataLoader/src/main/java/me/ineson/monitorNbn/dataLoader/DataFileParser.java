/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author peter
 *
 */
public class DataFileParser {

	
	public void processFile( File dataFile, boolean tailFile, 
			String databaseUri, boolean overwrite) throws IOException {

		//Get date for file/check DB has stats.
		//save, date, number of failures
		FileReader reader = new FileReader(dataFile);
		
		List<String>lines = new ArrayList<>();
		boolean testBlock = false;
		boolean testsFailing = false;
		long filePointer = 0L;
		long linesRead = 0L;
		LocalDateTime outageStartTime;
		LocalDateTime outageEndTime;
		
		while( true) {
			TestSection section = reader.getNextTestSection();
			if( Objects.isNull(section)) {
				// If null is returned then hit the EOF.
				return;
			}
			
			//long lineFilePosition = input file getPos();
			long lineFilePosition = 22L;
			//String nextLine = read.nextLine();
/*			if( testBlock) {
				lines.add(nextLine);
				if( AbstractTestVerifier.isEndOfTestBlock( nextLine)) {
					//boolean testFailed = parseInput();
					boolean testFailed = true;
					
					if( testFailed && ! testsFailing) {
						testsFailing = true;
						//outageStartTime = read datetime from start line;
						
					} if( ! testFailed && testsFailing) {
						//outageEndTime = read datetime from end line;
						//wrtie/update db;
						//save start/end time, 
						testsFailing = false;
						outageStartTime = null;
						outageEndTime = null;
						lines.clear();
					}

					testBlock = false;
				}
				
			} else if( AbstractTestVerifier.isStartOfTestBlock( nextLine)) {
				testBlock = true;
				// if ! testsFailing grab start time?
				//get version and parser.
                if( ! testsFailing) {
    				filePointer = lineFilePosition;
    				linesRead = 0L;
                }

			}
*/			
		}
		
	}
			
}
