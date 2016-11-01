package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application
{
	@Override
	public void start(Stage stage)
	{
		try 
		{
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("application/CropShotGUI.fxml"));
			Parent root = (Parent)loader.load();				
				    
			Scene scene = new Scene(root,800,630);
			
			stage.setResizable(false);	
			stage.setMinWidth(750.0);
			stage.setMinHeight(630.0);
			
			Controller controller = ((Controller)loader.getController());
			controller.setStage(stage);
			
			stage.getIcons().add(new Image("/application/crop.png"));
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>()
			{
				public void handle(WindowEvent we)
				{
					System.exit(0);
				}
			});
			
			stage.setTitle("CropShot");
			stage.setScene(scene);
			stage.show();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
