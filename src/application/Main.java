package application;
	
import pa.PA;
import hopfield.Hopfield;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Main extends Application 
{
	
	/* Array of patterns */
	private static ArrayList<WritableImage> patterns = new ArrayList<WritableImage>();
	
	/* Index of the last selected pattern */
	private static int index;
	
	/* Pattern Associator */
	private static PA pa;
	
	/* Hopfield Neural Network. */
	private static Hopfield hopfield;
	
	
	
	@Override
	public void start(Stage primaryStage) 
	{
		try 
		{
			Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
			Scene scene = new Scene(root);
			
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			SetEventHandlers(scene);
			
			primaryStage.setResizable(false);
			primaryStage.show();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) 
	{
		launch(args);
	}
	
	
	/** Create pattern images. */
	private static void CreateImages(String pattern, Scene scene, String prefix)
	{
		patterns.clear();

		patterns.addAll(new ReadFile("resources/patterns/" + pattern + ".txt").getImages());
			
		if (patterns.size() == 0)
			return;
		
		// show first
		ImageView input_image = (ImageView) scene.lookup(prefix + "input_image");
		input_image.setImage(patterns.get(0));
		index = 0;
		
		Label index_label = (Label) scene.lookup(prefix + "index_label");
		index_label.setText("1/" + patterns.size());
	}
	
	
	private static void SetEventHandlers(Scene scene)
	{
		SetEventHandlersPAHop(scene, "#pa_");
		SetEventHandlersPAHop(scene, "#hopfield_");
	}
	
	
	private static void SetEventHandlersPAHop(Scene scene, String prefix)
	{
		Slider degradation_slider = (Slider) scene.lookup(prefix + "degradation_slider");
		Label degradation_label = (Label) scene.lookup(prefix + "degradation_label");
		
		// slider value changed
		degradation_slider.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			int val = newValue.intValue();
			degradation_label.textProperty().setValue(new Integer(val).toString() + "%");
			
			alter(val, scene, prefix);
		});
		
		@SuppressWarnings("unchecked")
		ComboBox<String> patterns_combobox = (ComboBox<String>) scene.lookup(prefix + "patterns_combobox");
		
		// combobox selection changed
		patterns_combobox.valueProperty().addListener((observable, oldStr, newStr) ->
		{
			try
			{
				CreateImages(newStr, scene, prefix);
				
				// init inputs and compute weights
				if (prefix.equals("#pa_"))
					pa = new PA(patterns);
				else if (prefix.equals("#hopfield_"))
					hopfield = new Hopfield(patterns);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
		
		Button next_button = (Button) scene.lookup(prefix + "next_button");
		ImageView input_image = (ImageView) scene.lookup(prefix + "input_image");
		Label index_label = (Label) scene.lookup(prefix + "index_label");
		
		// next button click
		next_button.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e) 
			{
				if (patterns.size() > 0)
				{
					// show the next image
					input_image.setImage(patterns.get(++index % patterns.size()));
					String idx = new Integer(index % patterns.size() + 1).toString();
					index_label.setText(idx + "/" + patterns.size());
					degradation_slider.setValue(0);
					degradation_label.textProperty().setValue("0%");
				}
			}	
		});
		
		
		Button compute_button = (Button) scene.lookup(prefix + "compute_button");
		ImageView out_image = (ImageView) scene.lookup(prefix + "out_image");
		
		// compute button click
		compute_button.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e) 
			{
				if (patterns.size() > 0)
				{
					ArrayList<Integer> input = new ArrayList<Integer>();
					
					Image current = input_image.getImage();
					PixelReader pr = current.getPixelReader();
					
					// compute the input according to the deteriored image
					for (int y = 0; y < PA.HEIGHT; y++) 
		            {
		                for (int x = 0; x < PA.WIDTH; x++) 
		                {
		                	Color col = pr.getColor(x, y);
		                	
		                	if (col.equals(PA.ForeColor))
		                		input.add(PA.Foreground);
		                	else
		                		input.add(PA.Background);
		                }
		            }
					
					try
					{
						// compute pa/hopfield and show the outcome
						WritableImage wr = null;
						
						if (pa != null && prefix.equals("#pa_"))
							wr = pa.Compute(input);
						else if (hopfield != null && prefix.equals("#hopfield_"))
							wr = hopfield.Compute(input);
						
						out_image.setImage(wr);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}	
		});
	}


	private static void alter(int deterioration, Scene scene, String prefix)
	{
		WritableImage wr = patterns.get(index % patterns.size());
		PixelReader pr = wr.getPixelReader();
		
		// clone the original pattern
		WritableImage copy = new WritableImage(pr, PA.WIDTH, PA.HEIGHT);
		PixelWriter pw = copy.getPixelWriter();
		pr = copy.getPixelReader();
        
		deterioration = deterioration * (PA.WIDTH * PA.HEIGHT) / 100;
		Random rand = new Random();
		
		// alter the pattern
		for (int i = 0; i < deterioration; i++)
		{
			int x = rand.nextInt(PA.WIDTH);
			int y = rand.nextInt(PA.HEIGHT);
			Color color = pr.getColor(x, y);
			
			if (color.equals(PA.ForeColor))
				pw.setColor(x, y, Color.WHITE);
			else
				pw.setColor(x, y, PA.ForeColor);
		}
		
		// show the deteriored pattern
		ImageView input_image = (ImageView) scene.lookup(prefix + "input_image");
		input_image.setImage(copy);
	}


}

