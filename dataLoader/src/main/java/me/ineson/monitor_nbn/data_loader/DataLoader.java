/**
 * 
 */
package me.ineson.monitor_nbn.data_loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jline.internal.Log;
import me.ineson.monitor_nbn.shared.dao.DailySummaryDao;
import me.ineson.monitor_nbn.shared.dao.DatasourceManager;
import me.ineson.monitor_nbn.shared.dao.OutageDao;

/**
 * @author peter
 *
 */
public class DataLoader {

    private static final Logger log = LogManager.getLogger(DataLoader.class);
    
	private static final String OPTTON_HELP = "h";

	private static final String OPTTON_DATAFILE = "f";

	private static final String OPTTON_WIPE = "w"; 

	private static final String OPTTON_TAIL = "t"; 

	private static final String OPTTON_DATABASE_URI = "u"; 

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException {
		
		Options options = new Options();
		options.addOption( Option.builder( OPTTON_HELP)
				.longOpt( "help")
				.desc( "print this message")
				.build());
        options.addOption( Option.builder( OPTTON_DATAFILE)
        		.required()
        		.longOpt( "file")
        		.desc( "The data file to be loaded into the database")
        		.hasArg()
        		.build());
        options.addOption( Option.builder( OPTTON_WIPE)
        		.longOpt( "wipe")
        		.desc( "Wipe the ALL existing data from the database")
        		.build());
        options.addOption( Option.builder( OPTTON_TAIL)
        		.longOpt("tail")
        		.desc("Tails the datafile adding new data to the datadase as and when added to the file")
        		.build());
        options.addOption( Option.builder(OPTTON_DATABASE_URI)
        		.longOpt("uri")
        		.desc("The URI to the mongoDB database, e.g. something like mongodb://username:password@localhost:27017")
        		.hasArg()
        		.build());
		
	    // create the parser
	    CommandLineParser parser = new DefaultParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args);
	        
	        if( line.hasOption(OPTTON_HELP)) {
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp( "DataLoader", options );
	        } else {
	        	final String url = line.getOptionValue(OPTTON_DATABASE_URI);
	        	Log.info("URI = " + url);
	        	final DatasourceManager datasourceManager = StringUtils.isBlank(url) ? new DatasourceManager() : new DatasourceManager(url);
	        	final DailySummaryDao dailySummaryDao = new DailySummaryDao(datasourceManager.getDatabase());
	        	final OutageDao outageDao = new OutageDao(datasourceManager.getDatabase());

	        	if(line.hasOption(OPTTON_WIPE)) {
	        		dailySummaryDao.deleteAll();
	        		outageDao.deleteAll();
	        	}

	            boolean tailFile = line.hasOption(OPTTON_TAIL);

	            String dataFilename = line.getOptionValue(OPTTON_DATAFILE);
	            File dataFile = new File( dataFilename);
	            if (!dataFile.exists() && dataFile.isFile()) {
	            	throw new FileNotFoundException(dataFilename);
	            } else if (!dataFile.canRead()) {
	            	throw new FileNotFoundException("Cannot read file: " + dataFilename);
				}
	            
	            DataFileParser dataFileParser = new DataFileParser();
	            dataFileParser.setDailySummaryDao(dailySummaryDao);
	            dataFileParser.setOutageDao(outageDao);
	            
	            log.info("About to read data from file: {}", dataFile.getAbsolutePath());
	            dataFileParser.processFile(dataFile, tailFile);
	        }
	    
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	}

}
