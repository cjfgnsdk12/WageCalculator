package controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import view.ViewerController;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private  SplitPane splitpane;
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage=primaryStage;
		this.primaryStage.setTitle("WageApp");
		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader= new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/view/Viewer.fxml"));
			splitpane=(SplitPane)loader.load();
			
			Scene scene =new Scene(splitpane);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			ViewerController controller=loader.getController();
			//스테이지 닫힐때 이벤트
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		              controller.saveConfig();
		          }
			 });        
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
