/**
 * 
 */
package me.ineson.monitorNbn.shared.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author peter
 *
 */
public class FileReader implements Closeable {

    /**
     * When tailing a file this is the interval (in milliseconds) between checks for reading more data from the file. 
     * Currently set to 10 minutes.  
     */
    private static final long TAIL_INTERVAL = 10L * 1000L;

	private LocalDateTime timeoutTime = null;

	private RandomAccessFile file;

	private int lineCount = 0;

	public FileReader(File fileToRead) throws FileNotFoundException {
		super();
		this.file = new RandomAccessFile(fileToRead, "r");
	}
	
	public FileReader(File fileToRead, boolean tailFile) throws FileNotFoundException {
		super();
		this.file = new RandomAccessFile(fileToRead, "r");
		if (tailFile) {
			timeoutTime = LocalDate.now().atStartOfDay().plusDays(1L).plusMinutes(10L);
		}
	}

	public TestSection getNextTestSection() throws IOException {
		TestSection testSection = new TestSection();
		List<String>sectionLines = null;
		
		do {
			long startfilePosition = file.getFilePointer();
			String nextLine = readLineFromFile();

			if (Objects.isNull(nextLine)) {
				return null;
			}
			
			lineCount++;
			
			if (Objects.isNull(sectionLines)) {
				if( isStartOfTestBlock(nextLine)) {
					sectionLines = new ArrayList<>();
					sectionLines.add(nextLine);
					testSection.setLines(sectionLines);
					
					testSection.setFilePosition(startfilePosition);
					testSection.setFirstLineNumber(lineCount);
				}
			} else {
				sectionLines.add(nextLine);
				
				if (isEndOfTestBlock(nextLine)) {
					testSection.setLastLineNumber(lineCount);
				}
			}
			
		} while( testSection.getLastLineNumber() == 0) ;
		
		return testSection;
	}

    public void seek(long pos) throws IOException {
        file.seek(pos);
	}

    public String getLine() throws IOException {
        return file.readLine();
	}

    public List<String> getLines(int count) throws IOException {
        List<String> lines = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) {
            lines.add(getLine());
        }
        return lines;
	}

    public void close() throws IOException {
        if( file != null) {
            RandomAccessFile temp = file;
            file = null;
            temp.close();
        }
    }

	
    static boolean isStartOfTestBlock( String line) {
        return ! Objects.isNull(line) && line.startsWith("==== start"); 
    }

    static boolean isEndOfTestBlock( String line) {
        return ! Objects.isNull(line) && line.startsWith("==== end"); 
    }

    private final String readLineFromFile() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;
        
        while (!eol) {
            switch (c = file.read()) {
            case -1:
                if (Objects.isNull(timeoutTime)) {
                    eol = true;
                } else if (LocalDateTime.now().isAfter(timeoutTime)) {
                    if(input.length() > 0 ) {
                        // Hit the timeout time, if there is that has been read then return it to the caller.
                        // Timeout will occur the next time the method is called. 
                        eol = true;
                    } else {
                        throw new IOException("Timeout occured tailing file");
                    }
                } else {
                    try {
                        Thread.sleep(TAIL_INTERVAL);
                    } catch (InterruptedException e) {
                        throw new IOException("Error occured when tailing a file.",e);
                    }
                }
                break;
            case '\n':
                eol = true;
                break;
            case '\r':
                eol = true;
                long cur = file.getFilePointer();
                if ((file.read()) != '\n') {
                    file.seek(cur);
                }
                break;
            default:
                input.append((char)c);
                break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }

}
