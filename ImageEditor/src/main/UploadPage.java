package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadPage {
	Stage stage;
	Scene scene;
	HBox hb1;
	ImageView beforeIV;
	ImageView afterIV;

	private BufferedImage bufferedImage;
	Button newScreenBtn;
	
	
	public UploadPage(Stage stage) {
		this.stage = stage;
		
		Label titleLbl = new Label("Upload and Transform Your Image");
		titleLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		
		//======OPEN IMAGE======
		Label chooseImageLabel = new Label("Choose an image:");
		chooseImageLabel.setAlignment(Pos.CENTER_LEFT);
	    TextField filePathField = new TextField("No file selected.");
	    filePathField.setEditable(false);
        Button openButton = new Button("Browse...");
        openButton.setOnAction(e -> openImage(stage, filePathField, beforeIV, afterIV));
        
        HBox fileChooserBox = new HBox(10, openButton, filePathField);
        fileChooserBox.setAlignment(Pos.CENTER_LEFT);
        
        //======CHOOSE FILTER======
        Label chooseOptionLabel = new Label("Choose an option:");
        ComboBox<String> filterOptions = new ComboBox<>();
        filterOptions.getItems().addAll("Grayscale", "Blur");
        filterOptions.setValue("Grayscale");
        VBox optionsBox = new VBox(10, chooseOptionLabel, filterOptions);
        optionsBox.setAlignment(Pos.CENTER_LEFT);

        Button applyButton = new Button("Convert Image");
        applyButton.setOnAction(e -> applyFilter(filterOptions.getValue()));
        applyButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        //======DISPLAY BEFORE AFTER======
        beforeIV = new ImageView();
        afterIV = new ImageView();
        setFixedImageSize(beforeIV);
        setFixedImageSize(afterIV);
        hb1 = new HBox(10, beforeIV, afterIV);
        hb1.setStyle("-fx-alignment: center;");
        
        //======CONTAINER======
        VBox formBox = new VBox(15, chooseImageLabel, fileChooserBox, chooseOptionLabel, filterOptions, applyButton);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-radius: 10;");
        formBox.setAlignment(Pos.CENTER);
        formBox.setPrefWidth(550);
        formBox.setMaxWidth(550);

        // Root Layout
        newScreenBtn = new Button("Open in new Window");
        VBox root = new VBox(20, titleLbl, formBox, hb1, newScreenBtn);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-padding: 10; -fx-alignment: center;");
        
        newScreenBtn.setOnAction(e->{
        	new FullPage(stage);
        });
        
		stage.setTitle("Image Editing App");
		scene = new Scene(root, 600, 600);
		stage.setScene(scene);
		stage.show();
	}
	
	private void openImage(Stage stage, TextField filePathField, ImageView beforeIV, ImageView afterIV) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Update the file path in the TextField
                filePathField.setText(selectedFile.getName());

                // Read and store the image in the class-level bufferedImage
                bufferedImage = ImageIO.read(selectedFile);

                // Convert the bufferedImage to an FX image and display it in beforeIV
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                beforeIV.setImage(image);
                beforeIV.setFitWidth(300); // Set fixed width
                beforeIV.setPreserveRatio(true);

                // Clear the afterIV for now
                afterIV.setImage(null);
            } catch (IOException ex) {
                showError("Error loading image: " + ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void applyFilter(String filter) {
        if (bufferedImage == null) {
            showError("Please load an image first.");
            return;
        }

        BufferedImage newImage;
        switch (filter) {
            case "Grayscale":
                newImage = applyGrayscale(bufferedImage);
                break;
            case "Blur":
                newImage = applyBlur(bufferedImage);
                break;
            default:
                showError("Unknown filter: " + filter);
                return;
        }

        Image fxImage = SwingFXUtils.toFXImage(newImage, null);
        afterIV.setImage(fxImage);

        //display after dikasih filter
        bufferedImage = newImage;
    }

    private BufferedImage applyGrayscale(BufferedImage image) {
        BufferedImage grayscaleImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int a = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int gray = (r + g + b) / 3;
                int newRgb = (a << 24) | (gray << 16) | (gray << 8) | gray;
                grayscaleImage.setRGB(x, y, newRgb);
            }
        }
        return grayscaleImage;
    }

    private BufferedImage applyBlur(BufferedImage image) {
        BufferedImage blurredImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int[] dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        int[] dy = {-1, -1, -1, 0, 0, 0, 1, 1, 1};

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int a = 0, r = 0, g = 0, b = 0, count = 0;

                for (int i = 0; i < dx.length; i++) {
                    int nx = x + dx[i];
                    int ny = y + dy[i];

                    if (nx >= 0 && ny >= 0 && nx < image.getWidth() && ny < image.getHeight()) {
                        int rgb = image.getRGB(nx, ny);
                        a += (rgb >> 24) & 0xff;
                        r += (rgb >> 16) & 0xff;
                        g += (rgb >> 8) & 0xff;
                        b += rgb & 0xff;
                        count++;
                    }
                }

                a /= count;
                r /= count;
                g /= count;
                b /= count;
                int newRgb = (a << 24) | (r << 16) | (g << 8) | b;
                blurredImage.setRGB(x, y, newRgb);
            }
        }
        return blurredImage;
    }

    private void setFixedImageSize(ImageView imageView) {
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }
	
}
