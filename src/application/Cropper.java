package application;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import javax.imageio.ImageIO;


public class Cropper extends Thread
{
	public static boolean running;
	public static long ausgabe;
	public static Controller controller;	
	public static int x;
	public static int y;
	public static int width;
	public static int height;
	public static ArrayList<String> skippedFileNames = new ArrayList<String>();
	
	public static void setValues(int x1, int y1, int width1, int height1)
	{
		x = x1;
		y = y1;
		width = width1;
		height = height1;			
	}	
	
	public static boolean checkDimensionsShort(File file)
	{
		try
		{
			BufferedImage image = ImageIO.read(file);

			if((x + width) > image.getWidth())
			{
				return false;
			}

			if((y + height) > image.getHeight())
			{
				return false;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		return true;
	}
	
	public static int[] checkDimensions(File file)
	{
		try
		{
			BufferedImage image = ImageIO.read(file);			
			if((x + width) > image.getWidth())
			{			
				int[] result = new int[2];
				result[0] = 0;
				result[1] = image.getWidth();
				return result;
			}

			if((y + height) > image.getHeight())
			{
				int[] result = new int[2];
				result[0] = 1;
				result[1] = image.getHeight();
				return result;
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		return new int[]{2};
	}	
	
	@Override
	public void run() 
	{ 
		int skipped = 0;
		
		File directory = new File(controller.path.replace("\\", "/") + "/CropShot/");
		directory.mkdirs();
		
		for(int i = 0; i < controller.files.size(); i++)
		{
			if(running)
			{
				File current = controller.files.get(i);
				if(checkDimensionsShort(current))
				{
					try
					{	
						BufferedImage image = ImageIO.read(current);
						BufferedImage img = image.getSubimage(x, y, width, height); 
					
						BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics g = copyOfImage.createGraphics();
						g.drawImage(img, 0, 0, null);		
						
						String fileName = current.getName();
						fileName = fileName.substring(0, fileName.length()-4);
						File datei = new File(controller.path.replace("\\", "/") + "/CropShot/" + fileName + ".png");					
						ImageIO.write(copyOfImage, "png", datei);									
					}
					catch(Exception e1)
					{	
						skippedFileNames.add(current.getName());
						skipped++;
					}
				}
				else
				{
					skippedFileNames.add(current.getName());
					skipped++;
				}
					
				double fortschritt = (i+1)/(double)controller.files.size();	
				final int j = i;
				
				Platform.runLater(()->{							
					controller.setProgressBar(fortschritt);
				});	
				
				Platform.runLater(()->{							
					controller.setProgressLabel((j+1) + "/" + controller.files.size());
				});	
				
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{							
					running = false;
				}	
			}
		}
		
		if(running)
		{
			final int skippedFiles = skipped;
		 	Platform.runLater(()->{	
		 		
		 		Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Abgeschlossen");
				alert.setHeaderText("");
				alert.setContentText("Dateien erfolgreich zugeschnitten.\n\n‹bersprungene Dateien: " + skippedFiles);			
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(new Image(this.getClass().getResource("/application/crop.png").toString()));
				dialogStage.setMinHeight(175);			
				
				String skippedText = "";
				for(int k = 0; k < skippedFileNames.size(); k++)
				{
					skippedText += skippedFileNames.get(k) + "\n"; 
				}			
	
				Label label = new Label("‹bersprungene Dateien:");
	
				TextArea textArea = new TextArea(skippedText);
				textArea.setEditable(false);
				textArea.setWrapText(true);
	
				textArea.setMaxWidth(Double.MAX_VALUE);
				textArea.setMaxHeight(Double.MAX_VALUE);
				GridPane.setVgrow(textArea, Priority.ALWAYS);
				GridPane.setHgrow(textArea, Priority.ALWAYS);
	
				GridPane expContent = new GridPane();
				expContent.setMaxWidth(Double.MAX_VALUE);
				expContent.add(label, 0, 0);
				expContent.add(textArea, 0, 1);
				
				alert.getDialogPane().setExpandableContent(expContent);			
				
				ButtonType buttonTypeOne = new ButtonType("Ordner ˆffnen");
				ButtonType buttonTypeTwo = new ButtonType("OK");
				
				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
				
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonTypeOne)
				{
					controller.openButton();
				}
				else
				{
					alert.close();
				}
			
				running = false;
		 	 });			 	
		
			skippedFileNames.clear();		
			 	
			Platform.runLater(()->{
				controller.clearList();		
				controller.setLabelZero();	
				controller.setProgressLabel("");
				controller.setProgressBar(0.0);
				controller.choosen = false;
				controller.clearPreview();
				controller.cropButton.setDisable(true);
				controller.cancelButton.setDisable(true);
				controller.chooseButton.requestFocus();
				controller.chooseButton.setDisable(false);
				controller.selectButton.setDisable(false);
				controller.savePathButton.setDisable(false);
				controller.list.setDisable(false);
				controller.files.clear();		
				controller.correct.clear();		
			});	
		}
		else
		{
			skippedFileNames.clear();		
		 	
			Platform.runLater(()->{				
				controller.setLabelZero();	
				controller.setProgressLabel("");
				controller.setProgressBar(0.0);			
				controller.clearPreview();	
				controller.cropButton.setDisable(false);
				controller.cancelButton.setDisable(true);			
				controller.chooseButton.setDisable(false);
				controller.selectButton.setDisable(false);
				controller.savePathButton.setDisable(false);
				controller.list.setDisable(false);				
			});	
		}
	}
}