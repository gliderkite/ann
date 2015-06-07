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
	private static ArrayList<WritableImage> patterns_pa = new ArrayList<WritableImage>();
	private static ArrayList<WritableImage> patterns_hopfield = new ArrayList<WritableImage>();
	
	/* Index of the last selected pattern */
	private static int index_pa;
	private static int index_hopfield;
	
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
	private static final void CreateImages(String pattern, Scene scene, String prefix)
	{
		ArrayList<WritableImage> patterns = null;
		
		if (prefix.equals("#pa_"))
		{
			index_pa = 0;
			patterns = patterns_pa;
		}
		else if (prefix.equals("#hopfield_"))
		{
			index_hopfield = 0;
			patterns = patterns_hopfield;
		}
		
		patterns.clear();

		patterns.addAll(new ReadFile("patterns/" + pattern + ".txt").getImages());
			
		if (patterns.size() == 0)
			return;
		
		// show first
		ImageView input_image = (ImageView) scene.lookup(prefix + "input_image");
		input_image.setImage(patterns.get(0));
		
		
		
		Label index_label = (Label) scene.lookup(prefix + "index_label");
		index_label.setText("1/" + patterns.size());
	}
	
	
	/** Sets controls events handlers */
	private static final void SetEventHandlers(Scene scene)
	{
		SetEventHandlersPAHop(scene, "#pa_");
		SetEventHandlersPAHop(scene, "#hopfield_");
	}
	
	
	/** Sets PA/Hopfield events handlers. */
	private static final void SetEventHandlersPAHop(Scene scene, String prefix)
	{
		Slider degradation_slider = (Slider) scene.lookup(prefix + "degradation_slider");
		Label degradation_label = (Label) scene.lookup(prefix + "degradation_label");
		Label step_label = (Label) scene.lookup(prefix + "step_label");
		
		// slider value changed
		degradation_slider.valueProperty().addListener((observable, oldValue, newValue) ->
		{
			int val = newValue.intValue();
			degradation_label.textProperty().setValue(new Integer(val).toString() + "%");
			
			alter(val, scene, prefix);
			
			if (prefix.equals("#hopfield_") && hopfield != null)
			{
				hopfield.Reset();
				step_label.setText("Current Step: 0");
			}
		});
		
		@SuppressWarnings("unchecked")
		ComboBox<String> patterns_combobox = (ComboBox<String>) scene.lookup(prefix + "patterns_combobox");
		ImageView out_image = (ImageView) scene.lookup(prefix + "out_image");
		
		// combo-box selection changed
		patterns_combobox.valueProperty().addListener((observable, oldStr, newStr) ->
		{
			try
			{
				CreateImages(newStr, scene, prefix);
				
				// init inputs and compute weights
				if (prefix.equals("#pa_"))
					pa = new PA(patterns_pa);
				else if (prefix.equals("#hopfield_"))
				{
					hopfield = new Hopfield(patterns_hopfield);
					step_label.setText("Current Step: 0");
				}
				
				degradation_slider.setValue(0);
				degradation_label.textProperty().setValue("0%");
				out_image.setImage(null);
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
				int index = -1;
				ArrayList<WritableImage> patterns = null;
				
				if (prefix.equals("#pa_"))
				{
					index = ++index_pa;
					patterns = patterns_pa;
				}
				else if (prefix.equals("#hopfield_"))
				{
					index = ++index_hopfield;
					patterns = patterns_hopfield;
				}
				
				if (patterns.size() > 0)
				{
					// show the next image
					input_image.setImage(patterns.get(index % patterns.size()));
					String idx = new Integer(index % patterns.size() + 1).toString();
					index_label.setText(idx + "/" + patterns.size());
					degradation_slider.setValue(0);
					degradation_label.textProperty().setValue("0%");
					
					if (prefix.equals("#hopfield_") && hopfield != null)
					{
						hopfield.Reset();
						step_label.setText("Current Step: 0");
					}
				}
			}	
		});
		
		
		Button compute_button = (Button) scene.lookup(prefix + "compute_button");
		
		// compute button click
		compute_button.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e) 
			{
				if (prefix.equals("#pa_") && patterns_pa.size() == 0)
					return;
				else if (prefix.equals("#hopfield_") && patterns_hopfield.size() == 0)
					return;
				
				ArrayList<Integer> input = null;
				
				if (prefix.equals("#pa_") || (prefix.equals("#hopfield_") && hopfield.getStepsNumber() == 0))
				{
					input = new ArrayList<Integer>(PA.HEIGHT * PA.WIDTH);
					Image current = input_image.getImage();
					PixelReader pr = current.getPixelReader();
					
					// compute the input according to the deteriorated image
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
				}
				
				try
				{
					// compute pa/hopfield and show the outcome
					WritableImage wr = null;
					
					if (pa != null && prefix.equals("#pa_"))
						wr = pa.Compute(input);
					else if (hopfield != null && prefix.equals("#hopfield_"))
					{
						wr = hopfield.Compute(input);
						step_label.setText("Current Step: " + hopfield.getStepsNumber());
					}
					
					out_image.setImage(wr);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}


	/** ALters the current input image. */
	private static final void alter(int deterioration, Scene scene, String prefix)
	{
		WritableImage wr = null;
		
		if (prefix.equals("#pa_"))
			wr = patterns_pa.get(index_pa % patterns_pa.size());
		else if (prefix.equals("#hopfield_"))
			wr = patterns_hopfield.get(index_hopfield % patterns_hopfield.size());
		
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
		
		// show the deteriorated pattern
		ImageView input_image = (ImageView) scene.lookup(prefix + "input_image");
		input_image.setImage(copy);
	}


}

