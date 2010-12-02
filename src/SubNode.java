import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class SubNode {
    private final static int MAX_LINES = 3;
    
    private int numOfWords;
    private int index;
    private int startTime;
    private int endTime;
    private String lines[];
    private SubNode next;
    private SubNode prev;
    private HashMap<String, LinkedList<IndexItem>> idx;
    
    public SubNode(int i){
    	 	idx = new HashMap<String, LinkedList<IndexItem>> ();
    		numOfWords = 0;
            index = i;
            startTime = 0;
            endTime = 0;
            lines = null;
            next = null;
    }
    
    public void insertNext(int i){
            this.next = new SubNode(i);
            this.next.next = null;
    }
    
    
    public boolean addLine(String s){
            boolean success = false;
            
            if(lines == null){
                    lines = new String[MAX_LINES];
            }
            
            //look for the next available slot to add the string
            for(int i = 0; i < lines.length; i++){
                    if(lines[i] == null){
                            lines[i] = s;
                            success = true;
                            break;
                    }
            }
            
            return success;
    }
    
    
    
    public void printLines(){
            for(int i = 0; i < lines.length; i++){
                    if(lines[i] != null){
                        System.out.println(lines[i]);
                    }
            }
    }
    
    
    
    public void setStartTime(int t){
            this.startTime = t;
    }
    
    public void setEndTime(int t){
            this.endTime = t;
    }
    
    public boolean validateTimes(){
            return ((this.startTime < this.endTime) ? true : false);
    }
    
    public SubNode getNext(){
            return next;
    }
    
    public void setNext(SubNode n){
            this.next = n;
    }
    
    public SubNode getPrevious(){
            return prev;
    }
    
    public void setPrevious(SubNode n){
            this.prev = n;
    }
    
    public int getIndex(){
            return index;
    }
    
    public int getStartTime(){
            return startTime;
    }
    
    public int getEndTime(){
            return endTime;
    }
    
    public String[] getLines(){
            return lines;
    }
    
    public int getNumOfWords () {
    	return numOfWords;
    }
    
    public void setNumOfWords (int n) {
    	numOfWords = n;
    }
    
    public void markWord (String word, int weight, int pos) {
    	LinkedList<IndexItem> bucket;
    	IndexItem it = new IndexItem (weight, pos+numOfWords);
    	if ((bucket = idx.get (word)) == null) {
    		bucket = new LinkedList<IndexItem> ();
    		bucket.add (it);
    		idx.put (word, bucket);
    	} else {
    		bucket.add (it);
    	}
    }
    
    public Set<String> getKeySet () {
    	return idx.keySet();
    }
    
    public Iterator<IndexItem> getBucket (String key) {
    	if (idx.containsKey (key))
    		return idx.get (key).iterator();
    	else 
    		return null;
    }
    
    public void incrNumOfWords (int n) {
    	numOfWords += n;
    }
}
