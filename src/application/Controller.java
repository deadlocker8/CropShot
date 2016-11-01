package application;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.imageio.ImageIO;

import tools.Worker;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;

public class Controller implements Initializable
{
	@FXML private Label labelWidth;
	@FXML private Label labelHeight;
	@FXML private Label labelX;
	@FXML private Label labelY;
	@FXML private Label labelSavePath;
	@FXML public Button chooseButton;
	@FXML public Button cropButton;
	@FXML public Button selectButton;
	@FXML private Pane pane;
	@FXML private AnchorPane anchorPaneMain;
	@FXML public Pane previewPane;
	@FXML private Label previewLabel;
	@FXML private Label label;
	@FXML private Label preview;
	@FXML private ProgressBar progressBar;
	@FXML private Label progressLabel;
	@FXML private ImageView imageView;
	@FXML private HBox hbox;

	public Button savePathButton;
	public Button cancelButton;
	private Cropper cropper;
	public Stage stage;
	public ArrayList<File> files;
	public ArrayList<Boolean> correct;
	public String path;
	public ListView<File> list;
	public boolean choosen;
	public String[] settings;
	private File lastFolder;
	public static ResourceBundle bundle = ResourceBundle.getBundle("application/", Locale.GERMANY);

	public void setStage(Stage s)
	{
		stage = s;
	}

	@FXML
	public void chooseButton(ActionEvent e)
	{
		FileChooser chooser = new FileChooser();	
		File prefDirectory = new File(System.getProperty("user.home") + "/Pictures/Screenshots/");		
		if(lastFolder != null)
		{
			chooser.setInitialDirectory(lastFolder);
		}
		else
		{
			chooser.setInitialDirectory(prefDirectory);
		}
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
		chooser.setTitle("Dateien auswählen");

		List<File> choosenFiles = chooser.showOpenMultipleDialog(stage);
		if(choosenFiles != null)
		{
			ArrayList<File> loaded = new ArrayList<File>(choosenFiles);
			for(int i = 0; i < loaded.size(); i++)
			{
				files.add(loaded.get(i));
			}
			lastFolder = loaded.get(0).getParentFile();
		}

		setLabel();
		setList(true);

		choosen = true;
	}

	@FXML
	public void cropButton(ActionEvent e)
	{
		if(choosen)
		{
			if(path != null)
			{
				cancelButton.setDisable(false);
				cropButton.setDisable(true);
				chooseButton.setDisable(true);
				selectButton.setDisable(true);
				savePathButton.setDisable(true);
				list.setDisable(true);
				saveSettings();

				cropper = new Cropper();
				Cropper.running = true;
				Cropper.controller = this;
				cropper.start();
			}
			else
			{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warnung");
				alert.setHeaderText("");
				alert.setContentText("Kein Speicherort ausgewählt!");
				alert.initOwner(stage);
				Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
				dialogStage.getIcons().add(new Image(this.getClass().getResource("/application/crop.png").toString()));
				alert.showAndWait();
			}
		}
	}

	public void setLabel()
	{
		label.setText(files.size() + " Dateien ausgewählt");
	}

	public void setLabelZero()
	{
		label.setText("0 Dateien ausgewählt");
	}

	public void clearList()
	{
		list.getItems().clear();
		list.setPlaceholder(new Label(""));
	}

	public void setList(boolean checkDimensions)
	{
		Worker.runLater(() -> {
			Platform.runLater(() -> {
				cropButton.setDisable(true);
			});
		});
		list.getItems().clear();

		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setMaxHeight(50.0);
		indicator.setMaxWidth(50.0);
		list.setPlaceholder(indicator);

		if(files.size() == 0)
		{
			Label placeHolder = new Label("Keine Dateien ausgewählt");
			placeHolder.setStyle("-fx-font-size: 16;");
			list.setPlaceholder(placeHolder);
		}
		else
		{
			Worker.runLater(() -> {

				if(checkDimensions)
				{
					Platform.runLater(() -> {
						correct.clear();
					});
				}

				for(int i = 0; i < files.size(); i++)
				{
					correct.add(Cropper.checkDimensionsShort(files.get(i)));
				}

				Platform.runLater(() -> {
					list.getItems().addAll(files);
					list.getSelectionModel().select(0);
					list.requestFocus();
					cropButton.setDisable(false);
				});
			});
		}
	}

	public void setProgressBar(double wert)
	{
		progressBar.setProgress(wert);
	}

