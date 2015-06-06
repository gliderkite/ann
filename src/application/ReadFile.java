package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import pa.PA;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ReadFile {

	private String filename;
	private ArrayList<WritableImage> images;
	
	public ReadFile(String filename)
	{
		this.filename = filename;
		this.images = new ArrayList<WritableImage>();
		read();
	}
	
	
	private void read()
	{
		
		try (BufferedReader br = new BufferedReader(new FileReader(this.filename)))
		{
			String sCurrentLine;
			WritableImage wr = new WritableImage(PA.WIDTH, PA.HEIGHT);
            PixelWriter pw = wr.getPixelWriter();
            int y = 0;
 
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.isEmpty()){
					images.add(wr);
					wr = new WritableImage(PA.WIDTH, PA.HEIGHT);
		            pw = wr.getPixelWriter();
		            y = 0;
				} else {
	                for (int x = 0; x < PA.WIDTH; x++) 
	                {
	                	if (sCurrentLine.charAt(x) == 'x')
	                		pw.setColor(x, y, PA.ForeColor);
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
