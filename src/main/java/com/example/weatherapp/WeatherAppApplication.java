package com.example.weatherapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class WeatherAppApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        applicationContext = SpringApplication.run(WeatherAppApplication.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainform.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Parent parent = loader.load();

        stage.setTitle("Weather Application");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/icons/icon.png"));
        stage.setScene(new Scene(parent, 400, 600));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        applicationContext.close();
    }
}
