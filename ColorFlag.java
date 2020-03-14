package imetl;

import java.lang.reflect.Field;

import java.awt.Color;

public class ColorFlag extends Flag{
	private Color defaultColor;
	
	public ColorFlag(String fl, Color def){this(fl, 1, def);}
	public ColorFlag(String fl, int args, Color def){this(new String[]{fl}, args, def);}
	
	public ColorFlag(String sh, String ln, Color def){this(sh, ln, 1, def);}
	public ColorFlag(String sh, String ln, int args, Color def){this(new String[]{sh, ln}, args, def);}
	
	public ColorFlag(String[] aliases, Color def){this(aliases, 1, def);}
	public ColorFlag(String[] aliases, int args, Color def){
		super(aliases, args);
		
		this.defaultColor = def;
	}
	
	// Obtain Color values from Strings
	public Color[] getColors(){
		Color[] cols = new Color[values.size()];
		
		for(int i = 0; i < values.size(); i++){
			String vl = values.get(i);
			if(vl == null){
				cols[i] = defaultColor;
				continue;
			}
			
			try{
				Field field = Color.class.getField(vl.toUpperCase());
				cols[i] = (Color)field.get(null);
			}catch(Exception e){
				cols[i] = defaultColor;
			}
		}
		return cols;
	}
}
