package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Gamma extends Command{
	private ImageFlag input, output;
	private DoubleFlag gamma;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl gamma -y GAMMA_VALUE -o OUTPUT_LOCATION [-i] INPUT_IMAGES...\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image files to be gamma corrected\n"
	+ "   -o, --output		Location to save gamma corrected images\n"
	+ "   -y, -g, --gamma	Power to apply to value component of the images\n"
	+ "\n"
	+ "The input images are transformed by taking their HSV value channel\n"
	+ "to the power of GAMMA_VALUE to obtain the new value channel.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";

	
	public Gamma(){
		super("gamma");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		gamma = new DoubleFlag(new String[]{"-y", "-g", "--gamma"}, 1, 1);
		
		parser.addAll(new Flag[]{input, output, gamma});
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
		
		double gammaValue = 1;
		if(gamma.hasValue()){
			gammaValue = gamma.getDoubles()[0];
		}
		
		BufferedImage[] outImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			outImgs[i] = gammaCorrect(imgs[i], gammaValue);
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		output.writeImages(outImgs, names);
		
		return true;
	}
	
	
	private static BufferedImage gammaCorrect(BufferedImage img, double gammaValue){
		int w = img.getWidth(), h = img.getHeight();
		BufferedImage newimg = new BufferedImage(w, h, img.getType());
		
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color c = new Color(img.getRGB(i, j));
				int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
				float[] hsb = Color.RGBtoHSB(r, g, b, null);
				
				hsb[2] = (float)Math.pow(hsb[2], gammaValue);
				
				newimg.setRGB(i, j, Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB());
			}
		}
		return newimg;
	}
}
