package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Grey extends Command{
	private ImageFlag input;
	private ImageFlag luma, lightness, intensity, value;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl grey [-y LUMA_OUTPUT] [-l LIGHTNESS_OUTPUT] [-I INTENSITY_OUTPUT] [-v VALUE_OUTPUT] [-i] INPUT_IMAGES...\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image files to be converted to greyscale\n"
	+ "\n"
	+ "   -y, --luma		Location to save luma converted image\n"
	+ "   -l, --light,		Location to save lightness converted image\n"
	+ "   --lightness\n"
	+ "   -I, --intensity	Location to save intensity converted image\n"
	+ "   -v, --value		Location to save value converted image\n"
	+ "\n"
	+ "The greyscale types are defined as follows:\n"
	+ "   luma		Weighted average of the RGB components\n"
	+ "			according to the Rec. 709 standard\n"
	+ "   lightness	Average of the maximum and minimum\n"
	+ "			RGB components\n"
	+ "   intensity	Average of the RGB components\n"
	+ "   value	Maximum RGB component\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	
	public Grey(){
		super("grey");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		luma = new ImageFlag("-y", "--luma", 1);
		lightness = new ImageFlag(new String[]{"-l", "--light", "--lightness"}, 1);
		intensity = new ImageFlag("-I", "--intensity", 1);
		value = new ImageFlag("-v", "--value", 1);
		
		parser.addAll(new Flag[]{input, luma, lightness, intensity, value});
	}
	
	protected boolean execute(){
		if(!input.hasValue()){
			System.out.println("No Input provided");
			return false;
		}
		
		BufferedImage[] imgs = input.readImages();
		List<String> vls = input.getValues();
		
		boolean success = true;
		Metric[] metrics = new Metric[]{Metric.LUMA, Metric.LIGHTNESS, Metric.INTENSITY, Metric.VALUE};
		for(Metric met : metrics){
			ImageFlag flg = getFlagByMetric(met);
			
			if(flg.isPresent()){
				BufferedImage[] outImgs = new BufferedImage[imgs.length];
				String[] names = new String[imgs.length];
				
				for(int i = 0; i < imgs.length; i++){
					outImgs[i] = greyify(imgs[i], met);
					
					if(vls.get(i) != null){
						names[i] = new File(vls.get(i)).getName();
					}
				}
				
				if(!flg.writeImages(outImgs, names)){
					success = false;
				}
			}
		}
		
		return success;
	}
	
	private ImageFlag getFlagByMetric(Metric met){
		switch(met){
			case LUMA: return luma;
			case LIGHTNESS: return lightness;
			case INTENSITY: return intensity;
			case VALUE: return value;
		}
		return null;
	}
	
	
	public static BufferedImage greyify(BufferedImage img, Metric met){
		if(img == null){
			return null;
		}
		
		int w = img.getWidth(), h = img.getHeight();
		BufferedImage newimg = new BufferedImage(w, h, img.getType());
		
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				Color nc = calculateMetric(c, met);
				newimg.setRGB(i, j, nc.getRGB());
			}
		}
		return newimg;
	}
	
	public static Color calculateMetric(Color c, Metric met){
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		int max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
		
		int grey = 0;
		switch(met){
			case LUMA: grey = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
			break;
			case LIGHTNESS: grey = (max + min) / 2;
			break;
			case INTENSITY: grey = (r + g + b) / 3;
			break;
			case VALUE: grey = max;
		}
		
		return new Color(grey, grey, grey);
	}
	
	
	public static enum Metric{
		LUMA, LIGHTNESS, INTENSITY, VALUE
	}
}
