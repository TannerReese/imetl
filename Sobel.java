package imetl;

import java.util.List;

import java.io.File;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Sobel extends Command{
	private double[][] verticalKernel = {
		new double[]{-1, -2, -1},
		new double[]{0, 0, 0},
		new double[]{1, 2, 1}
	};
	private double[][] horizontalKernel = {
		new double[]{-1, 0, 1},
		new double[]{-2, 0, 2},
		new double[]{-1, 0, 1}
	};
	
	private ImageFlag input, output, vertical, horizontal;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl sobel -o MAGNITUDE_OUTPUT [-i] INPUT_IMAGES...\n"
	+ "       imetl sobel [-v VERTICAL] [-h HORIZONTAL] [-i] INPUT_IMAGES...\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image file to apply sobel operator to\n"
	+ "   -o, --output		Location to save gradient magnitude\n"
	+ "   -v, --vertical	Location to save y-gradient\n"
	+ "   -h, --horizontal	Location to save x-gradient\n"
	+ "\n"
	+ "The x and y gradients are calculated using kernel\n"
	+ "operators. The zero value of these gradients is\n"
	+ "stored as a 128-grey value. The magnitude is then\n"
	+ "the magnitude of the normalized magnitude of the\n"
	+ "gradient vectors.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";

	
	public Sobel(){
		super("sobel");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		output = new ImageFlag("-o", "--output", 1);
		
		vertical = new ImageFlag("-v", "--vertical", 1);
		horizontal = new ImageFlag("-h", "--horizontal", 1);
		
		parser.addAll(new Flag[]{input, output, vertical, horizontal});
		parser.setFreeFlag(0);
	}
	
	protected boolean execute(){
		if(!input.hasValue()){
			System.out.println("No Input provided");
			return false;
		}
		if(!output.hasValue() && !vertical.hasValue() && !horizontal.hasValue()){
			System.out.println("No Output provided");
			return false;
		}
		BufferedImage[] imgs = input.readImages();
		List<String> vls = input.getValues();
		
		boolean doVert = output.isPresent() || vertical.isPresent();
		boolean doHorz = output.isPresent() || horizontal.isPresent();
		BufferedImage[] vertImgs = new BufferedImage[imgs.length], horzImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		for(int i = 0; i < imgs.length; i++){
			BufferedImage im = imgs[i];
			if(doVert){
				vertImgs[i] = im == null ? null : Kernel.applyKernel(verticalKernel, im);
			}
			
			if(doHorz){
				horzImgs[i] = im == null ? null : Kernel.applyKernel(horizontalKernel, im);
			}
			
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		if(output.isPresent()){
			BufferedImage[] outImgs = new BufferedImage[imgs.length];
			for(int i = 0; i < imgs.length; i++){
				outImgs[i] = combineSobel(vertImgs[i], horzImgs[i]);
			}
			output.writeImages(outImgs, names);
		}
		
		if(vertical.isPresent()){
			vertical.writeImages(vertImgs, names);
		}
		
		if(horizontal.isPresent()){
			horizontal.writeImages(horzImgs, names);
		}
		
		return true;
	}
	
	
	private static BufferedImage combineSobel(BufferedImage vert, BufferedImage horz){
		int w = vert.getWidth(), h = vert.getHeight();
		if(w != horz.getWidth() || h != horz.getHeight()){
			System.out.println("Image Dimensions Do Not Match");
			return null;
		}
		
		BufferedImage newimg = new BufferedImage(w, h, vert.getType());
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				Color colVert, colHorz;
				colVert = new Color(vert.getRGB(i, j));
				colHorz = new Color(horz.getRGB(i, j));
				newimg.setRGB(i, j, combineColors(colVert, colHorz).getRGB());
			}
		}
		return newimg;
	}
	
	private static Color combineColors(Color cv, Color ch){
		int rv = cv.getRed(), gv = cv.getGreen(), bv = cv.getBlue();
		int rh = ch.getRed(), gh = ch.getGreen(), bh = ch.getBlue();
		
		rv = 2 * (rv - 128);
		gv = 2 * (gv - 128);
		bv = 2 * (bv - 128);
		
		rh = 2 * (rh - 128);
		gh = 2 * (gh - 128);
		bh = 2 * (bh - 128);
			
		int r, g, b;
		double rt2 = Math.sqrt(2);
		r = (int)(Math.sqrt(rv * rv + rh * rh) / rt2);
		g = (int)(Math.sqrt(gv * gv + gh * gh) / rt2);
		b = (int)(Math.sqrt(bv * bv + bh * bh) / rt2);
		
		return new Color(r, g, b); 
	}
}
