package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Resize extends Command{
	private ImageFlag input, output;
	private DoubleFlag width, height;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl resize [-w NEW_WIDTH] [-h NEW_HEIGHT] -o OUTPUT_LOCATION [-i] INPUT_IMAGE\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image file to be resized\n"
	+ "   -o, --output		Location to save resized image\n"
	+ "\n"
	+ "   -w, --width		Width to resize the image to\n"
	+ "   -h, --height		Height to resize the image to\n"
	+ "\n"
	+ "If not specified, the width and height will not be changed\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public Resize(){
		super("resize");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		width = new DoubleFlag("-w", "--width", 1, -1);
		height = new DoubleFlag("-h", "--height", 1, -1);
		
		parser.addAll(new Flag[]{input, output, width, height});
		parser.setFreeFlag(0);
	}
	
	// Returns whether the resizing was successful
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
		
		double w, h;
		w = width.isPresent() ? width.getDoubles()[0] : -1;
		h = height.isPresent() ? height.getDoubles()[0] : -1;
		
		BufferedImage[] outImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			outImgs[i] = resize(imgs[i], w, h);
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		output.writeImages(outImgs, names);
		
		return true;
	}
	
	
	// Resize image by sampling the original using bilinear interpolation
	private static BufferedImage resize(BufferedImage img, double w, double h){
		if(img == null){
			return null;
		}
		
		int wd = img.getWidth(), ht = img.getHeight();
		w = w < 0 ? wd : (w <= 1 ? w * wd : w);
		h = h < 0 ? ht : (h <= 1 ? h * ht : h);
		BufferedImage newimg = new BufferedImage((int)w, (int)h, img.getType());
		
		double scaleX = wd / w, scaleY = ht / h;
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				double x = i * scaleX, y = j * scaleY;
				Color c = lerp(img, x, y);
				newimg.setRGB(i, j, c.getRGB());
			}
		}
		return newimg;
	}
	
	// Bilinearly Interpolate the Color "between" pixels
	public static Color lerp(BufferedImage img, double x, double y){
		int w = img.getWidth(), h = img.getHeight();
		if(x > w || y > h){
			return null;
		}
		int i1 = (int)Math.floor(x), i2 = i1 == w - 1 ? w - 2 : i1 + 1;
		int j1 = (int)Math.floor(y), j2 = j1 == h - 1 ? h - 2 : j1 + 1;
		
		Color c1, c2, c3, c4;
		c1 = new Color(img.getRGB(i1, j1));
		c2 = new Color(img.getRGB(i1, j2));
		c3 = new Color(img.getRGB(i2, j1));
		c4 = new Color(img.getRGB(i2, j2));
		
		double w1, w2, w3, w4;
		double den = (i2 - i1) * (j2 - j1);
		w1 = (i2 - x) * (j2 - y) / den;
		w2 = (i2 - x) * (y - j1) / den;
		w3 = (x - i1) * (j2 - y) / den;
		w4 = (x - i1) * (y - j1) / den;
		
		double r, g, b;
		r = w1 * c1.getRed() + w2 * c2.getRed() + w3 * c3.getRed() + w4 * c4.getRed();
		g = w1 * c1.getGreen() + w2 * c2.getGreen() + w3 * c3.getGreen() + w4 * c4.getGreen();
		b = w1 * c1.getBlue() + w2 * c2.getBlue() + w3 * c3.getBlue() + w4 * c4.getBlue();
		return new Color((int)Math.abs(r), (int)Math.abs(g), (int)Math.abs(b));
	}
}
