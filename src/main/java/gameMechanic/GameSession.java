package gameMechanic;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import resource.GameSettings;
import resource.ResourceFactory;

import gameClasses.Field;
import gameClasses.Field.checker;
import gameClasses.Snapshot;
import utils.TimeHelper;
import utils.VFS;

public class GameSession{
	private static String dirForLog; 
	private static AtomicInteger creatorId=new AtomicInteger();
	private int whiteId;
	private int blackId;
	private int lastStroke;
	private int blackQuantity,whiteQuantity;
	private int id=creatorId.incrementAndGet();
	private long lastStrokeTime = TimeHelper.getCurrentTime();
	private Field[][] currentPositions;
	private StringBuilder log = new StringBuilder();
	final private GameSettings settings;

	static{
		dirForLog = TimeHelper.getGMT();
		File dir = new File("log/"+dirForLog);
		dir.mkdirs();
	}
	
	public GameSession(int id1, int id2){
		settings = (GameSettings) ResourceFactory.instanse().getResource("settings/gameSettings.xml");
		descInit(id1, id2);
	}

	public GameSession(int id1, int id2,int fieldSize, int playerSize){
		settings = new GameSettings(fieldSize,playerSize);
		descInit(id1, id2);
	}

	private void descInit(int id1, int id2) {
		currentPositions = new Field[settings.getFieldSize()][settings.getFieldSize()];
		blackQuantity = whiteQuantity = settings.getFieldSize()*settings.getPlayerSize()/2;
		whiteId=id1;
		lastStroke=blackId=id2;
		for(int y=0;y<settings.getFieldSize();y++){
			if(y<settings.getPlayerSize()){
				generateLine(y, checker.white,isOdd(y));
			}
			else if (y>=settings.getFieldSize()-settings.getPlayerSize()){
				generateLine(y, checker.black,isOdd(y));
			}
			else{
				generateEmptyLine(y);
			}
		}
	}

	private void generateEmptyLine(int y) {
		for(int x=0;x<settings.getFieldSize();x++)
			generateField(x, y,checker.nothing);
	}

	private void generateLine(int y, checker color, boolean needOdd) {
		for(int x=0;x<settings.getFieldSize();x++)
			if(isOdd(x)==needOdd)
				generateField(x, y,color);
			else
				generateField(x, y,checker.nothing);
	}

	private boolean isOdd(int number){
		return number%2==1;
	}

	private void generateField(int x, int y,checker field) {
		currentPositions[y][x]=new Field(field);
	}

	private checker getAnotherColor(checker myColor){
		if(myColor==checker.black)
			return checker.white;
		else if(myColor==checker.white)
			return checker.black;
		else
			return checker.nothing;
	}

	public boolean checkStroke(int id, int from_x, int from_y, int to_x, int to_y){
		String inLog="gameSession.checkStroke("+String.valueOf(id)+','+String.valueOf(from_x)+','+String.valueOf(from_y)+','+String.valueOf(to_x)+','+String.valueOf(to_y)+");\n";
		boolean changeId=true;
		if(id==whiteId){
			to_y=settings.getFieldSize()-1-to_y;
			from_y=settings.getFieldSize()-1-from_y;
		}
		else{
			from_x=settings.getFieldSize()-1-from_x;
			to_x=settings.getFieldSize()-1-to_x;
		}
		if(!checking(id,from_x, from_y, to_x, to_y))
			return false;
		if (eating(from_x, from_y, to_x, to_y)){
			if(!checkEating(from_x, from_y, to_x, to_y)){
				System.err.println("false4");
				return false;
			}
			changeId=!makeEatingStroke(from_x, from_y, to_x, to_y);
		}
		else{
			if(!makeUsualStroke(from_x, from_y, to_x, to_y)){
				System.err.println("false5");
				return false;
			}
		}
		if(changeId)
			lastStroke=id;
		lastStrokeTime=TimeHelper.getCurrentTime();
		log.append(inLog);
		return true;
	}

