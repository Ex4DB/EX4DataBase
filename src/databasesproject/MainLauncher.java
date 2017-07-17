/**
 * Daniel Marzayev 318687134 89-281-02
 * Danny Perov 318810637 89-281-02
 */
package databasesproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class to launch the GUI.
 */
public class MainLauncher extends Application {

    /**
     * Launches the GUI.
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader     fxmlLoader;
        Parent         root;
        HomeController controller;
        Server         server;

        //Initialize server.
        server = new ConnectDataBase();

        fxmlLoader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        //Set server.
        controller.setServer(server);
        Scene scene = new Scene(root);

        stage.setTitle("Database Project 2017");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
