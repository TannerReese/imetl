package imetl;

import java.util.List;

import java.io.File;

import java.awt.image.BufferedImage;

public class Blur extends Command{
	private ImageFlag input;
	private ImageFlag box, gaussian;
	private IntegerFlag size;
	
	private static final String HELP_MANUAL =
	  "Usage: imetl blur [-s KERNEL_SIZE] -b BOX_BLUR_OUTPUT [-i] INPUT_IMAGES...\n"
	+ "       imetl blur [-s KERNEL_SIZE] -g GAUSSIAN_OUTPUT [-i] INPUT_IMAGES...\n"
	+ "\n"
	+ "Flags:\n"
	+ "   -i, --input		Image file(s) to be blurred\n"
	+ "   -s, --size		Distance from the center pixel that the kernel incorporates\n"
	+ "			Ex: -s 2 creates a kernel of dimensions 5 x 5\n"
	+ "   -b, --box		Location to store box blurred image\n"
	+ "   -g, --gaussian	Location to store gaussian blurred image\n"
	+ "\n"
	+ "Either the box blur, gaussian blur, or both can be used.\n"
	+ "The blurred images will be saved to their respective files.\n"
	+ "\n"
	+ "Other commands can be seen by calling:\n"
	+ "   imetl --help";
	
	public Blur(){
		super("blur");
		helpManual = HELP_MANUAL;
		
		input = new ImageFlag("-i", "--input", -1);
		size = new IntegerFlag("-s", "--size", 1, 1);
		
		box = new ImageFlag("-b", "--box", 1);
		gaussian = new ImageFlag("-g", "--gaussian", 1);
		
		parser.addAll(new Flag[]{input, size, box, gaussian});
	}
	
	protected boolean execute(){
		if(!input.hasValue()){
			System.out.println("No Input provided");
			return false;
		}
		
		if(!box.hasValue() && !gaussian.hasValue()){
			System.out.println("No Output provided");
			return false;
		}
		BufferedImage[] imgs = input.readImages();
		List<String> vls = input.getValues();
		
		int sz = 1;
		if(size.isPresent()){
			sz = size.getIntegers()[0];
		}
		
		double[][][] boxKernels = getBoxBlur(sz), gaussKernels = getGaussianBlur(sz);
		BufferedImage[] boxImgs = new BufferedImage[imgs.length], gaussImgs = new BufferedImage[imgs.length];
		String[] names = new String[imgs.length];
		
		BufferedImage tmp;
		for(int i = 0; i < imgs.length; i++){
			if(box.isPresent()){
				tmp = Kernel.applyKernel(boxKernels[0], imgs[i]);
				boxImgs[i] = Kernel.applyKernel(boxKernels[1], tmp);
			}
			
			if(gaussian.isPresent()){
				tmp = Kernel.applyKernel(gaussKernels[0], imgs[i]);
				gaussImgs[i] = Kernel.applyKernel(gaussKernels[1], tmp);
			}
			
			names[i] = vls.get(i) == null ? null : new File(vls.get(i)).getName();
		}
		
		if(box.isPresent()){
			box.writeImages(boxImgs, names);
		}
		
		if(gaussian.isPresent()){
			gaussian.writeImages(gaussImgs, names);
		}
		return true;
	}
	
	
	public static double[][][] getBoxBlur(int size){
		int length = 2 * size + 1;
		double[][] vert = new double[length][1], horz = new double[1][length];
		
		for(int i = 0; i < length; i++){
			vert[i][0] = 1;
			horz[0][i] = 1;
		}
		return new double[][][]{vert, horz};
	}
	
	public static double[][][] getGaussianBlur(int size){
		int length = 2 * size + 1;
		double[][] vert = new double[length][1], horz = new double[1][length];
		
		double mid = length / 2.0, stdev = size / 2.0;
		double z, v;
		for(int i = 0; i < length; i++){
			z = (i - mid) / stdev;
			v = Math.exp(- z * z / 2);
			
			vert[i][0] = v;
			horz[0][i] = v;
		}
		return new double[][][]{vert, horz};
	}
}
