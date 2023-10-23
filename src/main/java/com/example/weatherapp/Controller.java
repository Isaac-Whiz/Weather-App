package com.example.weatherapp;

import com.example.weatherapp.responses.LocationInfo;
import com.example.weatherapp.responses.WeatherApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@org.springframework.stereotype.Controller
public class Controller {

    public Pane panel;
    public Button btnSearch;
    public TextField txtInputCity;
    public Label txtLabelTime;
    public Label txtLabelTemperature;
    public Label txtLabelWindSpeed;
    public Label txtLabelDirection;
    public Label txtLabelFeelsLike;
    public ImageView imageViewCurrent;
    public Label txtLabelCondition;
    public Label txtLabelDegrees;
    public Label txtLabelDegreesBig;
    public Label txtLabelLastUpdated;
    public Label txtLabelCity;
    public Label txtLabelCountry;
    public Label txtLabelVisibility;
    public Label txtLabelHumidity;
    public Label txtLabelPre;
    public Label txtLabelPressure;
    public Label txtLabelIsDay;
    public Label txtLabelCloud;
    private String city;
    private WeatherApiResponse weatherApiResponse;

    @FXML
    public void initialize() throws IOException, InterruptedException {
        panel.setBackground(new Background(new BackgroundImage(
                new Image("icons/bg_image.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        findLocation();
        weatherApiResponse = getWeatherCondition();
        populateViews();
        handleEvents();
    }
    private WeatherApiResponse getWeatherCondition() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://weatherapi-com.p.rapidapi.com/current.json?q=" + city))
                .header("X-RapidAPI-Key", "e7dbe03879mshdffd80f8b508a87p112a67jsnffe511b215dd")
                .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body(), WeatherApiResponse.class);
        } else {
            txtInputCity.setText(null);
            showErrorAlert( "Please enter a valid city name.");
            return null;
        }
    }

    private boolean isInternetAvailable() throws IOException {
        InetAddress address = InetAddress.getByName("8.8.8.8");
        return address.isReachable(3000);
    }

    private void showErrorAlert( String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Weather App");
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Weather App");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void findLocation() {
        try {
            String apiUrl = "http://ip-api.com/json";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl))
                    .method("GET", HttpRequest.BodyPublishers.noBody()).build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            LocationInfo locationInfo = mapper.readValue(response.body(), LocationInfo.class);

            city = locationInfo.getCity().toLowerCase();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void populateViews() throws IOException {

        var tempC = weatherApiResponse.getCurrent().getTempC();
        var condition = weatherApiResponse.getCurrent().getCondition().getText();
        var currentTime = weatherApiResponse.getLocation().getLocaltime();
        var windDirection = weatherApiResponse.getCurrent().getWindDir();
        var windSpeed = weatherApiResponse.getCurrent().getWindMph();
        var feelsLike = weatherApiResponse.getCurrent().getFeelslikeC();
        var imageUrl = weatherApiResponse.getCurrent().getCondition().getIcon();
        var country = weatherApiResponse.getLocation().getCountry();
        var city = weatherApiResponse.getLocation().getName();
        var lastUpdated = weatherApiResponse.getCurrent().getLastUpdated();
        var degrees = "0";
        var visibility = weatherApiResponse.getCurrent().getVisMiles();
        var humidity = weatherApiResponse.getCurrent().getHumidity();
        var pre = weatherApiResponse.getCurrent().getPrecipIn();
        var pressure = weatherApiResponse.getCurrent().getPressureMb();
        var isDay = weatherApiResponse.getCurrent().getIsDay();
        var cloud = weatherApiResponse.getCurrent().getCloud();

        //Convert image path to url
        URL url = new URL("https:" + imageUrl);
        BufferedImage bufferedImage = ImageIO.read(url);

        imageViewCurrent.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        txtLabelTime.setText(currentTime);
        txtLabelTemperature.setText(String.valueOf(tempC));
        txtLabelCondition.setText(condition);
        txtLabelDirection.setText(windDirection);
        txtLabelWindSpeed.setText(String.valueOf(windSpeed));
        txtLabelFeelsLike.setText(String.valueOf(feelsLike));
        txtLabelDegrees.setText(degrees);
        txtLabelDegreesBig.setText(degrees);
        txtLabelCountry.setText(country);
        txtLabelCity.setText(city);
        txtLabelLastUpdated.setText(lastUpdated);
        txtLabelCloud.setText(cloud + "%");
        txtLabelIsDay.setText(String.valueOf(isDay));
        txtLabelPre.setText(pre + "mm");
        txtLabelPressure.setText(pressure + "Pa");
        txtLabelHumidity.setText(humidity + "HR");
        txtLabelVisibility.setText(visibility + "miles");
        txtInputCity.setText(null);
    }

    private void handleEvents() {
        btnSearch.setOnAction(actionEvent -> {
            try {
                if (isInternetAvailable()) {
                        city = txtInputCity.getText().toLowerCase();
                        weatherApiResponse = getWeatherCondition();
                        populateViews();
                } else {
                    showInfoAlert("Enable internet to search city.");
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
