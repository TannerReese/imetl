package imetl;

public class BooleanFlag extends Flag{
	public BooleanFlag(String fl){this(new String[]{fl});}
	
	public BooleanFlag(String sh, String ln){this(new String[]{sh, ln});}
	
	public BooleanFlag(String[] aliases){
		super(aliases, 1);
	}
	
	
	public boolean getBoolean(){
		return present;
	}
}
