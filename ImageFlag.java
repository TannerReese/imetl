package imetl;

import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageFlag extends Flag{
	public ImageFlag(String fl){this(fl, 1);}
	public ImageFlag(String fl, int args){this(new String[]{fl}, args);}
	
	public ImageFlag(String sh, String ln){this(sh, ln, 1);}
	public ImageFlag(String sh, String ln, int args){this(new String[]{sh, ln}, args);}
	
	public ImageFlag(String[] aliases){this(aliases, 1);}
	public ImageFlag(String[] aliases, int args){
		super(aliases, args);
	}
	
	
	public BufferedImage[] readImages(){
		BufferedImage[] imgs = new BufferedImage[values.size()];
		for(int i = 0; i < values.size(); i++){
			String imgpath = values.get(i);
			try{
				imgs[i] = ImageIO.read(new File(imgpath));
			}catch(IOException ex){
				System.out.println("Image Not Found " + imgpath);
				imgs[i] = null;
			}
		}
		
		return imgs;
	}
	
	public boolean writeImages(BufferedImage img, String name){
		return writeImages(new BufferedImage[]{img}, new String[]{name});
	}
	
	public boolean writeImages(BufferedImage[] imgs, String[] names){
		if(values.size() == 0){
			return false;
		}
		
		String dirname = values.get(0);
		if(dirname != null && new File(dirname).isDirectory()){
			for(int i = 0; i < imgs.length; i++){
				if(imgs[i] != null && i < names.length){
					if(names[i] == null){
						System.out.println("No Name provided for Image");
					}else{
						writeImage(imgs[i], new File(dirname, names[i]));
					}
				}
			}
		}else{
			for(int i = 0; i < imgs.length; i++){
				if(imgs[i] != null && i < values.size()){
					writeImage(imgs[i], values.get(i));	
				}
			}
		}
		
		return true;
	}
	
	private static boolean writeImage(BufferedImage img, File fl){
		return writeImage(img, fl.toString());
	}
	private static boolean writeImage(BufferedImage img, String path){
		String format = getFormat(path);
		
		if(!(new File(path)).isDirectory()){
			try{
				if(!ImageIO.write(img, format, new File(path))){
					System.out.println("Unable to Writer for Format " + format);
				}else{
					return true;
				}
			}catch(IOException ex){
				System.out.println("Unable to Write Image to " + path);
			}
		}else{
			System.out.println("Write location is a Directory");
		}
		
		return false;
	}
	
	
	public static String getFormat(String path){
		return path.substring(path.lastIndexOf('.') + 1);
	}
}
