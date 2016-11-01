package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class SelectController implements Initializable
{
	@FXML private Slider sliderWidth;
	@FXML private Slider sliderHeight;
	@FXML private Slider sliderX;
	@FXML private Slider sliderY;
	@FXML private TextField textFieldWidth;	
	@FXML private TextField textFieldHeight;		
	@FXML private TextField textFieldX;	
	@FXML private TextField textFieldY;
	@FXML private StackPane stackPane;
	@FXML private Pane paneImage;
	@FXML private Pane pane;
	@FXML private VBox vbox;
	@FXML private Button buttonOK;
	@FXML private Button buttonCancel;
	
	public Stage stage;
	private int previewWidth;
	private int previewHeight;
	private double scale;
	private Rectangle selection;
	private Rectangle border;
	private double originalWidth;
	private double originalHeight;	
	private int maxX;	
	private int maxY;
	private Controller controller;
	private File file;
	
	public void setStage(Stage s)
	{
		stage = s;
	}
	
	public void setController(Controller c)
	{
		controller = c;
	}
	
	public void setImage(File f)
	{
		file = f;
	}
	
	@FXML
	public void buttonOK()
	{	
		int x = (int)sliderX.getValue();
		int y = (int)sliderY.getValue();
		int width = (int)sliderWidth.getValue();
		int height = (int)sliderHeight.getValue();		
		
		Cropper.setValues(x, y, width, height);		
		controller.updateLabels();
		stage.close();
		controller.setList(true);	
		controller.clearPreview();
	}
	
	@FXML
	public void buttonCancel()
	{
		stage.close();
	}
	
	public void init()
	{
		sliderWidth.setMajorTickUnit(10.0);
		sliderWidth.setMinorTickCount(1);
		sliderWidth.setShowTickLabels(false);
		sliderWidth.setShowTickMarks(false);
		
		sliderHeight.setMajorTickUnit(10.0);
		sliderHeight.setMinorTickCount(1);
		sliderHeight.setShowTickLabels(false);
		sliderHeight.setShowTickMarks(false);
		
		sliderX.setMajorTickUnit(10.0);
		sliderX.setMinorTickCount(1);
		sliderX.setShowTickLabels(false);
		sliderX.setShowTickMarks(false);	
		
		sliderY.setMajorTickUnit(10.0);
		sliderY.setMinorTickCount(1);
		sliderY.setShowTickLabels(false);
		sliderY.setShowTickMarks(false);			
		
		try
		{
			BufferedImage img = ImageIO.read(file);
			
			originalWidth = img.getWidth();
			originalHeight = img.getHeight();				
			
			paneImage.setStyle("-fx-background-image: url(\"" + file.toURI().toURL().toString() + "\"); -fx-background-size: contain; -fx-background-repeat: no-repeat; -fx-background-position: center;");		
			
			stackPane.widthProperty().addListener(new ChangeListener<Number>()
			{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{	
					if(stackPane.getWidth() > 0.0)
					{						
						previewWidth = (int)stackPane.getWidth();
						scale = previewWidth / originalWidth;
						previewHeight = (int)(scale * originalHeight);
						
						if(previewHeight > stackPane.getHeight())
						{
							//make it full-height instead of full-width								
							previewHeight = (int)stackPane.getHeight();
							scale = previewHeight / originalHeight;
							previewWidth = (int)(scale * originalWidth);				
						}	
						
						pane.setMaxWidth(previewWidth);
						pane.setMaxHeight(previewHeight);
					
						updateBorder();
						updateRectangle((int)sliderX.getValue(), (int)sliderY.getValue(), (int)sliderWidth.getValue(), (int)sliderHeight.getValue());		
					}								
				}
			});	
			

			stackPane.heightProperty().addListener(new ChangeListener<Number>()
			{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{	
					if(stackPane.getHeight() > 0.0)
					{	
						previewHeight = (int)stackPane.getHeight();
						scale = previewHeight / originalHeight;
						previewWidth = (int)(scale * originalWidth);	
						
						if(previewWidth > stackPane.getWidth())
						{
							previewWidth = (int)stackPane.getWidth();
							scale = previewWidth / originalWidth;
							previewHeight = (int)(scale * originalHeight);				
						}	
						
						pane.setMaxWidth(previewWidth);
						pane.setMaxHeight(previewHeight);
											
						updateBorder();
						updateRectangle((int)sliderX.getValue(), (int)sliderY.getValue(), (int)sliderWidth.getValue(), (int)sliderHeight.getValue());
					}
				}
			});	
						
					
			sliderWidth.setMax(originalWidth);
			sliderHeight.setMax(originalHeight);
			
			sliderWidth.setValue(originalWidth / 2);
			sliderHeight.setValue(originalHeight / 2);	
			
			maxX = (int)originalWidth - (int)sliderWidth.getValue();
			sliderX.setMax(maxX);	
			
			maxY = (int)originalHeight - (int)sliderHeight.getValue();
			sliderY.setMax(maxY);			
			
			updateBorder();
			updateRectangle((int)sliderX.getValue(), (int)sliderY.getValue(), (int)sliderWidth.getValue(), (int)sliderHeight.getValue());				
			
			sliderWidth.valueProperty().addListener(new ChangeListener<Number>()
			{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{
					updateRectangle((int)sliderX.getValue(), (int)sliderY.getValue(), newValue.intValue(), (int)sliderHeight.getValue());		
					maxX = (int)originalWidth - (int)sliderWidth.getValue();					
					sliderX.setMax(maxX);
				}
			});
			
			sliderHeight.valueProperty().addListener(new ChangeListener<Number>()
			{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{					
					updateRectangle((int)sliderX.getValue(), (int)sliderY.getValue(), (int)sliderWidth.getValue(), newValue.intValue());	
					maxY = (int)originalHeight - (int)sliderHeight.getValue();
					sliderY.setMax(maxY);
				}
			});	
			
			sliderX.valueProperty().addListener(new ChangeListener<Number>()
			{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{							
					updateRectangle(newValue.intValue(), (int)sliderY.getValue(), (int)sliderWidth.getValue(), (int)sliderHeight.getValue());					
				}
			});	
			
			sliderY.valueProperty().addListener(new ChangeListener<Number>()
			{
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{							
					updateRectangle((int)sliderX.getValue(), newValue.intValue(),(int)sliderWidth.getValue(), (int)sliderHeight.getValue());					
				}
			});	
			
			textFieldWidth.textProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
				{					
					try
					{
						int i = Integer.parseInt(newValue);	
						if(i < originalWidth)
						{
							sliderWidth.setValue(i);
						}
						else
						{
							sliderWidth.setValue((int)originalWidth);
							textFieldWidth.setText(String.valueOf((int)originalWidth));
						}												
					}
					catch(NumberFormatException e)
					{
						textFieldWidth.setText(oldValue);
					}					
				}
			});			

			textFieldHeight.textProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
				{					
					try
					{
						int i = Integer.parseInt(newValue);	
						if(i < originalHeight)
						{
							sliderHeight.setValue(i);
						}
						else
						{
							sliderHeight.setValue((int)originalHeight);
							textFieldHeight.setText(String.valueOf((int)originalHeight));
						}											
					}
					catch(NumberFormatException e)
					{
						textFieldHeight.setText(oldValue);
					}					
				}
			});		
			
			textFieldX.textProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
				{					
					try
					{
						int i = Integer.parseInt(newValue);	
						if(i < maxX)
						{
							sliderX.setValue(i);
						}
						else
						{
							sliderX.setValue(maxX);
							textFieldX.setText(String.valueOf(maxX));
						}											
					}
					catch(NumberFormatException e)
					{
						textFieldX.setText(oldValue);
					}					
				}
			});	
			
			textFieldY.textProperty().addListener(new ChangeListener<String>()
			{
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
				{					
					try
					{
						int i = Integer.parseInt(newValue);	
						if(i < maxY)
						{
							sliderY.setValue(i);
						}
						else
						{
							sliderY.setValue(maxY);
							textFieldY.setText(String.valueOf(maxY));
						}											
					}
					catch(NumberFormatException e)
					{
						textFieldY.setText(oldValue);
					}					
				}
			});				
		}
		catch(IOException e)
		{			
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{	
		
	}
	
	private void updateRectangle(int x, int y, int width, int height)
	{		
		selection = new Rectangle();
		selection.setX(x * scale);
		selection.setY(y * scale);
		selection.setWidth(width * scale);
		selection.setHeight(height * scale);
		selection.setFill(Color.rgb(127, 127, 127, 0.7));	
		
		pane.getChildren().clear();	
		
		pane.getChildren().add(border);
		pane.getChildren().add(selection);	
	
		textFieldWidth.setText(String.valueOf((int)sliderWidth.getValue()));
		textFieldHeight.setText(String.valueOf((int)sliderHeight.getValue()));
		textFieldX.setText(String.valueOf((int)sliderX.getValue()));
		textFieldY.setText(String.valueOf((int)sliderY.getValue()));
	}
	
	private void updateBorder()
	{				
		border = new Rectangle();
		border.setX(0);
		border.setY(0);
		border.setWidth((int)previewWidth);
		border.setHeight((int)previewHeight);
		border.setFill(Color.TRANSPARENT);
		border.setStroke(Color.RED);
		border.setStrokeWidth(1.5);	
	}	
}