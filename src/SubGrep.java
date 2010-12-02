import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;

import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.*;

public class SubGrep {
	
	private	Set<String> keyDB;
	private SubList sub;
	private SnowballStemmer stemmer;
	private boolean doStemming;
	private boolean closedCaption;
	private Matcher captionRegex;
	private Pattern bracketPattern;
	private Vector<SubNode> hits; 
	private XmlOut xmlWriter;
	
	private void parseKeywords (String fname) {
		File keyFile = new File(fname);
		keyDB = new HashSet<String>();
		
		if (!keyFile.exists()){
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
	        
	        if (keyFile != null && br != null) {
                //gather the first group of strings
                try{
                	keyStr = br.readLine();
                } catch (Exception e){
                    e.printStackTrace();
                }
                
                while (keyStr != null) {
                	if (keyStr.length() > 0)
                		if (this.doStemming) {
                			this.stemmer.setCurrent (keyStr);
							this.stemmer.stem();
							keyDB.add(this.stemmer.getCurrent ());
                		} else {
                			keyDB.add (keyStr);
                		}
                    try {
                    	keyStr = br.readLine();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
	        }
		}
	}
	
	private String[] tokenize (String line) {
		// remove all punctuation marks
		String punctFree = line.toLowerCase().replaceAll("\\p{Punct}", "");
		// tokenize
		String[] tokens = punctFree.split ("\\p{Space}");
		return tokens;
	}
	
	private String getStem (String word) {
		this.stemmer.setCurrent (word);
		this.stemmer.stem();
		return this.stemmer.getCurrent ();
	}
	
	private void processTokens (String[] tokens, SubNode n, int weight) {
		for (int i=0; i < tokens.length; ++i) {
			String word = tokens[i];
			if (this.doStemming) {
				word = getStem (tokens[i]);
			}
			
			if (this.keyDB.contains (word)) {
				System.out.println ("!!! in keydb " + word);
				n.addToWeight (weight);
				
				/* add node to the hits db if it's not
				 * already there. 
				 */
				if (!hits.contains (n))
					hits.add (n);
			}
			
		}
	}
	
	private void findKeywords () {
		SubNode curr = this.sub.getHead();

		while (curr != null){
			String[] lines = curr.getLines();
			for(int i = 0; i < lines.length; i++){
				if(lines[i] != null){
					if (this.closedCaption) {
						// create caption regular expression matcher
						if (this.captionRegex == null) {
							this.captionRegex = 
								this.bracketPattern.matcher (lines[i]);
						} else {
							this.captionRegex.reset (lines[i]);
						}
						
						// find the closed captions
						boolean foundCC = false;
						while (this.captionRegex.find ()) {
							foundCC = true;
							String[] tokens = tokenize (this.captionRegex.group (1));
							
							/* add 5 to weight as it's in a closed
							 * caption, that expresses emotions
							 * hopefully
							 */
							processTokens (tokens, curr, 5);
						}
						
						// we don't want to reprocess lines that had captions
						if (foundCC)
							continue;
					} 
					String[] tokens = tokenize (lines[i]);
					processTokens (tokens, curr, 1);
				}
			}

			curr = curr.getNext();
		}
	}
	
    public static void main(String[] args){
    	CommandLineParser parser = new PosixParser();
    	Options options = new Options();
    	
    	options.addOption (OptionBuilder.withLongOpt ("keyfile")
    			.withDescription ("Keywords file")
                .hasArg()
                .withArgName ("KEYFILE")
                .create ());
    	options.addOption ("c", "closed-caption", false, "The given subtitle is closed captioned");
    	options.addOption ("s", "stem", false, "Use stemmer");
    	options.addOption ("o", "output", true, "Output xml");
    	options.addOption (OptionBuilder.withLongOpt ("time-format")
    			.withDescription ("Time format of the subtitles. Default is milli-seconds")
                .hasArg()
                .withArgName ("TIME-FORMAT")
                .create ());
    	
    	SubGrep sg = new SubGrep ();
    	sg.hits = new Vector<SubNode> ();
    	try {
    	    // parse the command line arguments
    	    CommandLine line = parser.parse (options, args);
    	    
    	    if (!line.hasOption("o") || !line.hasOption("output")) {
    	    	// print the output to stdout
    	    	sg.xmlWriter = new XmlOut ();
    	    } else {
    	    	// print the output to the given file
    	    	sg.xmlWriter = new XmlOut (line.getOptionValue ("o"));
    	    }
    	    
    	    if (!line.hasOption ("keyfile")) {
    	        System.out.println ("No keywords file has been given");
    	        System.exit(0);
    	    }
    	    
    	    // parse keywords into Set<String>
    	    sg.parseKeywords (line.getOptionValue("keyfile"));
    	    
    	    // check if we want stemming
    	    if (line.hasOption ("s") || line.hasOption("stem")) {
    	    	sg.doStemming = true;
    	    	sg.stemmer = new englishStemmer ();
    	    } else {
    	    	sg.doStemming = false;
    	    }
    	    
    	    if (line.hasOption('c') || line.hasOption("closed-caption")) {
    	    	sg.closedCaption = true;
    	    	try {
    	    		sg.bracketPattern = Pattern.compile ("\\(([a-z ,]*)\\)");
    	    	} catch (PatternSyntaxException pse) {
    	    		System.out.println("Pattern exception:" + pse.getMessage ());
    	    	} catch (IllegalArgumentException exp) {
    	    		System.out.println("Illegal arg exception:" + exp.getMessage ());
    	    	}
    	    } else {
    	    	sg.closedCaption = false;    	    	
    	    }
    	    
    	    if (line.hasOption ("time-format")) {
    	    	
    	    }
    	    
    	    // load the given srt file
    	    if (line.getArgs().length < 1) {
    	    	if (args.length < 1) {
    	    		System.out.println ("no args");
    	    		System.exit(0);
    	    	}
    	    }
    	    sg.sub = SrtParser.loadFile (line.getArgs()[0]);
    	    
    	    sg.findKeywords ();
    	    
    	    sg.xmlWriter.generate (line.getArgs()[0], sg.hits);
    	    
    	} catch (ParseException exp) {
    	    System.out.println ("Unexpected exception:" + exp.getMessage());
    	}
    }
    
}
