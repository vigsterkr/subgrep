import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
import java.util.Date;

public class XmlOut {
	
	private StringBuffer sb;
	private File output;
	private OutputStream os;
	private boolean colonTime;
	
	public XmlOut () {
		sb = new StringBuffer ();
		os = System.out;
		colonTime = false;
	}
	
	public XmlOut (String fname) {
		sb = new StringBuffer ();
		colonTime = false;
		
		try {
			output = new File (fname);
			if (!output.canWrite ()) {
				System.out.println ("Cannot write to the given file: "+fname);
				System.exit (-1);
			}
			
			os = new FileOutputStream (output);
		} catch (NullPointerException exp) {
			System.out.println ("Error opening file: "+ exp);
		} catch (SecurityException exp) {
			System.out.println ("Security problem: " + exp);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void generate (String subFile, Vector<SubNode> idx) {
		/* */
		writeHeader (subFile);
		
		Iterator<SubNode> it = idx.iterator();
		while (it.hasNext()) {
			SubNode curr = it.next();
			writeNode (curr);
		}
		
		writeFooter ();
		
		try {
			os.write (sb.toString().getBytes());
			os.flush ();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTimeFormat () {
		colonTime = true;
	}
	
	private void writeHeader (String subFile) {
		Date currTime = new Date ();
		sb.append ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append ("<subgrep>\n");
		sb.append ("\t<date>"+ currTime.toString () +"</date>\n");
		sb.append ("\t<file path=\""+ subFile +"\"/>\n");
		sb.append ("\n");
		sb.append ("\t<nodes>\n");
		//<!DOCTYPE Server SYSTEM "opt/pdos/etc/pdoslrd.dtd">\n");
	}
	
	private void writeFooter () {
		sb.append ("\t</nodes>\n");
		sb.append ("</subgrep>");
	}
	
	private String getColonTimeFormat (int timeInMS) {
		int ms = 0, s = 0, m = 0, h = 0;
		String time;
		
		if (timeInMS > 0) {
			ms = timeInMS % 1000;
			timeInMS /= 1000;
			if (timeInMS > 0) {
				s = timeInMS % 60;
				timeInMS -= s;
				timeInMS /= 60;
				
				if (timeInMS > 0) {
					m = (timeInMS) % 60;
					timeInMS -= m;
					timeInMS /= 60;
					
					if (timeInMS > 0) {
						h = timeInMS % 60;
					}
				}
			}
		
		}
		
		time = String.format ("%02d:%02d:%02d.%03d", h, m, s, ms);
		return time;
	}
	
	private void writeNode (SubNode n) {
		String startTime, endTime;
		int startTimeInt = n.getStartTime ();
		int endTimeInt = n.getEndTime ();
		if (colonTime) {
			startTime = getColonTimeFormat (startTimeInt);
			endTime = getColonTimeFormat (endTimeInt);
		} else {
			startTime = Integer.toString (startTimeInt);
			endTime = Integer.toString (endTimeInt);
		}

		sb.append ("\t\t<node start=\""+ startTime +"\" end=\"" +
				endTime +"\" sum-weight=\""+ n.getWeight() +"\">\n");
		//sb.append ("\t\t\t<keyword value=\"\" w=\"\" pos=\"\"/>\n");
		sb.append ("\t\t</node>\n");
	}
}
