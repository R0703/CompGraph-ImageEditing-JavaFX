package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
//	private ImageView imageView = new ImageView();
	private BufferedImage bufferedImage;
	
	
	public UploadPage(Stage stage) {
		this.stage = stage;
		
        Button openButton = new Button("Open Image");
        openButton.setOnAction(e -> openImage(stage));

        ComboBox<String> filterOptions = new ComboBox<>();
        filterOptions.getItems().addAll("Grayscale", "Blur");
        filterOptions.setValue("Grayscale");

        Button applyButton = new Button("Apply Filter");
        applyButton.setOnAction(e -> applyFilter(filterOptions.getValue()));
        
        //display before after img dikasih filter
        beforeIV = new ImageView();
        afterIV = new ImageView();
        setFixedImageSize(beforeIV);
        setFixedImageSize(afterIV);
        hb1 = new HBox(10, beforeIV, afterIV);
        hb1.setStyle("-fx-alignment: center;");
//        setFixedImageSize(imageView);

        // Layout
        VBox root = new VBox(10, openButton, filterOptions, applyButton, hb1);
        root.setStyle("-fx-padding: 10; -fx-alignment: center;");
        
		stage.setTitle("Image Editing App");
		scene = new Scene(root, 600, 600);
		stage.setScene(scene);
		stage.show();
	}
	
	private void openImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);

               //display before dikasih filter
                beforeIV.setImage(image);
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

        //display after dikasih filter
        afterIV.setImage(fxImage);
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
