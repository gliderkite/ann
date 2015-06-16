package nets;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.image.WritableImage;


public class Perceptron extends Network
{
	/* Vector of weights. */
	private ArrayList<Float> weights;
	
	/* Bias */
	float bias;
	
	
	/** Initializes a new instance of the class. */
	public Perceptron(ArrayList<WritableImage> patternsImg, int size)
	{
		DIM = size;
		nInputs = DIM * DIM;
		
		// init patterns
		ArrayList<ArrayList<Integer>> patterns = Initialize(patternsImg);
		
		// init weights
		Random rand = new Random();
		weights = new ArrayList<Float>(nInputs);
		
		for (int i = 0; i < nInputs; i++)
			weights.add(rand.nextFloat() - 0.5f);

		// init bias
		bias = rand.nextFloat() - 0.5f;
		
		
		//Learn(patterns);
	}
}