	private boolean checkEating(int from_x, int from_y, int to_x, int to_y){
		if(!fieldIsKing(from_x, from_y))
			return true;
		else
			return !checkKingOtherEating(from_x, from_y, to_x, to_y);
	}
	
	private boolean checkKingOtherEating(int from_x, int from_y, int to_x, int to_y){
		checker anotherColor = getAnotherColor(getFieldType(from_x, from_y));
		int on_x=normal(to_x-from_x), on_y=normal(to_y-from_y);
		int x=from_x, y=from_y;
		while(getFieldType(x,y)!=anotherColor){
			x+=on_x; y+=on_y;
		}
		Field eatingField = new Field(getField(x,y));
		int eatingFieldX = x, eatingFieldY = y;
		clearField(x, y);
		boolean ans=checkOtherEatingOpportunity(x,y,from_x, from_y, to_x, to_y);
		getField(eatingFieldX, eatingFieldY).make(eatingField);
		return ans;
	}

	private boolean checkOtherEatingOpportunityForField(int from_x, int from_y, int x, int y) {
		Field wasField=new Field();
		boolean ans=false;
		wasField.make(getField(x,y));
		getField(x,y).make(getField(from_x, from_y));
		if(canEat(x,y)){
			ans=true;
		}
		getField(x,y).make(wasField);
		return ans;
	}

	private boolean checkOtherEatingOpportunity(int x, int y, int from_x, int from_y, int to_x, int to_y){
		int on_x = normal(to_x-from_x), on_y=normal(to_y-from_y);
		boolean ans=false;
		for(x+=on_x,y+=on_y;inBorder(x)&&(inBorder(y));x+=on_x, y+=on_y){
			if((x==to_x)&&(y==to_y))
				continue;
			ans |= checkOtherEatingOpportunityForField(from_x, from_y, x, y);
		}
		return ans;
	}
	
	private Field getField(int x, int y){
		return currentPositions[y][x];
	}
	
	private checker getPlayerColor(int id){
		if(id == whiteId)
			return checker.white;
		else 
			return checker.black;
	}
	
	private boolean checking(int id,int from_x, int from_y, int to_x, int to_y){
		if(id==lastStroke){
			System.err.println("false1");
			return false;
		}
		if(!standartCheck(from_x, from_y, to_x, to_y)){
			System.err.println("false2");
			return false;
		}
		checker myColor = getPlayerColor(id);
		if(getFieldType(from_x, from_y)!=myColor){
			System.err.println("false3");
			return false;
		}
		return true;
	}
	
	private boolean makeEatingStroke(int from_x, int from_y, int to_x, int to_y){
		eat(from_x, from_y,to_x, to_y);
		if(becameKing(to_x, to_y)){
			makeKing(to_x, to_y);
		}
		return canEat(to_x,to_y);	
	}
	
	private boolean makeUsualStroke(int from_x, int from_y, int to_x, int to_y){
		checker myColor = getFieldType(from_x, from_y);
		if(canEat(myColor)){
			return false;
		}
		move(from_x, from_y, to_x, to_y);
		if(becameKing(to_x, to_y)){
			makeKing(to_x, to_y);
		}
		return true;
	}
	
	private void makeKing(int x, int y){
		currentPositions[y][x].makeKing();
	}
	
	private boolean becameKing(int x, int y) {
		checker myColor = getFieldType(x,y);
		return ((myColor==checker.black)&&(y==0))||((myColor==checker.white)&&(y==settings.getFieldSize()-1));
	}

	private boolean fieldIsKing(int x, int y) {
		return currentPositions[y][x].isKing();
	}

	private checker getFieldType(int x, int y) {
		return currentPositions[y][x].getType();
	}

	private boolean canEat(int x, int y){
		if(fieldIsKing(x,y))
			return kingCanEat(x,y);
		else
			return pawnCanEat(x,y);
	}

