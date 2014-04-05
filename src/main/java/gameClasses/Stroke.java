package gameClasses;

public class Stroke{
    private int to_x,to_y,from_x,from_y;
    private String status="",color="";
    private char next='0';

    public Stroke(int x1, int y1, int x2, int y2,String st){
        to_x=x1; to_y=y1;
        from_x=x2; from_y=y2;
        status=st;
    }

    public Stroke(int x1, int y1, int x2, int y2, String st, String col){
        to_x=x1; to_y=y1;
        from_x=x2; from_y=y2;
        status=st;
        color=col;
    }

    public Stroke(int x1, int y1, int x2, int y2, String st, String col,char next){
        to_x=x1; to_y=y1;
        from_x=x2; from_y=y2;
        status=st;
        color=col;
        this.next=next;
    }

    public Stroke(){
        to_x=to_y=from_x=from_y=-1;
    }

    public Stroke(String stat){
        to_x=to_y=from_x=from_y=-1;
        status=stat;
    }

    public Stroke(Stroke stroke){
        to_x=stroke.getTo_X();
        to_y=stroke.getTo_Y();
        from_x=stroke.getFrom_X();
        from_y=stroke.getFrom_Y();
        status=stroke.getStatus();
        color=stroke.getColor();
        next=stroke.getNext();
    }

    public Stroke getInverse(){
        if(color == "b")
            return new Stroke(7-to_x, 7-to_y, 7-from_x, 7-from_y,status,"w",next);
        else
            return new Stroke(7-to_x, 7-to_y, 7-from_x, 7-from_y,status,"b",next);
    }

    public void clear(){
        to_x=to_y=from_x=from_y=-1;
        status=color="";
        next='\0';
    }

    public boolean isEmpty(){
        if((to_x!=-1)||(to_y!=-1)||(from_x!=-1)||(from_y!=-1))
            return false;
        return true;
    }

    public int getTo_X(){
        return to_x;
    }

    public int getTo_Y(){
        return to_y;
    }

    public int getFrom_X(){
        return from_x;
    }

    public int getFrom_Y(){
        return from_y;
    }

    public String getStatus(){
        return status;
    }

    public String getColor(){
        return color;
    }

    public char getNext(){
        return next;
    }

    public void setTo_X(int x){
        to_x=x;
    }

    public void setTo_Y(int y){
        to_y=y;
    }

    public void setFrom_X(int x){
        from_x=x;
    }

    public void setFrom_Y(int y){
        from_y=y;
    }

    public void setStatus(String st){
        status=st;
    }

    public void setColor(String col){
        color=col;
    }

    public void setNext(char next){
        this.next=next;
    }

    public void fullSet(int x1, int y1, int x2, int y2){
        to_x=x1; to_y=y1;
        from_x=x2; from_y=y2;
    }

    public String toString(){
        return "{\"color\":\""+color+"\",\"to_x\":"+to_x+",\"to_y\":"+to_y+
                ",\"from_x\":"+from_x+",\"from_y\":"+from_y+
                ",\"status\":\""+status+"\",\"next\":\""+next+"\"}";
    }

}