	public void setProgressLabel(String text)
	{
		progressLabel.setText(text);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		CropShotCell.controller = this;

		loadSettings();

		pane.setStyle("-fx-background-color: #FFFFFF;");
		label.setText("0 Dateien ausgewählt");
		progressLabel.setText("");

		Image icon = new Image("/application/crop.png");
		imageView.setImage(icon);
		choosen = false;

		previewLabel.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 2px;");

		FontIcon iconChooseSavePath = new FontIcon(FontIconType.FOLDER);
		iconChooseSavePath.setSize(15);
		iconChooseSavePath.setColor(Color.web("#333333"));

		savePathButton = new Button("", iconChooseSavePath);
		savePathButton.setPrefHeight(15);
		savePathButton.setPrefWidth(15);

		savePathButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				final DirectoryChooser directoryChooser = new DirectoryChooser();
				try
				{
					File start = new File(path);
					if(start.exists())
					{
						directoryChooser.setInitialDirectory(start);
					}
				}
				catch(Exception e)
				{

				}

				final File selectedDirectory = directoryChooser.showDialog(stage);

				if(selectedDirectory != null)
				{
					path = selectedDirectory.getAbsolutePath();
					saveSettings();
					labelSavePath.setText(path);
				}
			}
		});

		anchorPaneMain.getChildren().add(savePathButton);

		AnchorPane.setLeftAnchor(savePathButton, 380.0);
		AnchorPane.setTopAnchor(savePathButton, 185.0);

		FontIcon iconCancel = new FontIcon(FontIconType.TIMES);
		iconCancel.setSize(15);
		iconCancel.setColor(Color.web("#CC0000"));

		cancelButton = new Button("", iconCancel);
		cancelButton.setPrefHeight(15);
		cancelButton.setPrefWidth(15);

		cancelButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				if(Cropper.running)
				{
					cropper.interrupt();
					Cropper.running = false;
				}
			}
		});

		anchorPaneMain.getChildren().add(cancelButton);

		AnchorPane.setLeftAnchor(cancelButton, 312.0);
		AnchorPane.setBottomAnchor(cancelButton, 39.0);

		list = new ListView<File>();

		list.setCellFactory(new Callback<ListView<File>, ListCell<File>>()
		{
			@Override
			public ListCell<File> call(ListView<File> arg0)
			{
				return new CropShotCell();
			}
		});

		files = new ArrayList<File>();
		correct = new ArrayList<Boolean>();

		list.setPrefWidth(413);
		list.setPrefHeight(308);
		pane.getChildren().add(list);
		setPreviewListener();

		cropButton.setDisable(true);
		cancelButton.setDisable(true);

		pane.setOnDragOver(new EventHandler<DragEvent>()
		{
			@Override
			public void handle(DragEvent event)
			{
				if(Cropper.running == false)
				{
					if(event.getGestureSource() != pane && event.getDragboard().hasFiles())
					{
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
					event.consume();
				}
			}
		});

		pane.setOnDragDropped(new EventHandler<DragEvent>()
		{
			@Override
			public void handle(DragEvent event)
			{
				if(Cropper.running == false)
				{
					Dragboard board = event.getDragboard();
					boolean success = false;

					if(board.hasFiles())
					{
						success = true;
						List<File> dropped = board.getFiles();

						for(int i = 0; i < dropped.size(); i++)
						{
							if(dropped.get(i).toURI().toString().endsWith(".jpg") || dropped.get(i).toURI().toString().endsWith(".png"))
							{
								files.add(dropped.get(i));
							}
						}

						setLabel();
						setList(true);
						choosen = true;
					}
					event.setDropCompleted(success);
					event.consume();
				}
			}
		});

		Tooltip tooltip = new Tooltip();
		Tooltip.install(labelSavePath, tooltip);	
		
		tooltip.setOnShowing(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event) 
			{
				tooltip.setText(path);
			};
		});		
	}

	public void setPreviewListener()
	{
		list.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if(choosen)
				{
					int index = list.getSelectionModel().getSelectedIndex();
					File file = files.get(index);

					int[] result = Cropper.checkDimensions(file);
					if(result[0] == 0)
					{
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warnung");
						alert.setHeaderText("");
						alert.getDialogPane().setPrefSize(350, 150);
						alert.setContentText("Bildausschnitt liegt außerhalb des Bildes! \n\nX + Breite > Bildbreite \n" + Cropper.x + " + " + Cropper.width + " > " + result[1]);
						alert.initOwner(stage);
						alert.showAndWait();
					}
					else if(result[0] == 1)
					{
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warnung");
						alert.setHeaderText("");
						alert.getDialogPane().setPrefSize(350, 150);
						alert.setContentText("Bildausschnitt liegt außerhalb des Bildes! \n\nY + Höhe > Bildhöhe \n" + Cropper.y + " + " + Cropper.height + " > " + result[1]);
						alert.initOwner(stage);
						alert.showAndWait();
					}
					else
					{
						try
						{
							previewPane.getChildren().remove(previewLabel);

							BufferedImage image = ImageIO.read(file);
							BufferedImage img = image.getSubimage(Cropper.x, Cropper.y, Cropper.width, Cropper.height);

							BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
							Graphics g = copyOfImage.createGraphics();
							g.drawImage(img, 0, 0, null);

							Image previewImage = SwingFXUtils.toFXImage(copyOfImage, null);
							ImageView view = new ImageView(previewImage);
							view.fitWidthProperty().bind(previewPane.widthProperty());
							view.fitHeightProperty().bind(previewPane.heightProperty());

							previewLabel = new Label("", view);
							previewLabel.setStyle("-fx-border-color: #000000; -fx-border-width: 2px;");
							setPreviewListener();
							previewPane.getChildren().add(previewLabel);

						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public void clearPreview()
	{
		previewPane.getChildren().remove(previewLabel);
		previewLabel = new Label("Bild in Liste anklicken für Vorschau");
		previewLabel.setPrefWidth(250);
		previewLabel.setPrefHeight(190);
		previewLabel.setAlignment(Pos.CENTER);
		previewLabel.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-width: 2px; -fx-font-size: 15px; -fx-font-family: \"System\";");
		previewPane.getChildren().add(previewLabel);
	}

	public void buttonSelect()
	{
		int selectedIndex = list.getSelectionModel().getSelectedIndex();
		if(selectedIndex != - 1)
		{
			File selected = list.getSelectionModel().getSelectedItem();

			try
			{
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SelectGUI.fxml"));

				Parent root = (Parent)fxmlLoader.load();
				Stage newStage = new Stage();
				newStage.setScene(new Scene(root, 700, 500));
				newStage.setMinHeight(250);
				newStage.setMinWidth(250);

				newStage.setTitle("Bereich auswählen");

				newStage.getIcons().add(new Image("/application/crop.png"));

				newStage.setResizable(true);

				SelectController newController = (SelectController)fxmlLoader.getController();
				newController.setStage(newStage);
				newController.setController(this);
				newController.setImage(selected);
				newStage.initModality(Modality.APPLICATION_MODAL);
				newController.init();

				newStage.show();
			}
			catch(IOException e1)
			{
				e1.printStackTrace();
			}
		}
		else
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warnung");
			alert.setHeaderText("");
			alert.setContentText("Kein Bild ausgewählt.\nBitte wähle ein Bild aus der Liste.");
			alert.initOwner(stage);
			Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(new Image(this.getClass().getResource("/application/crop.png").toString()));
			alert.showAndWait();
		}
	}

	public void loadSettings()
	{
		settings = ReadFile.readSettings();
		if(settings != null)
		{
			labelX.setText(settings[0]);
			labelY.setText(settings[1]);
			labelWidth.setText(settings[2]);
			labelHeight.setText(settings[3]);

			Cropper.setValues(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]), Integer.parseInt(settings[2]), Integer.parseInt(settings[3]));
			
			if(!settings[4].equals("null"))
			{						
				path = settings[4];	
				File pathTest = new File(path);
				if(pathTest.exists())
				{
					labelSavePath.setText(path);
				}
				else
				{
					labelSavePath.setText("<Nicht ausgewählt>");
				}
			}	
			else
			{
				labelSavePath.setText("<Nicht ausgewählt>");
			}
		}
		else
		{
			labelX.setText("0");
			labelY.setText("0");
			labelWidth.setText("10");
			labelHeight.setText("10");

			Cropper.setValues(0, 0, 10, 10);
			labelSavePath.setText("<Nicht ausgewählt>");
		}
	}

	public void saveSettings()
	{
		if(settings != null)
		{
			settings[0] = "" + Cropper.x;
			settings[1] = "" + Cropper.y;
			settings[2] = "" + Cropper.width;
			settings[3] = "" + Cropper.height;
			settings[4] = path;
			SaveFile.saveSettings(settings);
		}
	}

	public void updateLabels()
	{
		labelX.setText(String.valueOf(Cropper.x));
		labelY.setText(String.valueOf(Cropper.y));
		labelWidth.setText(String.valueOf(Cropper.width));
		labelHeight.setText(String.valueOf(Cropper.height));
	}

	public void about(ActionEvent e)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Über " + bundle.getString("app.name"));
		alert.setHeaderText(bundle.getString("app.name"));
		alert.setContentText("Version:     " + bundle.getString("version.name") + "\r\nDatum:      " + bundle.getString("version.date") + "\r\nAutor:        Robert Goldmann\r\n");
		alert.initOwner(stage);
		Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(new Image("application/crop.png"));
		alert.showAndWait();
	}

	@FXML
	public void openButton()
	{
		try
		{
			Desktop.getDesktop().open(new File(path.replace("\\", "/") + "/CropShot/"));
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
}