	private boolean pawnCanEatRightUp(int x, int y){
		checker anotherColor=getAnotherColor(getFieldType(x,y));
		return (y<settings.getFieldSize()-2)&&(x<settings.getFieldSize()-2)&&(getFieldType(x+1,y+1)==anotherColor)&&(fieldIsEmpty(x+2,y+2));
	}

	private boolean pawnCanEatRightDown(int x, int y){
		checker anotherColor=getAnotherColor(getFieldType(x,y));
		return (y>1)&&(x<settings.getFieldSize()-2)&&(getFieldType(x+1,y-1)==anotherColor)&&(fieldIsEmpty(x+2,y-2));
	}

	private boolean pawnCanEatLeftUp(int x, int y){
		checker anotherColor=getAnotherColor(getFieldType(x,y));
		return (y<settings.getFieldSize()-2)&&(x>1)&&(getFieldType(x-1,y+1)==anotherColor)&&(fieldIsEmpty(x-2,y+2));
	}

	private boolean pawnCanEatLeftDown(int x, int y){
		checker anotherColor=getAnotherColor(getFieldType(x,y));
		return (y>1)&&(x>1)&&(getFieldType(x-1,y-1)==anotherColor)&&(fieldIsEmpty(x-2,y-2));
	}

	private boolean pawnCanEat(int x, int y){
		return pawnCanEatRightUp(x,y)||pawnCanEatLeftUp(x,y)||pawnCanEatRightDown(x,y)||pawnCanEatLeftDown(x,y);
	}

	private boolean kingCanEatRightUp(int x, int y){
		checker myColor=getFieldType(x,y), anotherColor=getAnotherColor(myColor);
		for(int counter=1;counter<settings.getFieldSize();counter++){
			if((x+counter>=settings.getFieldSize()-2)||(y+counter>=settings.getFieldSize()-2)
					||(getFieldType(x+counter,y+counter)==myColor))
				return false;
			if(getFieldType(x+counter,y+counter)==anotherColor){
				return fieldIsEmpty(x+counter+1,y+counter+1);
			}
		}
		return false;
	}

	private boolean kingCanEatLeftUp(int x, int y){
		checker myColor=getFieldType(x,y), anotherColor=getAnotherColor(myColor);
		for(int counter=1;counter<settings.getFieldSize();counter++){
			if((x-counter<=1)||(y+counter>=settings.getFieldSize()-2)
					||(getFieldType(x-counter,y+counter)==myColor))
				return false;
			if(getFieldType(x-counter,y+counter)==anotherColor){
				return fieldIsEmpty(x-counter-1,y+counter+1);
			}
		}
		return false;
	}

	private boolean kingCanEatRightDown(int x, int y){
		checker myColor=getFieldType(x,y), anotherColor=getAnotherColor(myColor);
		for(int counter=1;counter<settings.getFieldSize();counter++){
			if((x+counter>=settings.getFieldSize()-2)||(y+counter<=1)
					||(getFieldType(x+counter,y-counter)==myColor))
				return false;
			if((x+counter>=settings.getFieldSize())||(y-counter<=0))
				return false;
			if(getFieldType(x+counter,y-counter)==anotherColor){
				return fieldIsEmpty(x+counter+1,y-counter-1);
			}
		}
		return false;
	}

	private boolean kingCanEatLeftDown(int x, int y){
		checker myColor=getFieldType(x,y), anotherColor=getAnotherColor(myColor);
		for(int counter=1;counter<settings.getFieldSize();counter++){
			if((x-counter<=1)||(y-counter<=1)||(getFieldType(x-counter,y-counter)==myColor))
				return false;
			if(getFieldType(x-counter,y-counter)==anotherColor){
				return fieldIsEmpty(x-counter-1,y-counter-1);
			}
		}
		return false;
	}

	private boolean kingCanEat(int x, int y){
		return kingCanEatRightUp(x, y)||kingCanEatRightDown(x,y)||kingCanEatLeftUp(x,y)||kingCanEatLeftDown(x,y);
	}

