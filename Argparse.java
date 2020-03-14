package imetl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class Argparse{
	private List<Flag> flags;
	private int freeFlag;
	
	public Argparse(){
		this(new ArrayList<Flag>(), 0);
	}
	
	public Argparse(Flag[] flags){this(flags, 0);}

	public Argparse(Flag[] flags, int freeFlag){
		this(Arrays.asList(flags), freeFlag);
	}
	
	public Argparse(List<Flag> flags){this(flags, 0);}
	
	public Argparse(List<Flag> flags, int freeFlag){
		this.flags = flags;
		this.freeFlag = freeFlag;
	}
	
	public void clear(){
		for(Flag fl : flags){
			fl.clear();
		}
	}
	
	public void add(Flag fl){flags.add(fl);}
	
	public void addAll(Flag[] fls){flags.addAll(Arrays.asList(fls));}
	
	public void setFreeFlag(int freeFlag){
		this.freeFlag = freeFlag;
	}
	
	
	
	public Map<String, List<String>> getParameters(){
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		
		for(Flag fl : flags){
			if(fl.isPresent()){
				params.put(fl.primaryAlias(), fl.getValues());
			}
		}
		return params;
	}
	
	
	// Primary method for parsing string
	public void parse(String[] args){
		clear();
		Flag free = flags.get(freeFlag);
		
		String s;
		for(int i = 0; i < args.length; i++){
			s = args[i];
			
			Flag fl = whoseAlias(s);
			if(fl != null){
				fl.setPresent();
				
				for(i++ ; i < args.length; i++){
					s = args[i];
					if(isAlias(s) || fl.isFull()){
						i--;
						break;
					}else{
						fl.add(s);
					}
				}
			}else{
				free.setPresent();
				free.add(s);
			}
		}
	}
	
	// Identify to which Flag a given alias string (e.g. "-i") belongs
	public Flag whoseAlias(String s){
		for(Flag fl : flags){
			if(fl.isAlias(s)){
				return fl;
			}
		}
		
		return null;
	}
	
	public boolean isAlias(String s){return whoseAlias(s) != null;}	
}
