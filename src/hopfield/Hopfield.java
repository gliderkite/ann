package hopfield;

import java.util.ArrayList;

import javafx.scene.image.WritableImage;
import pa.PA;


/** Hopfield Neural Network. */
public class Hopfield extends PA
{
	/** Initializes a new instance of the class. */
	public Hopfield(ArrayList<WritableImage> patternsImg) 
	{
		super(patternsImg);
		
		// clears the diagonal
		for (int i = 0; i < nInputs; i++)
			weights.get(i).set(i, 0);
	}

}
