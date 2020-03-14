package imetl;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Kernel{	
	public static BufferedImage applyKernel(double[][] kernel, BufferedImage img){
		int wd = img.getWidth(), ht = img.getHeight();
		BufferedImage newimg = new BufferedImage(wd, ht, img.getType());
		
		int s = kernel.length, r = kernel[0].length;
		for(int i = 0; i < wd; i++){
			for(int j = 0; j < ht; j++){
				double tr = 0, tg = 0, tb = 0;
				double min = 0, max = 0;
				
				for(int g = 0; g < r; g++){
					for(int h = 0; h < s; h++){
						int x = g + i - r / 2, y = h + j - s / 2;
						
						if(0 <= x && x < wd && 0 <= y && y < ht){
							Color c = new Color(img.getRGB(x, y));
							
							double wei = kernel[h][g];
							min += wei < 0 ? wei * 255 : 0;
							max += wei > 0 ? wei * 255 : 0;
							
							tr += wei * c.getRed();
							tg += wei * c.getGreen();
							tb += wei * c.getBlue();
						}
					}
				}
				
				double diff = max - min;
				tr = (tr - min) * 255 / diff;
				tg = (tg - min) * 255 / diff;
				tb = (tb - min) * 255 / diff;
				
				newimg.setRGB(i, j, new Color((int)tr, (int)tg, (int)tb).getRGB());
			}
		}
		return newimg;
	}
}
