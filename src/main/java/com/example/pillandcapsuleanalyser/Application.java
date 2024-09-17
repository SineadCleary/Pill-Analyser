package com.example.pillandcapsuleanalyser;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setTitle("Pill and Capsule Analyser");
        stage.setScene(scene);
        stage.show();
        primaryStage = stage;
    }

    public static void main(String[] args) {
        launch();
    }

    public static FXMLLoader openView(String view, String title, boolean newWin) throws Exception {
        Stage stage;
        if (newWin) stage = new Stage();
        else stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(view));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        return fxmlLoader;
    }
}