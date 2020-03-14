package imetl;

import java.util.List;

import java.io.File;

import java.awt.image.BufferedImage;

public class Crop extends Command{
	private ImageFlag input, output;
	private DoubleFlag xpos, ypos, width, height;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl crop [-x XPOS] [-y YPOS] [-w WIDTH] [-h HEIGHT] -o OUTPUT_LOCATION [-i] INPUT_IMAGE\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image to be cropped\n"
	+ "   -o, --output		Location to put cropped image\n"
	+ "\n"
	+ "   -x			Distance of left edge of crop from left edge of the original image\n"
	+ "   -y			Distance of top edge of crop from the top edge of the original image\n"
	+ "   -w, --width		Width of the crop rectangle\n"
	+ "   -h, --height		Height of the crop rectangle\n"
	+ "\n"
	+ "The default (x, y) is (0, 0) which is the upper\n"
	+ "left-hand corner. The default width and height are\n"
	+ "the width and height of the original image\n"
	+ "\n"
	+ "The x, y, width, and height may be given as either\n"
	+ "a proportion of the width or height or a number of pixels\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public Crop(){
		super("crop");
		helpManual = HELP_MANUAL;
		
		// Set parameters for flags
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		width = new DoubleFlag("-w", "--width", 1, -1);
		height = new DoubleFlag("-h", "--height", 1, -1);
		
		xpos = new DoubleFlag("-x", 1, 0);
		ypos = new DoubleFlag("-y", 1, 0);
		
		parser.addAll(new Flag[]{input, output, width, height, xpos, ypos});
		parser.setFreeFlag(0);
	}
	
	protected boolean execute(){
		// Ensure that the command was given input and output
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
		
		// Obtain width, height, x-position, and y-position from Flags
		double w, h, x, y;
		w = width.hasValue() ? width.getDoubles()[0] : -1;
		h = height.hasValue() ? height.getDoubles()[0] : -1;
		
		x = xpos.hasValue() ? xpos.getDoubles()[0] : 0;
		y = ypos.hasValue() ? ypos.getDoubles()[0] : 0;
		
		// Apply given cropping parameters to given images
		BufferedImage[] outImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			outImgs[i] = subimage(imgs[i], w, h, x, y);
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		output.writeImages(outImgs, names);
		
		return true;
	}
	
	
	// Static method for cropping images according to either proportional or pixel based measurements
	private static BufferedImage subimage(BufferedImage img, double w, double h, double x, double y){
		if(img == null){
			return null;
		}
		
		// Convert w, h, x, and y from proportional measurements to pixel based
		int wd = img.getWidth(), ht = img.getHeight();
		w = w < 0 ? wd : (w <= 1 ? w * wd : w);
		h = h < 0 ? ht : (h <= 1 ? h * ht : h);
		x = x <= 1 ? x * wd : x;
		y = y <= 1 ? y * ht : y;
		
		// Prevent w and h from escaping img boundaries
		if(w + x > wd){
			w = wd - x;
		}
		
		if(h + y > ht){
			h = ht - y;
		}
		
		return img.getSubimage((int)x, (int)y, (int)w, (int)h);
	}
}
