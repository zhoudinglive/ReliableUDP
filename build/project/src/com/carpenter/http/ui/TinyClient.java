package com.carpenter.http.ui;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TinyClient extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		try {
			//BorderPane root = new BorderPane();
			Parent root = FXMLLoader.load(getClass().getResource("TinyClientDemo.fxml"));
			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
//		Scene scene = new Scene(root);
//		primaryStage.setScene(scene);
//		primaryStage.setResizable(false);
//		primaryStage.setTitle("RDT_Base_HTTP_Demo");
//		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
