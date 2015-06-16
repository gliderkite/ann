package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import nets.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ReadFile {

	private String filename;
	private int multiplier;
	private ArrayList<WritableImage> images;
	private int DIM;
	
	
	public ReadFile(String filename, int multiplier)
	{
		this.filename = filename;
		this.multiplier = multiplier;
		this.DIM = Main.FILE_IMG_DIM * multiplier;
		this.images = new ArrayList<WritableImage>();
		read();
	}
	
	
	private void read()
	{
		
		try (BufferedReader br = new BufferedReader(new FileReader(this.filename)))
		{
			String sCurrentLine;
			WritableImage wr = new WritableImage(DIM, DIM);
            PixelWriter pw = wr.getPixelWriter();
            int y = 0;
 
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.isEmpty()){
					images.add(wr);
					wr = new WritableImage( DIM,  DIM);
		            pw = wr.getPixelWriter();
		            y = 0;
				} else {
	                for (int x = 0; x < Main.FILE_IMG_DIM ; x++) 
	                {
            			if (sCurrentLine.charAt(x) == 'x'){
		                	for (int i = 0 ; i < this.multiplier ; i++)
		                	{
		                		for (int j = 0; j < this.multiplier ; j++)
		                		{
	                				pw.setColor((x * this.multiplier) + i, (y * this.multiplier) + j, PA.ForeColor);
			                	}
		                	}
		                }
	                }
	                y++;
				}
			}
			

			images.add(wr);
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<WritableImage> getImages()
	{
		return this.images;
	}
}