	private boolean canEat(checker myColor){
		for(int x=0;x<settings.getFieldSize();x++)
			for(int y=0;y<settings.getFieldSize();y++){
				if((getFieldType(x, y)==myColor)&&(canEat(x,y)))
					return true;
			}
		return false;
	}

	private void move(int from_x, int from_y, int to_x, int to_y){
		currentPositions[to_y][to_x].make(currentPositions[from_y][from_x]);
		clearField(from_x,from_y);
	}

	private void clearField(int x, int y){
		currentPositions[y][x].clear();
	}
	
	private void eat(int from_x, int from_y, int to_x, int to_y){
		int on_x=normal(to_x-from_x),on_y=normal(to_y-from_y);
		int x=from_x, y=from_y;
		for(int counter=1; counter<abs(to_x-from_x);counter++){
			x+=on_x; y+=on_y;
			if(getFieldType(x,y)==checker.black)
				blackQuantity--;
			else if(getFieldType(x,y)==checker.white)
				whiteQuantity--;
			clearField(x,y);
		}
		move(from_x,from_y,to_x,to_y);
	}

	private int abs(int number){
		return Math.abs(number);
	}
	
	private int normal(int number){
		if(number==0)
			return 0;
		else
			return number/abs(number);
	}
	
	private boolean inBorder(int number){
		return (number>=0)&&(number<=settings.getFieldSize()-1);
	}
	
	private boolean standartCheck(int from_x, int from_y, int to_x, int to_y){
		if(isOdd(abs(to_x-to_y))||isOdd(abs(from_x-from_y)))
			return false;
		if(!inBorder(to_x)||!inBorder(to_y)||!inBorder(from_x)||!inBorder(from_y))
			return false;
		if(getFieldType(to_x, to_y)!=checker.nothing){
			return false;
		}
		return true;
	}

	private boolean kingEating(int from_x, int from_y, int to_x, int to_y){
		checker myColor=getFieldType(from_x, from_y),anotherColor=getAnotherColor(myColor);
		int on_x=normal(to_x - from_x);
		int on_y=normal(to_y - from_y);
		int x=from_x, y=from_y;
		for(int counter=1;counter<abs(to_x-from_x);counter++){
			x+=on_x; y+=on_y;
			if(getFieldType(x,y)==myColor)
				return false;
			if(getFieldType(x,y)==anotherColor){
				return(fieldIsEmpty(x+on_x, y+on_y));
			}
		}
		return false;
	}

	private boolean pawnEating(int from_x, int from_y, int to_x, int to_y){
		if((abs(from_x-to_x)!=2)||(abs(from_y-to_y)!=2))
			return false;
		checker myColor=getFieldType(from_x, from_y),anotherColor=getAnotherColor(myColor);
		int on_x=normal(to_x-from_x), on_y=normal(to_y-from_y);
		return (getFieldType(from_x+on_x,from_y+on_y)==anotherColor)&&fieldIsEmpty(to_x,to_y);
	}
	
	private boolean eating(int from_x, int from_y, int to_x, int to_y){
		if((abs(from_x-to_x)<2)||(abs(from_y-to_y)<2))
			return false;
		if(fieldIsKing(from_x, from_y))
			return kingEating(from_x, from_y, to_x, to_y);
		else
			return pawnEating(from_x, from_y, to_x, to_y);
	}
	
	private boolean canMoveRightUp(int x, int y){
		if((y<settings.getFieldSize()-1)&&(x<settings.getFieldSize()-1)&&fieldIsEmpty(x+1, y+1))
			return true;
		else
			return false;
	}
	
	private boolean canMoveRightDown(int x, int y){
		if((y>0)&&(x<settings.getFieldSize()-1)&&fieldIsEmpty(x+1, y-1))
			return true;
		else
			return false;
	}
	
	private boolean canMoveLeftUp(int x, int y){
		if((y<settings.getFieldSize()-1)&&(x>0)&&fieldIsEmpty(x-1, y+1))
			return true;
		else
			return false;
	}
	
