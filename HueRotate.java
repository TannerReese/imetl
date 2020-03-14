package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HueRotate extends Command{
	private ImageFlag input, output;
	private DoubleFlag degrees, radians;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl huerotate -d DEGREES -o OUTPUT_LOCATION [-i] INPUT_FILES...\n"
	+ "       imetl huerotate -r RADIANS -o OUTPUT_LOCATION [-i] INPUT_FILES...\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image Files to be rotated\n"
	+ "   -o, --output		Location to save rotated images\n"
	+ "   -d, --degrees,	Angle to rotate by in degrees\n"
	+ "   -a, --angle\n"
	+ "   -r, --radians	Angle to rotate by in radians\n"
	+ "\n"
	+ "Hue Rotations are performed by shifting the hue of each pixel by the given angle\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public HueRotate(){
		super("huerotate");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		degrees = new DoubleFlag(new String[]{"-d", "--degrees", "-a", "--angle"}, 1, 120);
		radians = new DoubleFlag("-r", "--radians", 1, Math.PI * 2 / 3);
		
		parser.addAll(new Flag[]{input, output, degrees, radians});
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
		
		double angle = Math.PI * 2 / 3;
		if(radians.hasValue()){
			angle = radians.getDoubles()[0];
		}
		if(degrees.hasValue()){
			angle = degrees.getDoubles()[0] * Math.PI / 180;
		}
		
		BufferedImage[] outImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			outImgs[i] = rotate(imgs[i], angle);
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		output.writeImages(outImgs, names);
		
		return true;
	}
	
	
	private static BufferedImage rotate(BufferedImage img, double ang){
		int w = img.getWidth(), h = img.getHeight();
		BufferedImage newimg = new BufferedImage(w, h, img.getType());
		
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
				
				hsb[0] += ang / (Math.PI * 2);
				newimg.setRGB(i, j, Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB());
			}
		}
		return newimg;
	}
}
