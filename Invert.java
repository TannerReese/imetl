package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Invert extends Command{
	private ImageFlag input, output;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl invert -o OUTPUT_FILE [-i] INPUT_FILE\n"
	+ "       imetl invert -o OUTPUT_DIRECTORY [-i] INPUT_FILES...\n"
	+ "\n"
	+ "Flags: \n"
	+ "   -i, --input		Image files to be inverted\n"
	+ "   -o, --output		Location to place inverted images\n"
	+ "\n"
	+ "Images are inverted by subtracting\n"
	+ "the red, green, and blue components\n"
	+ "from the maximum, 255.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public Invert(){
		super("invert");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		parser.addAll(new Flag[]{input, output});
		parser.setFreeFlag(0);
	}
	
	protected boolean execute(){
		if(!input.hasValue()){
			System.out.println("No Input provided");
			return false;
		}
		if(!output.hasValue()){
			System.out.println("No Output provided");
			return false;
		}
		BufferedImage[] imgs = input.readImages();
		List<String> vls = input.getValues();
		
		BufferedImage[] outImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			outImgs[i] = invert(imgs[i]);
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		output.writeImages(outImgs, names);
		
		return true;
	}
	
	
	private static BufferedImage invert(BufferedImage img){
		if(img == null){
			return null;
		}
		
		int w = img.getWidth(), h = img.getHeight();
		BufferedImage newimg = new BufferedImage(w, h, img.getType());
		
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				
				int r, g, b;
				r = 255 - c.getRed();
				g = 255 - c.getGreen();
				b = 255 - c.getBlue();
				
				newimg.setRGB(i, j, new Color(r, g, b).getRGB());
			}
		}
		return newimg;
	}
}
