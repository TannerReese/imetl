package imetl;

public class DoubleFlag extends Flag{
	private double defaultDouble;
	
	public DoubleFlag(String fl, double def){this(fl, 1, def);}
	public DoubleFlag(String fl, int args, double def){this(new String[]{fl}, args, def);}
	
	public DoubleFlag(String sh, String ln, double def){this(sh, ln, 1, def);}
	public DoubleFlag(String sh, String ln, int args, double def){this(new String[]{sh, ln}, args, def);}
	
	public DoubleFlag(String[] aliases, double def){this(aliases, 1, def);}
	public DoubleFlag(String[] aliases, int args, double def){
		super(aliases, args);
		
		this.defaultDouble = def;
	}
	
	
	public double[] getDoubles(){
		double[] ds = new double[values.size()];
		for(int i = 0; i < values.size(); i++){
			try{
				ds[i] = Double.parseDouble(values.get(i));
			}catch(NumberFormatException ex){
				ds[i] = defaultDouble;
			}
		}
		
		return ds;
	}
}
