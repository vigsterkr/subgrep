import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OpenSubtitles {

	private XmlRpcClient client = null;
	private String token = null;

	private static String userAgent = "OS Test User Agent";
	private static String osUser;
	private static String osPasswd;
	private static String osLang;
	
	public OpenSubtitles (String user, String passwd, String lang) {
		osUser = user;
		osPasswd = passwd;
		osLang = lang;
		
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL (new URL("http://api.opensubtitles.org/xml-rpc"));
			client = new XmlRpcClient();
			client.setConfig(config);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean login () {
		if (token != null) {
			try {
				Object[] params = new Object[]{token};
				Map ret = (Map) client.execute("NoOperation", params);
				String status = (String) ret.get("status");
				if (!status.equals("200 OK")) {
					token = null;
					login ();
				}
			} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		try {
			Object[] params = new Object[]{osUser, osPasswd, osLang, userAgent};
			HashMap<?,?> status = (HashMap<?,?>) client.execute("LogIn", params);
			String st = (String) status.get("status");
			if (st.equals("200 OK")) {
				token = (String) status.get("token");
				return true;
			}
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	private void logout () {
		try {
			Object[] params = new Object[]{token};
			client.execute("LogOut", params);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseSearchResult (Map searchResult) {
        Object data = searchResult.get("data");
        if (data instanceof Object[]) {
            for (Object element : (Object[]) data) {
                Map entry = (Map) element;
                System.out.println (entry.get("MovieReleaseName")
                		+ " " + entry.get("SubHearingImpaired") 
                		+ " " + entry.get("IDSubtitleFile"));
            }
        }
	}
	
	public void search (long imdbid) {
		if (!login ()) {
			System.out.println ("Could not log in!");
		}
		
		try {
			Map<String, Map<String, String>> searchInfo = new HashMap<String, Map<String, String>>();
	        Map<String, String> movieInfo = new HashMap<String, String>();
	        movieInfo.put("imdbid", Long.toString (imdbid));
	        movieInfo.put("sublanguageid", "eng");
	        searchInfo.put("1", movieInfo);
	        Object[] params = new Object[] {token, searchInfo};
	        Map searchResult = (Map) client.execute("SearchSubtitles", params);
	        parseSearchResult (searchResult);	        
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logout ();

	}
	
	public void search (String movieHash, long byteSize) {
		if (!login ()) {
			System.out.println ("Could not log in!");
		}
		
		try {
			Map<String, Map<String, String>> searchInfo = new HashMap<String, Map<String, String>>();
	        Map<String, String> movieInfo = new HashMap<String, String>();
	        movieInfo.put("moviehash", movieHash);
	        movieInfo.put("moviebytesize", Long.toString(byteSize));
	        movieInfo.put("sublanguageid", "eng");
	        searchInfo.put("1", movieInfo);
	        Object[] params = new Object[] {token, searchInfo};
	        Map searchResult = (Map) client.execute("SearchSubtitles", params);
	        parseSearchResult (searchResult);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logout ();
	}

	public void search (String movie) {
		if (!login ()) {
			System.out.println ("Could not log in!");
		}
		
		try {
			Map<String, Map<String, String>> searchInfo = new HashMap<String, Map<String, String>>();
	        Map<String, String> movieInfo = new HashMap<String, String>();
	        movieInfo.put("query", movie);
	        movieInfo.put("sublanguageid", "eng");
	        searchInfo.put("1", movieInfo);
	        Object[] params = new Object[] {token, searchInfo};
	        Map searchResult = (Map) client.execute("SearchSubtitles", params);
	        parseSearchResult (searchResult);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logout ();
	}
	
	public void download (String subId) {
		try {
			Object[] params = new Object[]{token, subId};
			Map downResult = (Map) client.execute("DownloadSubtitles", params);
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
    	CommandLineParser parser = new PosixParser();
    	Options options = new Options();
    	
    	options.addOption (OptionBuilder.withLongOpt ("username")
    			.withDescription ("Open Subtitles username")
                .hasArg()
                .withArgName ("USERNAME")
                .create ());
    	options.addOption (OptionBuilder.withLongOpt ("passwd")
    			.withDescription ("Open Subtitles password")
                .hasArg()
                .withArgName ("PASSWORD")
                .create ());
    	
    	options.addOption (OptionBuilder.withLongOpt ("langs")
    			.withDescription ("Languges in a comma separated list")
                .hasArg()
                .withArgName ("LANG1, LANG2")
                .create ());
    	
    	options.addOption (OptionBuilder.withLongOpt ("imdbid")
    			.withDescription ("Find subtitles with imdbid")
                .hasArg()
                .withArgName ("IMDBID")
                .create ());
    	
    	options.addOption (OptionBuilder.withLongOpt ("query")
    			.withDescription ("Query by string")
                .hasArg()
                .withArgName ("QUERY")
                .create ());
    	
    	try {
    	    // parse the command line arguments
    	    CommandLine line = parser.parse (options, args);
    	    
    	    if (!line.hasOption("username")) {
    	        System.out.println ("No username given!");
    	        System.exit(0);
    	    }
    	    
    	    if (!line.hasOption ("passwd")) {
    	        System.out.println ("No password given for OS");
    	        System.exit(0);
    	    }
    	   
    	    if (!line.hasOption ("passwd")) {
    	        System.out.println ("No languages have been given");
    	        System.exit(0);
    	    }
    	    
    	    OpenSubtitles os = 
    	    	new OpenSubtitles (line.getOptionValue("username"), line.getOptionValue("passwd"), line.getOptionValue("langs"));
    	    
    	    if (line.hasOption ("imdbid")) {
    	    	os.search (line.getOptionValue("imdbid"));
    	    	System.exit (0);
    	    }
    	    
    	    if (line.hasOption("query")) {
    	    	os.search (line.getOptionValue("query"));
    	    	System.exit (0);    	    	
    	    }
    	    
    	    if (line.getArgs().length > 0) {
    	    	if (args.length > 0) {
    	    		File movie = new File (line.getArgs()[0]);
    	    		try {
						String movieHash = OpenSubtitlesHasher.computeHash (movie);
						os.search (movieHash, movie.length());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	    	}
    	    }    	
    	    
    	    System.out.println ("end");
    	    
    	} catch (ParseException exp) {
    	    System.out.println ("Unexpected exception:" + exp.getMessage());
    	}
	}
}
