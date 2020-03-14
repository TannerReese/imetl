package imetl;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Mask extends Command{
	private ImageFlag input, output;
	private ImageFlag mask, replace;
	private DoubleFlag weight;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl mask -m MASK_FILE -r REPLACE_FILE -o OUTPUT_LOCATION [-i] INPUT_FILE\n"
	+ "       imetl mask -w PROPORTION -r REPLACE_FILE -o OUTPUT_LOCATION [-i] INPUT_FILE\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image file to be masked\n"
	+ "   -o, --output		Location to save the masked image to\n"
	+ "   -m, --mask		Image to use to determine how much of\n"
	+ "			the replacement image is used. White\n"
	+ "			represents only the replacement, while\n"
	+ "			Black represents only the input file.\n"
	+ "   -r, --replace	Replacement image used to substitute\n"
	+ "			parts of the original\n"
	+ "   -w, --weight		Flat proportion used instead of the\n"
	+ "			mask. The same weight will be used in\n"
	+ "			the average everywhere. Zero represents\n"
	+ "			only the input file, while One represents\n"
	+ "			only the replacement file.\n"
	+ "\n"
	+ "The mask image is used to determine the proportion of the\n"
	+ "replacement present when averaging. A white pixel in the mask\n"
	+ "leads to the corresponding pixel from the replacement being\n"
	+ "put in the output, while a black pixel in the mask leads to\n"
	+ "the corresponding pixel from the input image being put in the\n"
	+ "output image.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public Mask(){
		super("mask");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", 1);
		output = new ImageFlag("-o", "--output", 1);
		
		mask = new ImageFlag("-m", "--mask", 1);
		replace = new ImageFlag("-r", "--replace", 1);
		weight = new DoubleFlag("-w", "--weight", 1, 0.5);
		
		parser.addAll(new Flag[]{input, output, mask, replace, weight});
		parser.setFreeFlag(0);
	}
	
	protected boolean execute(){
		// Ensure that there is an input image, replacer, and output to write to
		if(!input.hasValue()){
			System.out.println("No Input provided");
			return false;
		}
		if(!output.hasValue()){
			System.out.println("No Output provided");
			return false;
		}
		if(!replace.hasValue()){
			System.out.println("No Replacement Image provided");
			return false;
		}
		BufferedImage img = input.readImages()[0], repl = replace.readImages()[0];
		String vl = input.getValues().get(0);
		
		BufferedImage outImg;
		if(mask.hasValue()){
			outImg = replace(mask.readImages()[0], repl, img);
		}else if(weight.hasValue()){
			outImg = replace(weight.getDoubles()[0], repl, img);
		}else{
			System.out.println("No Weight or Mask provided");
			return false;
		}
		
		if(outImg == null){
			return false;
		}
		
		String name = vl == null ? null : new File(vl).getName();
		output.writeImages(outImg, name);
		
		return true;
	}
	
	
	// Average the pixel colors of two images together with a weight determined by the mask image
	private static BufferedImage replace(BufferedImage mask, BufferedImage white, BufferedImage black){
		int w = mask.getWidth(), h = mask.getHeight();
		if(
			w != white.getWidth() || h != white.getHeight() ||
			w != black.getWidth() || h != black.getHeight()
		){
			System.out.println("Image Dimensions Do Not Match");
			return null;
		}
		
		BufferedImage newimg = new BufferedImage(w, h, white.getType());
		
		Color wh, bk, mk;
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				wh = new Color(white.getRGB(i, j));
				bk = new Color(black.getRGB(i, j));
				
				mk = new Color(mask.getRGB(i, j));
				double p =(double) (mk.getRed() + mk.getGreen() + mk.getBlue()) / (3 * 255);
				newimg.setRGB(i, j, average(p, wh, bk).getRGB());
			}
		}
		
		return newimg;
	}
	
	// Perform averaging with uniform weight
	private static BufferedImage replace(double p, BufferedImage white, BufferedImage black){
		int w = white.getWidth(), h = white.getHeight();
		if(w != black.getWidth() || h != black.getHeight()){
			System.out.println("Image Dimensions Do Not Match");
			return null;
		}
		
		BufferedImage newimg = new BufferedImage(w, h, white.getType());
		
		Color wh, bk;
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				wh = new Color(white.getRGB(i, j));
				bk = new Color(black.getRGB(i, j));
				
				newimg.setRGB(i, j, average(p, wh, bk).getRGB());
			}
		}
		
		return newimg;
	}
	
	// Average two colors using the weights p and 1 - p
	private static Color average(double p, Color c1, Color c2){
		double r, g, b;
		r = p * c1.getRed() + (1 - p) * c2.getRed();
		g = p * c1.getGreen() + (1 - p) * c2.getGreen();
		b = p * c1.getBlue() + (1 - p) * c2.getBlue();
		
		return new Color((int)r, (int)g, (int)b);
	}
}
