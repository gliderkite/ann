package nets;

import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public abstract class Network 
{
	/* Bitmap WIDTH and HEIGHT */
	protected static int DIM = 0;
	
	/* Background input value. */
	public final static int Background = -1;
	
	/* Foreground input value. */
	public final static int Foreground = 1;
	
	/* Foreground color. */
	public final static Color ForeColor = Color.RED;
	
	/* Number of inputs. */
	protected int nInputs = 0;
	
	
	
	/** Initializes inputs. */
	protected final ArrayList<ArrayList<Integer>> Initialize(ArrayList<WritableImage> patternsImg)
	{
		/* Array of input patterns. */
		ArrayList<ArrayList<Integer>> patterns = new ArrayList<ArrayList<Integer>>(patternsImg.size());
		
		for (int i = 0; i < patternsImg.size(); i++)
		{
			WritableImage wr = patternsImg.get(i);
			PixelReader pr = wr.getPixelReader();
			
			// all input patterns must have the same size
			if ((int) wr.getWidth() != DIM)
				throw new IllegalArgumentException();
			if ((int) wr.getHeight() != DIM)
				throw new IllegalArgumentException();
			
			patterns.add(new ArrayList<Integer>(nInputs));
			
			for (int y = 0; y < wr.getHeight(); y++)
			{
				for (int x = 0; x < wr.getWidth(); x++)
				{
					Color color = pr.getColor(x, y);
					
					if (color.equals(PA.ForeColor))
						patterns.get(i).add(Foreground);
					else
						patterns.get(i).add(Background);
				}
			}
		}
		
		return patterns;
	}
	
	
}
