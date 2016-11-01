package application;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;

public class CropShotCell extends ListCell<File>
{
	public static Controller controller;

	@Override
	protected void updateItem(File item, boolean empty)
	{
		super.updateItem(item, empty);	
		if(!empty)
		{
			Label label = new Label(item.getName());
			label.setPrefWidth(320);

		
			Label warn;			
			if(!controller.correct.get(this.getIndex()))
			{
				FontIcon iconWarn = new FontIcon(FontIconType.WARNING);
				iconWarn.setSize(15);
				iconWarn.setColor(Color.web("#CC0000"));				
				warn = new Label("", iconWarn);				
			}
			else
			{
				FontIcon iconCorrect = new FontIcon(FontIconType.CHECK);
				iconCorrect.setSize(15);
				iconCorrect.setColor(Color.web("#247A2D"));				
				warn = new Label("", iconCorrect);			
			}
			
			FontIcon icon = new FontIcon(FontIconType.TIMES);
			icon.setSize(15);
			icon.setColor(Color.web("#CC0000"));

			Button deleteButton = new Button("", icon);
			deleteButton.setPrefHeight(15);
			deleteButton.setPrefWidth(15);
			deleteButton.setStyle("-fx-padding: 0;");		
			
			deleteButton.setOnAction(new EventHandler<ActionEvent>() 
			{
			    @Override public void handle(ActionEvent e)
			    {
			    	controller.files.remove(item);
			    	controller.setList(true);
			    }
			});

			HBox box = new HBox();
			box.getChildren().add(label);
			box.getChildren().add(warn);
			box.getChildren().add(deleteButton);
			HBox.setHgrow(label, Priority.ALWAYS);
			HBox.setMargin(warn, new Insets(0,0,0,25));
			HBox.setMargin(deleteButton, new Insets(0,0,0,10));
			
			setGraphic(box);			
		}
		else
		{			
			setGraphic(null);			
		}
	}
}