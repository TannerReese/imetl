package imetl;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class RGB extends Command{
	private ImageFlag input, output;
	private ImageFlag red, green, blue;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl rgb [-r RED_FILE] [-g GREEN_FILE] [-b BLUE_FILE] [-i] INPUT_IMAGE\n"
	+ "       imetl rgb [-r RED_FILE] [-g GREEN_FILE] [-b BLUE_FILE] -o OUTPUT_LOCATION\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input	Image file to be separated into its components\n"
	+ "   -o, --output	Location to save the image created from the combined components\n"
	+ "\n"
	+ "   -r, --red	Location to read from or write to the red component\n"
	+ "   -g, --green	Location to read from or write to the green component\n"
	+ "   -b, --blue	Location to read from or write to the blue component\n"
	+ "\n"
	+ "The red, green, and blue components are read from and written to\n"
	+ "the red, green, and blue channels of the respective files.\n"
	+ "These files will be written to if an input is given and read from\n"
	+ "if an output is given.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public RGB(){
		super("rgb");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", 1);
		output = new ImageFlag("-o", "--output", 1);
		
		red = new ImageFlag("-r", "--red", 1);
		green = new ImageFlag("-g", "--green", 1);
		blue = new ImageFlag("-b", "--blue", 1);
		
		parser.addAll(new Flag[]{input, output, red, green, blue});
		parser.setFreeFlag(0);
	}
	
	protected boolean execute(){
		if(input.hasValue()){
			BufferedImage[] imgs = input.readImages();
			if(imgs.length > 1){
				System.out.println("Multiple Input Images provided\nOnly one will be processed");
			}
			
			BufferedImage[] rgb = separate(imgs[0]);
			red.writeImages(rgb[0], null);
			green.writeImages(rgb[1], null);
			blue.writeImages(rgb[2], null);
			
			return true;
		}
		
		if(output.hasValue()){
			BufferedImage redImg = null, greenImg = null, blueImg = null;
			redImg = red.hasValue() ? red.readImages()[0] : null;
			greenImg = green.hasValue() ? green.readImages()[0] : null;
			blueImg = blue.hasValue() ? blue.readImages()[0] : null;
			
			try{
				output.writeImages(combine(redImg, greenImg, blueImg), null);
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
		BufferedImage red, green, blue;
		red = new BufferedImage(w, h, img.getType());
		green = new BufferedImage(w, h, img.getType());
		blue = new BufferedImage(w, h, img.getType());
		
		int r, g, b;
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				r = c.getRed();
				g = c.getGreen();
				b = c.getBlue();
				
				red.setRGB(i, j, new Color(r, 0, 0).getRGB());
				green.setRGB(i, j, new Color(0, g, 0).getRGB());
				blue.setRGB(i, j, new Color(0, 0, b).getRGB());
			}
		}
		return new BufferedImage[]{red, green, blue};
	}
	
	public static BufferedImage combine(BufferedImage red, BufferedImage green, BufferedImage blue)
		throws IllegalArgumentException{
		int w = 0, h = 0;
		boolean isset = false;
		if(red != null){
			w = red.getWidth();
			h = red.getHeight();
			isset = true;
		}
		
		if(green != null){
			if(!isset){
				w = green.getWidth();
				h = green.getHeight();
				isset = true;
			}else if(w != green.getWidth() || h != green.getHeight()){
				throw new IllegalArgumentException("Image Dimensions Do Not Match");
			}
		}
		
		if(blue != null){
			if(!isset){
				w = blue.getWidth();
				h = blue.getHeight();
				isset = true;
			}else if(w != blue.getWidth() || h != blue.getHeight()){
				throw new IllegalArgumentException("Image Dimensions Do Not Match");
			}
		}
		
		if(!isset){
			throw new IllegalArgumentException("No Components provided");
		}
		
		BufferedImage newimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				int r, g, b;
				r = red == null ? 0 : new Color(red.getRGB(i, j)).getRed();
				g = green == null ? 0 : new Color(green.getRGB(i, j)).getGreen();
				b = blue == null ? 0 : new Color(blue.getRGB(i, j)).getBlue();
				
				newimg.setRGB(i, j, new Color(r, g, b).getRGB());
			}
		}
		
		return newimg;
	}
}
