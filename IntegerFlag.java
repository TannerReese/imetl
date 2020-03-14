package imetl;

public class IntegerFlag extends Flag{
	private int defaultInteger;
	
	public IntegerFlag(String fl, int def){this(fl, 1, def);}
	public IntegerFlag(String fl, int args, int def){this(new String[]{fl}, args, def);}
	
	public IntegerFlag(String sh, String ln, int def){this(sh, ln, 1, def);}
	public IntegerFlag(String sh, String ln, int args, int def){this(new String[]{sh, ln}, args, def);}
	
	public IntegerFlag(String[] aliases, int def){this(aliases, 1, def);}
	public IntegerFlag(String[] aliases, int args, int def){
		super(aliases, args);
		
		this.defaultInteger = def;
	}
	
	
	public int[] getIntegers(){
		int[] ds = new int[values.size()];
		for(int i = 0; i < values.size(); i++){
			try{
				ds[i] = Integer.parseInt(values.get(i));
			}catch(NumberFormatException ex){
				ds[i] = defaultInteger;
			}
		}
		
		return ds;
	}
}
