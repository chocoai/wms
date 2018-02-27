package com.xyy.bill.print.meta;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) throws Exception{
		Robot robot=new Robot();
		Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
		for(int i=0;i<10000;i++){
			BufferedImage images=robot.createScreenCapture(new Rectangle(0, 0, (int)dim.getWidth(), (int)dim.getHeight()));
			ImageIO.write(images,"jpg",new File("h:/test/ttt"+i+".jpg"));
			Thread.sleep(200);
		}

	}

}
