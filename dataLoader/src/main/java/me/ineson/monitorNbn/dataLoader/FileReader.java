/**
 * 
 */
package me.ineson.monitorNbn.dataLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author peter
 *
 */
public class FileReader {

	private RandomAccessFile file;

	private int lineCount = 0;

	public FileReader(File fileToRead) throws FileNotFoundException {
		super();
		this.file = new RandomAccessFile(fileToRead, "r");
	}
	
	public TestSection getNextTestSection() throws IOException {
		TestSection testSection = new TestSection();
		List<String>sectionLines = null;
		
		do {
			long startfilePosition = file.getFilePointer();
			String nextLine = file.readLine();
			if( Objects.isNull( nextLine)) {
				return null;
			}
			
			lineCount++;
			
			if(Objects.isNull(sectionLines)) {
				if( isStartOfTestBlock(nextLine)) {
					sectionLines = new ArrayList<>();
					sectionLines.add(nextLine);
					testSection.setLines(sectionLines);
					
					testSection.setFilePosition(startfilePosition);
					testSection.setFirstLineNumber(lineCount);
				}
			} else {
				sectionLines.add(nextLine);
				
				if( isEndOfTestBlock(nextLine)) {
					testSection.setLastLineNumber(lineCount);
				}
			}
			
		} while( testSection.getLastLineNumber() == 0) ;
		
		return testSection;
	}
	
	
    static boolean isStartOfTestBlock( String line) {
		return ! Objects.isNull(line) && line.startsWith("==== start"); 
	}

    static boolean isEndOfTestBlock( String line) {
		return ! Objects.isNull(line) && line.startsWith("==== end"); 
	}
	
	
}
