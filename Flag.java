package imetl;

import java.util.List;
import java.util.ArrayList;

public class Flag{
	private String[] aliases;
	private int argCount;
	
	protected List<String> values;
	protected boolean present;
	
	public Flag(String fl){this(fl, 1);}
	public Flag(String fl, int args){this(new String[]{fl}, args);}
	
	public Flag(String sh, String ln){this(sh, ln, 1);}
	public Flag(String sh, String ln, int args){this(new String[]{sh, ln}, args);}
	
	public Flag(String[] aliases){this(aliases, 1);}
	
	public Flag(String[] aliases, int args){
		this.aliases = aliases;
		this.argCount = args;
		
		this.values = new ArrayList<String>();
		this.present = false;
	}
	
	public boolean hasValue(){return present && (values.size() >= argCount || argCount < 0);}
	
	public boolean isPresent(){return present;}
	
	public void setPresent(){this.present = true;}
	
	public int getArgCount(){return argCount;}
	
	public List<String> getValues(){return present ? values : null;}
	
	public String primaryAlias(){return aliases[0];}
	
	
	public boolean isAlias(String s){
		for(String al : aliases){
			if(s.equals(al)){
				return true;
			}
		}
		
		return false;
	}
	
	public void clear(){
		present = false;
		values.clear();
	}
	
	public void add(String s){values.add(s);}
	
	public boolean isFull(){
		if(argCount < 0) return false;
		return values.size() >= argCount;
	}
}
