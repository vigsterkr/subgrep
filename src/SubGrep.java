import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;

public class SubGrep {
	
	private	Set<String> keyDB;
	private SubList sub;
	
	private void parseKeywords (String fname) {
		File keyFile = new File(fname);
		keyDB = new HashSet<String>();
		
		if(!keyFile.exists()){
			String errMsg = "File does not exist: \n"+keyFile.getAbsolutePath();
			System.err.println(errMsg);
		} else {
			FileReader fr = null;
	        BufferedReader br = null;
	        String keyStr = null;
	        
	        try {
	        	fr = new FileReader(keyFile);
	        	br = new BufferedReader(fr);
	        } catch (Exception e){
	        	e.printStackTrace();
	        }
	        
	        if(keyFile != null && br != null) {
                //gather the first group of strings
                try{
                	keyStr = br.readLine();
                } catch (Exception e){
                    e.printStackTrace();
                }
                
                while (keyStr != null) {
                	if (keyStr.length() > 0)
                		keyDB.add (keyStr);
                	
                    try{
                    	keyStr = br.readLine();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
	        }
		}
	}
	
    public static void main(String[] args){
    	CommandLineParser parser = new PosixParser();
    	Options options = new Options();
    	
    	options.addOption (OptionBuilder.withLongOpt ("keyfile")
    			.withDescription ("Keywords")
                .hasArg()
                .withArgName ("KEYFILE")
                .create ());
    	
    	SubGrep sg = new SubGrep ();
    	try {
    	    // parse the command line arguments
    	    CommandLine line = parser.parse (options, args);

    	    // validate that block-size has been set
    	    if (!line.hasOption ("keyfile")) {
    	        // print the value of block-size
    	        System.out.println ("No keywords file has been given");
    	        System.exit(0);
    	    }
    	    
    	    // parse keywords into Set<String>
    	    sg.parseKeywords (line.getOptionValue("keyfile"));
    	    
    	    // load the give srt file
    	    if (line.getArgs().length < 1) {
    	    	if (args.length < 1) {
    	    		System.out.println ("no args");
    	    		System.exit(0);
    	    	}
    	    }
    	    sg.sub = SrtParser.loadFile(line.getArgs()[0]);
    	    
    	    
    	    
    	} catch (ParseException exp) {
    	    System.out.println ("Unexpected exception:" + exp.getMessage());
    	}
    }
    
}
