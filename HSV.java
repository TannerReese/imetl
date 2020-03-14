package imetl;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HSV extends Command{
	private ImageFlag input, output;
	private ImageFlag hue, saturation, value;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl hsv [-h HUE_FILE] [-s SATURATION_FILE] [-v VALUE_FILE] [-i] INPUT_IMAGE\n"
	+ "       imetl hsv [-h HUE_FILE] [-s SATURATION_FILE] [-v VALUE_FILE] -o OUTPUT_LOCATION\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image file to be separated into its components\n"
	+ "   -o, --output		Location to save the image created from the combined components\n"
	+ "\n"
	+ "   -h, --hue		Location to read from or write to the hue component\n"
	+ "   -s, --saturation	Location to read from or write to the saturation component\n"
	+ "   -v, --value		Location to read from or write to the value component\n"
	+ "\n"
	+ "The hue and value components are read from and written to\n"
	+ "the hue and value channels of the respective files. The saturation\n"
	+ "component is read and written as the value channel of the\n"
	+ "saturation file.\n"
	+ "These files will be written to if an input is given and read from\n"
	+ "if an output is given.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public HSV(){
		super("hsv");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", 1);
		output = new ImageFlag("-o", "--output", 1);
		
		hue = new ImageFlag("-h", "--hue", 1);
		saturation = new ImageFlag("-s", "--saturation", 1);
		value = new ImageFlag(new String[]{"-v", "--value", "-b", "--brightness"}, 1);
		
		parser.addAll(new Flag[]{input, output, hue, saturation, value});
		parser.setFreeFlag(0);
	}
	
	protected boolean execute(){
		if(input.hasValue()){
			BufferedImage[] imgs = input.readImages();
			if(imgs.length > 1){
				System.out.println("Multiple Input Images provided\nOnly one will be processed");
			}
			
			BufferedImage[] hsv = separate(imgs[0]);
			hue.writeImages(hsv[0], null);
			saturation.writeImages(hsv[1], null);
			value.writeImages(hsv[2], null);
			
			return true;
		}
		
		if(output.hasValue()){
			BufferedImage hueImg = null, saturationImg = null, valueImg = null;
			hueImg = hue.isPresent() ? hue.readImages()[0] : null;
			saturationImg = saturation.isPresent() ? saturation.readImages()[0] : null;
			valueImg = value.isPresent() ? value.readImages()[0] : null;
			
			try{
				output.writeImages(combine(hueImg, saturationImg, valueImg), null);
			}catch(IllegalArgumentException ex){
				System.out.println(ex.getMessage());
				return false;
			}
			return true;
		}
		
		System.out.println("No Input or Output provided");
		return false;
	}
	
	
	public static BufferedImage[] separate(BufferedImage img){
		int w = img.getWidth(), h = img.getHeight();
		BufferedImage hue, saturation, value;
		hue = new BufferedImage(w, h, img.getType());
		saturation = new BufferedImage(w, h, img.getType());
		value = new BufferedImage(w, h, img.getType());
		
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
				float[] hsb = Color.RGBtoHSB(r, g, b, null);
				
				hue.setRGB(i, j, Color.getHSBColor(hsb[0], 1, 1).getRGB());
				saturation.setRGB(i, j, new Color(hsb[1], hsb[1], hsb[1]).getRGB());
				value.setRGB(i, j, new Color(hsb[2], hsb[2], hsb[2]).getRGB());
			}
		}
		
		return new BufferedImage[]{hue, saturation, value};
	}
	
	public BufferedImage combine(BufferedImage hue, BufferedImage saturation, BufferedImage value)
		throws IllegalArgumentException{
		int wd = 0, ht = 0;
		boolean isset = false;
		if(hue != null){
			wd = hue.getWidth();
			ht = hue.getHeight();
			isset = true;
		}
		
		if(saturation != null){
			if(!isset){
				wd = saturation.getWidth();
				ht = saturation.getHeight();
				isset = true;
			}else if(wd != saturation.getWidth() || ht != saturation.getHeight()){
				throw new IllegalArgumentException("Image Dimensions Do Not Match");
			}
		}
		
		if(value != null){
			if(!isset){
				wd = value.getWidth();
				ht = value.getHeight();
				isset = true;
			}else if(wd != value.getWidth() || ht != value.getHeight()){
				throw new IllegalArgumentException("Image Dimensions Do Not Match");
			}
		}
		
		if(!isset){
			throw new IllegalArgumentException("No Components provided");
		}
		
		BufferedImage newimg = new BufferedImage(wd, ht, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < wd; i++){
			for(int j = 0; j < ht; j++){
				float h, s, v;
				h = hue == null ? 0 : getHSV(hue.getRGB(i, j))[0];
				s = saturation == null ? 0 : getHSV(saturation.getRGB(i, j))[2];
				v = value == null ? 0 : getHSV(value.getRGB(i, j))[2];
				
				newimg.setRGB(i, j, Color.getHSBColor(h, s, v).getRGB());
			}
		}
		return newimg;
	}
	
	
	private static float[] getHSV(int rgb){
		Color c = new Color(rgb);
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		return Color.RGBtoHSB(r, g, b, null);
	}
}
