package com.xyy.bill.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebKit extends Application {
	public static void  main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
			  
			          
	        WebView webView=new WebView(); 
	 
	        WebEngine engine=  webView.getEngine();  
	        engine.setJavaScriptEnabled(true);
	      
	        
	        engine.load("http://127.0.0.1:88");  
	        stage.setTitle("XYY ERP");
	        stage.setAlwaysOnTop(true);
	       
	  
	        stage.setScene(new Scene(webView));
	        stage.show();
	      
	}

}
