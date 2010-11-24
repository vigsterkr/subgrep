
public class SubList {
    private SubNode head;
    private SubNode tail;
    private SubNode currNode;
    private int srtBeginTime;
    private int srtEndTime;
    private String subtitlePath;
    
    
    public SubList(){
            head = null;
            tail = head;
            currNode = head;
            srtBeginTime = -1;
            srtEndTime = -1;
            subtitlePath = null;    
    }
    
    

    
    public void appendNode(int i){
            if(this.head == null){
                    this.head = new SubNode(i);
                    this.tail = head;
                    this.currNode = head;
            } else {
                    this.tail.insertNext(i);
                    this.tail = tail.getNext();
            }
    }
    
    
    
    
    public void printScript(){
            SubNode node = head;
            
            while(node != null){
                    node.printLines();
                    node = node.getNext();
                    System.out.println();
            }
    }
    
    public void setSubtitlePath(String path){
            this.subtitlePath = path;
    }
    
    public String getSubtitlePath(){
        return this.subtitlePath;
    }

	public int getBeginTime(){
	        return srtBeginTime;
	}
	
	public int getEndTime(){
	        return srtEndTime;
	}
	
	public SubNode getHead(){
	        return this.head;
	}
	
	public SubNode getTail(){
	        return this.tail;
	}
	
	public SubNode getCurrentNode(){
	        return this.currNode;
	}
	
}
