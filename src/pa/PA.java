package pa;

import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


/** Pattern Associator */
public class PA 
{
	
	/* Bitmap width */
	public final static int WIDTH = 20;
	
	/* Bitmap height */
	public final static int HEIGHT = 20;
	
	/* Background input value. */
	public final static int Background = -1;
	
	/* Foreground input value. */
	public final static int Foreground = 1;
	
	/* Foreground color. */
	public final static Color ForeColor = Color.RED;
	
	
	
	/* Matrix of weights. */
	private ArrayList<ArrayList<Integer>> weights;
	
	
	private int nInputs = 0;
	private int width = 0;
	private int height = 0;
	
	
	
	/** Initializes a new instance of the class. */
	public PA(ArrayList<WritableImage> patternsImg)
	{
		ArrayList<ArrayList<Integer>> patterns = Initialize(patternsImg);
		Learn(patterns);
	}
	
	
	/** Compute the PA according to the input and the current matrix of weights. */
	public WritableImage Compute(ArrayList<Integer> input)
	{
		ArrayList<Integer> outcome = new ArrayList<Integer>(nInputs);
		
		for (int j = 0; j < nInputs; j++)
	    {
		    int sum = 0;
		    
		    for (int i = 0; i < nInputs; i++)
				sum += input.get(i) * weights.get(i).get(j);
		    
		    if (sum >= 0)
		    	outcome.add(Foreground);
		    else
		    	outcome.add(Background);
	    }
		
		
		WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();
        
        for (int y = 0; y < height; y++)
        {
        	for (int x = 0; x < width; x++) 
            {
            	final int k = x + (y * width);
            	
            	if (outcome.get(k).equals(Foreground))
            		pw.setColor(x, y, PA.ForeColor);
            }
        }
        
        return wr;
	}
	
	
	/** Compute matrix of weights. */
	private void Learn(ArrayList<ArrayList<Integer>> patterns)
	{
		weights = new ArrayList<ArrayList<Integer>>(nInputs);

		// init weights (fill with 0)
		for (int i = 0; i < nInputs; i++)
		{
			weights.add(new ArrayList<Integer>(nInputs));
			
			for (int j = 0; j < nInputs; j++)
				weights.get(i).add(0);
		}
		
		// compute weights
		for (ArrayList<Integer> pattern : patterns)
		{
			for (int i = 0; i < nInputs; i++)
			{
				for (int j = 0; j < nInputs; j++)
				{
					Integer prev = weights.get(i).get(j);
					weights.get(i).set(j, prev + pattern.get(i) * pattern.get(j));
				}
			}
		}
	}
	
	
	/** Initializes inputs. */
	private ArrayList<ArrayList<Integer>> Initialize(ArrayList<WritableImage> patternsImg)
	{
		/* Array of input patterns. */
		ArrayList<ArrayList<Integer>> patterns = new ArrayList<ArrayList<Integer>>(patternsImg.size());
		
		for (int i = 0; i < patternsImg.size(); i++)
		{
			WritableImage wr = patternsImg.get(i);
			PixelReader pr = wr.getPixelReader();
			
			patterns.add(new ArrayList<Integer>());
			
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
			
			if (nInputs == 0)
			{
				 width = (int) wr.getWidth();
				 height = (int) wr.getHeight();
				 nInputs = (int) (wr.getHeight() * wr.getWidth());
			}
			else
			{
				// all input patterns must have the same size
				if ((int) wr.getWidth() != width)
					throw new IllegalArgumentException();
				if ((int) wr.getHeight() != height)
					throw new IllegalArgumentException();
			}
		}
		
		return patterns;
	}
	
}
