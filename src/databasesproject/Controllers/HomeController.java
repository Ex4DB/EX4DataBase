/**
 * Daniel Marzayev 318687134 89-281-02
 * Danny Perov 318810637 89-281-02
 */
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

    @FXML
    private Button simpleQueryBtn;

    /**
     * Sets the server.
     *
     * @param server server.
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

            path = "../Fxml/SimpleQueryPage.fxml";
        }

        //Load resource.
        fxmlLoader = new FXMLLoader(getClass().getResource(path));
        stage = (Stage) dmlBtn.getScene().getWindow();
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        //Set server.
        controller.setServer(this.server);
        Scene scene = new Scene(root);

        //Set scene.
        stage.setScene(scene);
        stage.show();


        if(event.getSource() == simpleQueryBtn){

            controller.showTables();
        }

    }


}
