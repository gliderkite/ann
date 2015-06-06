package hopfield;

import java.util.ArrayList;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import pa.PA;


/** Hopfield Neural Network. */
public class Hopfield extends PA
{
	/* Network output. */
	private ArrayList<Integer> outcome;
	
	
	
	/** Initializes a new instance of the class. */
	public Hopfield(ArrayList<WritableImage> patternsImg) 
	{
		super(patternsImg);
		
		// clears the diagonal
		for (int i = 0; i < nInputs; i++)
			weights.get(i).set(i, 0);
	}
	
	
	public void Reset()
	{
		outcome = null;
	}
	
	
	/** Compute the PA according to the input and the current matrix of weights. */
	@Override
	public WritableImage Compute(ArrayList<Integer> input)
	{
		ArrayList<Integer> output = null;
		
		if (outcome == null)
		{
			// this is the first step
			outcome = new ArrayList<Integer>(nInputs);
			
			for (int j = 0; j < nInputs; j++)
				outcome.add(0);
			
			output = input;
		}
		else
		{
			// this method has already been called
			output = outcome;
		}
		
		for (int j = 0; j < nInputs; j++)
	    {
		    int sum = 0;
		    
		    for (int i = 0; i < nInputs; i++)
				sum += output.get(i) * weights.get(i).get(j);
		    
		    if (sum >= 0)
		    	outcome.set(j, Foreground);
		    else
		    	outcome.set(j, Background);
	    }
		
		WritableImage wr = new WritableImage(WIDTH, HEIGHT);
        PixelWriter pw = wr.getPixelWriter();
        
        for (int y = 0; y < HEIGHT; y++)
        {
        	for (int x = 0; x < WIDTH; x++) 
            {
            	final int k = x + (y * WIDTH);
            	
            	if (outcome.get(k).equals(Foreground))
            		pw.setColor(x, y, PA.ForeColor);
            }
        }
        
        return wr;
	}

}
