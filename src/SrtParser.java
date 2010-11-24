/**
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;



/**
 * based on the SrtParser in JSubtitlePlayer
 * @author wiking
 *
 */
public class SrtParser {

	private enum SrtStage {
        INDEX, TIME, LINE1, LINE2, LINE3, BREAK
    }

	public static SubList loadFile(String path){
        SubList list = null;
        File srtFile = new File(path);
        
        if(!srtFile.exists()){
                String errMsg = "File does not exist: \n"+srtFile.getAbsolutePath();
                System.err.println(errMsg);
        } else {
                list = loadSubtitle(srtFile);
        }
        
        return list;
	}

    private static SubList loadSubtitle(File file){
        FileReader fr = null;
        BufferedReader br = null;
        String str = null;
        SrtStage stage = SrtStage.INDEX;
        int i = 0;
        int count = 0;
        SubList list = new SubList();
        
        try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
        } catch (Exception e){
                e.printStackTrace();
        }
        
        if(file != null && br != null){
                //gather the first group of strings
                try{
                        str = br.readLine();
                } catch(Exception e){
                        e.printStackTrace();
                }
                
                while(str != null){
                        count++;
                        
                        //determines where to go next
                        switch(stage){
                        case INDEX:
                                stage = (str.length() > 0) ? SrtStage.INDEX : SrtStage.BREAK;
                                break;
                        case TIME:
                                stage = (str.length() > 0) ? SrtStage.TIME : SrtStage.BREAK;
                                break;
                        case LINE1:
                                stage = (str.length() > 0) ? SrtStage.LINE1 : SrtStage.BREAK;
                                break;
                        case LINE2:
                                stage = (str.length() > 0) ? SrtStage.LINE2 : SrtStage.BREAK;
                                break;
                        case LINE3:
                                stage = (str.length() > 0) ? SrtStage.LINE3 : SrtStage.BREAK;
                                break;
                        case BREAK:
                                stage = (str.length() > 0) ? SrtStage.INDEX : SrtStage.BREAK;
                                break;
                        default:
                                //should never get here
                            System.out.println("WTF!?!");
                            stage = SrtStage.INDEX;
                            break;
                    }
                    
                    //action on each stage
                    switch(stage){
                    case INDEX:
                            try {
                                    i = Integer.parseInt(str);
                            } catch(Exception e){
                                    i = count;
                            }
                            
                            list.appendNode(i);
                            
                            stage = SrtStage.TIME;
                            break;
                    case TIME:
                            list.getTail().setStartTime(extractStartTime(str));
                            list.getTail().setEndTime(extractEndTime(str));
                            stage = SrtStage.LINE1;
                            break;
                    case LINE1:
                            list.getTail().addLine(str);
                            stage = SrtStage.LINE2;
                            break;
                    case LINE2:
                            list.getTail().addLine(str);
                            stage = SrtStage.LINE3;
                            break;
                    case LINE3:
                            list.getTail().addLine(str);
                            stage = SrtStage.BREAK;
                            break;
                    case BREAK:
                            //do nothing
                            stage = SrtStage.INDEX;
                            break;
                    default:
                            //should never get here
                            System.out.println("WTF-2!?!");
                            break;
                    }
                    
                    //gather another string
                    try{
                        str = br.readLine();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
        }

        return list;
    }

	
	
	
	private static int extractStartTime(String s){
		int srtBeginTime = -1;          
		int time = extractTimeHelper(s, true);
		
		if(srtBeginTime == -1 || time < srtBeginTime){
		        srtBeginTime = time;
		}
		return time;
	}


	private static int extractEndTime(String s){
		int srtEndTime = -1;
		int time = extractTimeHelper(s, false);
		
		if(srtEndTime == -1 || time > srtEndTime){
		        srtEndTime = time;
		}
		return time;
	}

	
	private static int extractTimeHelper(String s, boolean isStartTime){
		short hour = 0, minute = 0, second = 0, millisec = 0;
		String divider = " --> ";
		String format = "00:00:00,000";
		String timeStr = null;
		int time = 0;
		
		//get the corresponding time string
		if(isStartTime){
		        timeStr = s.substring(0, format.length());
		} else {
		    int offset = format.length()+divider.length();
		    timeStr = s.substring(offset, offset + format.length());
		}
		
		//calculate the time
		if(timeStr != null){
		    if(timeStr.length() == format.length()){
		            try {
		                    int colon1 = timeStr.indexOf(':', 0);
		                    hour = Short.parseShort(timeStr.substring(0, colon1));
		                    
		                    int colon2 = timeStr.indexOf(':', colon1+1);
		                    minute = Short.parseShort(timeStr.substring(colon1+1, colon2));
		                    
		                    int comma = timeStr.indexOf(',', colon2+1);
		                    second = Short.parseShort(timeStr.substring(colon2+1, comma));
		                    
		                    millisec = Short.parseShort(timeStr.substring(comma+1, timeStr.length()));
		                    
		                    time = millisec + (second * 1000) + (minute * 1000 * 60) + (hour * 1000 * 60 * 60);
		            } catch(Exception e){
		                    //do nothing
		            }
		    }
		}
		
		return time;
	}
	
}
