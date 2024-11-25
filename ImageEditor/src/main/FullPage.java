package main;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FullPage {
	Stage stage;
	Scene scene;
	
	Label EditedLbl, beforeLbl, afterLbl;
	GridPane gp;
	Button backBtn;
	VBox RootBox;
//	ImageView beforeImg, afterImg;
	
	public void styling() {
		
	}
	
	public void init() {
		gp = new GridPane();
		EditedLbl = new Label("Original and Transformed Image");
		beforeLbl = new Label("Original");
		afterLbl = new Label("Transformed");
		backBtn = new Button("Return");
		
		RootBox = new VBox();
			
		gp.add(beforeLbl, 0, 0);
		gp.add(afterLbl, 1, 0);
//		gp.add(beforeImg, 0, 1);
//		gp.add(afterImg, 1, 1);
		
		RootBox.getChildren().addAll(EditedLbl, gp, backBtn);
		RootBox.setAlignment(Pos.CENTER);
	}
	
	public FullPage(Stage stage) {
		this.stage = stage;
		init();
		styling();
		stage.setTitle("Image Editing App");
		stage.setScene(scene);
		stage.show();
	}
}