	private boolean canMoveLeftDown(int x, int y){
		if((y>0)&&(x>0)&&fieldIsEmpty(x-1, y-1))
			return true;
		else
			return false;
	}
	
	private boolean canMove(int x, int y){
		checker myColor=getFieldType(x,y);
		if(myColor==checker.white)
			return canMoveRightUp(x,y)||canMoveLeftUp(x,y);
		else 
			return canMoveRightDown(x,y)||canMoveLeftDown(x,y);
	}

	private boolean canMove(checker myColor){
		for(int x=0;x<settings.getFieldSize();x++)
			for(int y=0;y<settings.getFieldSize();y++){
				if((getFieldType(x, y)!=myColor)||isOdd(x+y))
					continue;
				if(canMove(x, y)||canEat(x, y)){
					return true;
				}
			}
		return false;
	}

	private boolean fieldIsEmpty(int x, int y) {
		return currentPositions[y][x].isEmpty();
	}

	public int getAnotherId(int id){
		return (whiteId+blackId-id);
	}
	
	public int getWinnerId(){
		return getWinnerId(TimeHelper.getCurrentTime());
	}

	private int getWinnerId(long currentTime){
		if(blackLose()||whiteWin(currentTime))
			return whiteId;
		else if (whiteLose()||blackWin(currentTime))
			return blackId;
		else
			return 0;
	}

	private boolean blackLose(){
		return ((blackQuantity==0)||(!canMove(checker.black)));
	}

	private boolean blackWin(long currentTime) {
		return (lastStroke==blackId)&&(currentTime-lastStrokeTime>settings.getStrokeTime());
	}

	private boolean whiteLose(){
		return ((whiteQuantity==0)||(!canMove(checker.white)));
	}
	
	private boolean whiteWin(long currentTime) {
		return (lastStroke==whiteId)&&(currentTime-lastStrokeTime>settings.getStrokeTime());
	}

	public Snapshot getSnapshot(int id){
		if (id==whiteId)
			return returnSnapshot('w');
		else
			return returnSnapshot('b');
	}

	private Snapshot returnSnapshot(char color) {
		return new Snapshot(currentPositions,color,settings.getFieldSize(),getNext());
	}

	public void saveAILog(String winner){
		String fileName="/log/AI/"+String.valueOf(id)+".txt";
		String data=winner+"\n"+log.toString();
		VFS.writeToFile(fileName, data);
	}
	
	public void saveLog(int winnerId){
		if(winnerId==blackId)
			saveAILog("black");
		else
			saveAILog("white");
		String fileName="/log/"+dirForLog+"/"+String.valueOf(id)+".txt";
		String data=log.toString()+"\n"+getSnapshot(whiteId).toStringTest();
		VFS.writeToFile(fileName, data);
		System.out.println("\nSave log for "+String.valueOf(id));
	}

	public char getNext(){
		if(lastStroke==whiteId)
			return 'b';
		else
			return 'w';
	}

	public int[] getFields(){
		int[] fields = new int[blackQuantity+whiteQuantity];
		int number=0;
		for(int y=0;y<settings.getFieldSize();y++){
			for(int x=0;x<settings.getFieldSize();x++){
				if(getFieldType(x, y)==checker.white){
					fields[number]=y*settings.getFieldSize()+x;
					if(fieldIsKing(x, y)){
						fields[number]*=-1;
					}
					number++;
				}
			}
		}
		for(int y=0;y<settings.getFieldSize();y++){
			for(int x=0;x<settings.getFieldSize();x++){
				if(getFieldType(x, y)==checker.black){
					fields[number]=y*settings.getFieldSize()+x;
					if(fieldIsKing(x, y)){
						fields[number]*=-1;
					}
					number++;
				}
			}
		}
		return fields;
	}

    public void clearWhiteQuantity() { whiteQuantity = 0; }
	public int getWhiteQuantity(){
		return whiteQuantity;
	}
	
	public int getBlackQuantity(){
		return blackQuantity;
	}
}
//Черная клетка, если координаты один. четности