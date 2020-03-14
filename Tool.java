package imetl;

public class Tool{
	private static final String VERSION = "1.0.0";
	private static final String HELP_MANUAL = 
	  "Usage: imetl <COMMAND> [OPTIONS] INPUT_IMAGES...\n"
	+ "\n"
	+ "Commands:\n"
	+ "   rgb		Separate or combine image(s) into their RGB components\n"
	+ "   hsv		Separate or combine image(s) into their HSV components\n"
	+ "   grey		Convert image(s) into various types of greyscale image\n"
	+ "   sobel	Calculate x, y, and magnitude values for the Sobel-Feldman operator\n"
	+ "   blur		Blur image(s) using a varying strength box or gaussian blur\n"
	+ "   crop		Crop image(s) to a given width and height at a particular location\n"
	+ "   resize	Resize image(s) to a given width and height\n"
	+ "   invert	Invert image(s) by subtracting the RGB components from 255\n"
	+ "   huerotate	Rotate the hue value of image(s) by an angle\n"
	+ "   mask		Merge to images according to the values in a mask image\n"
	+ "   threshold	Convert an image to an image strictly comprised of black and white pixels\n"
	+ "   gamma	Perform gamma scaling on the value channel of an image\n"
	+ "\n"
	+ "For more information about each command call:\n"
	+ "   imetl <COMMAND> --help";
	
	// List of commands to execute according to second argument
	public static Command[] commands = {
		new RGB(), new HSV(),
		new Grey(),
		new Sobel(),
		new Blur(),
		new Crop(), new Resize(),
		new Invert(),
		new HueRotate(),
		new Mask(),
		new Threshold(),
		new Gamma()
	};
	
	// Find command which matches a particular string
	public static Command getCommandByName(String name){
		name = name.toLowerCase();
		for(Command comd : commands){
			if(comd.getName().toLowerCase().equals(name)){
				return comd;
			}
		}
		return null;
	}
	
	public static void main(String[] args){
		if(args.length == 0){
			System.out.println("No Command provided");
			return;
		}
		
		// Display Help Manual and Version number
		if(args[0].toLowerCase().equals("--help")){
			System.out.println(HELP_MANUAL);
			return;
		}
		if(args[0].toLowerCase().equals("--version")){
			System.out.println("imetl version: " + VERSION);
			return;
		}
		
		Command comd = getCommandByName(args[0]);
		
		if(comd == null){
			System.out.println("No Command found with the Name " + args[0]);
			return;
		}
		
		// Remove command itself from the args passed to execute
		String[] newargs = new String[args.length - 1];
		for(int i = 1; i < args.length; i++){
			newargs[i - 1] = args[i];
		}
		
		// Execute command and return success or failure
		if(!comd.execute(newargs)){
			System.out.println("Transform was Unsuccessful");
			System.out.println("Call 'imetl --help' for information about Commands");
		}
	}
}
