package pa;

import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


/** Pattern Associator */
public class PA 
{
	
	/* Bitmap WIDTH and HEIGHT */
	public static int DIM;
	
	/* Background input value. */
	public final static int Background = -1;
	
	/* Foreground input value. */
	public final static int Foreground = 1;
	
	/* Foreground color. */
	public final static Color ForeColor = Color.RED;
	
	
	
	/* Matrix of weights. */
	protected ArrayList<ArrayList<Integer>> weights;
	
	/* Number of inputs. */
	protected int nInputs = 0;
	
	
	
	/** Initializes a new instance of the class. */
	public PA(ArrayList<WritableImage> patternsImg, int size)
	{
		DIM = size;
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
		
		
		WritableImage wr = new WritableImage(DIM, DIM);
        PixelWriter pw = wr.getPixelWriter();
        
        for (int y = 0; y < DIM; y++)
        {
        	for (int x = 0; x < DIM; x++) 
            {
            	final int k = x + (y * DIM);
            	
            	if (outcome.get(k).equals(Foreground))
            		pw.setColor(x, y, PA.ForeColor);
            }
        }
        
        return wr;
	}
	
	
	/** Compute matrix of weights. */
	private final void Learn(ArrayList<ArrayList<Integer>> patterns)
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
					if (i == j)
						continue;
					
					Integer prev = weights.get(i).get(j);
					weights.get(i).set(j, prev + pattern.get(i) * pattern.get(j));
				}
			}
		}
	}
	
	
	/** Initializes inputs. */
	private final ArrayList<ArrayList<Integer>> Initialize(ArrayList<WritableImage> patternsImg)
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
			
			if (nInputs == 0)
				nInputs = DIM * DIM;
			
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
