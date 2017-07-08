package databasesproject.Controllers;


import databasesproject.ConnectDataBase;
import databasesproject.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private Server server;

    @FXML
    private Button dmlBtn;

    @FXML
    private Button ddlBtn;

    /**
     * Sets the server.
     *
     * @param server
     */
    public void setServer(Server server) {

        this.server = server;
    }

    /**
     * Interface method.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Opens the requested page.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    public void openPage(ActionEvent event) throws IOException {

        String                 path;
        Stage                  stage;
        FXMLLoader             fxmlLoader;
        Parent                 root;
        QueryHandlerController controller;

        //Check which button was clicked.
        if (event.getSource() == dmlBtn) {

            path = "../Fxml/DmlPage.fxml";
        } else if (event.getSource() == ddlBtn) {

            path = "../Fxml/DdlPage.fxml";
        } else {

            path = "";
        }

        fxmlLoader = new FXMLLoader(getClass().getResource(path));
        stage = (Stage) dmlBtn.getScene().getWindow();
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        //Set server.
        controller.setServer(this.server);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

        //Hide current scene.
        //dmlBtn.getScene().getWindow().hide();

/*        Stage  stage;
        Parent root;

        //get reference to the button's stage
        stage = (Stage) dmlBtn.getScene().getWindow();

        //load up OTHER FXML document
        root = FXMLLoader.load(getClass().getResource("/databasesproject/DBGUI.fxml"));

        //Hide current scene.
        dmlBtn.getScene().getWindow().hide();

        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();*/
    }


}
