package nets;

import java.util.ArrayList;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


/** Pattern Associator */
public class PA extends Network
{
	
	/* Matrix of weights. */
	protected ArrayList<ArrayList<Integer>> weights;
	
	
	
	/** Initializes a new instance of the class. */
	public PA(ArrayList<WritableImage> patternsImg, int size)
	{
		DIM = size;
		nInputs = DIM * DIM;
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
	
	
}
