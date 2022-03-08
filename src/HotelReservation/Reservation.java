package HotelReservation;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class Reservation extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
        primaryStage.setTitle("Hotel Reservation");
        primaryStage.setScene(new Scene(root, 1024, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
