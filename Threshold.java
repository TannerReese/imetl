package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Threshold extends Command{
	private ImageFlag input, output;
	private DoubleFlag threshold;
	private ColorFlag color;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl threshold -t THRESHOLD [-c CENTER_COLOR] -o OUTPUT_LOCATION [-i] INPUT_IMAGES...\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image files to be thresholded\n"
	+ "   -o, --output		Location to save thresholded images\n"
	+ "   -t, --threshold	Cutoff point above which values are\n"
	+ "			made white and below which they are\n"
	+ "			made black\n"
	+ "   -c, --color		Color to use as the center. Pixels\n"
	+ "			closer to the center will be more likely\n"
	+ "			to become white while further colors will\n"
	+ " 			likely become black. Defaults to WHITE.\n"
	+ "\n"
	+ "The thresholder converts any provided image to an image strictly\n"
	+ "comprised of black and white pixels. This is done according to the\n"
	+ "center color and the threshold. The threshold must be between zero\n"
	+ "and one. Thresholds closer to one will yield more black and closer\n"
	+ "to zero more white. Those pixels closer in color to the center will\n"
	+ "remain white at higher thresholds.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public Threshold(){
		super("threshold");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		threshold = new DoubleFlag("-t", "--threshold", 1, 0.5);
		color = new ColorFlag("-c", "--color", 1, Color.WHITE);
		
		parser.addAll(new Flag[]{input, output, threshold, color});
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
		
		Color center = Color.WHITE;
		double thresh = 0.5;
		if(color.hasValue()){
			center = color.getColors()[0];
		}
		if(threshold.hasValue()){
			thresh = threshold.getDoubles()[0];
		}
		
		BufferedImage[] outImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			outImgs[i] = applyThreshold(imgs[i], center, thresh);
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		output.writeImages(outImgs, names);
		
		return true;
	}
	
	
	// Convert every pixel to either white or black depending on if it is close enough to the center color
	private static BufferedImage applyThreshold(BufferedImage img, Color center, double thresh){
		int w = img.getWidth(), h = img.getHeight();
		BufferedImage newimg = new BufferedImage(w, h, img.getType());
		
		double maxDist = getMaxDist(center);
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				double close = getNormalizedCloseness(center, maxDist, c);
				
				int value = close >= thresh ? 255 : 0;
				newimg.setRGB(i, j, new Color(value, value, value).getRGB());
			}
		}
		return newimg;
	}
	
	// Find the distance from the center color to the color that is farthest away
	private static double getMaxDist(Color center){
		int r = center.getRed(), g = center.getGreen(), b = center.getBlue();
		
		double total = 0;
		total += r >= 128 ? r : 255 - r;
		total += g >= 128 ? g : 255 - g;
		total += b >= 128 ? b : 255 - b;
		return total;
	}
	
	// Give each color in RGB space a value from 0 to 1 representing how close it is to the center color
	private static double getNormalizedCloseness(Color center, double maxDist, Color c){
		int cr = center.getRed(), cg = center.getGreen(), cb = center.getBlue();
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		return 1 - (Math.abs(cr - r) + Math.abs(cg - g) + Math.abs(cb - b)) / maxDist;
	}
}
