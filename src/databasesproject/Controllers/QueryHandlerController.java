/**
 * Daniel Marzayev 318687134 89-281-02
 * Danny Perov 318810637 89-281-02
 */
package databasesproject.Controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import databasesproject.ScriptRunner;
import databasesproject.Server;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class QueryHandlerController implements Initializable {
    private Server server;
    private int    vBoxNumber;

    public enum Type {DML, DDL, TABLES, VALUES, SIMPLE}

    ;
    private File userFilesPath = null;

    @FXML
    private TextArea       textAreaQueryDML;
    @FXML
    private TextArea       textAreaQueryDDL;
    @FXML
    private TextArea       textAreaResponseDML;
    @FXML
    private TextArea       textAreaResponseDDL;
    @FXML
    private HBox           boxTables;
    @FXML
    private TextArea       textAreaSimpleQuery;
    @FXML
    private TextArea       textAreaResponseSimpleQuery;
    @FXML
    private Button         dmlBtn;
    private List<VBox>     vBoxes;
    private List<String>   tables;
    private List<CheckBox> checkBoxes;

    /**
     * Constructor.
     */
    public QueryHandlerController() {
        super();
    }

    /**
     * Sets the server.
     *
     * @param server server.
     */
    public void setServer(Server server) {

        this.server = server;
    }

    /**
     * interface method.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Send to server request for tables names.
     *
     * @param event
     */
    @FXML
    public void showTables(ActionEvent event) {
        String query = "show tables;";
        responseThread(query, null, Type.TABLES, 0);
    }

    /**
     * Send to server request for tables values.
     *
     * @param tableName
     * @param tableNumber
     */
    public void describeTable(String tableName, int tableNumber) {
        String query = "DESC " + tableName + ";";
        tables.add(tableName);
        responseThread(query, null, Type.VALUES, tableNumber);
    }

    /**
     * Send to server simple query.
     *
     * @param event
     */
    @FXML
    public void sendSimpleQuery(ActionEvent event) {
        String select = "SELECT ";
        for (CheckBox value : checkBoxes) {
            if (value.isSelected())
                select += value.getText() + " ";
        }
        select += "FROM ";
        for (String table : tables) {
            select += table + " ";
        }
        String where = "WHERE " + this.textAreaSimpleQuery.getText() + ";";
        String query = select + where;
        //TODO build query and sent to server
        if (!"".equals(query)) {
            this.textAreaQueryDML.setText("");
            responseThread(query, this.textAreaResponseSimpleQuery, Type
                    .SIMPLE, 0);
        }
    }

    /**
     * submits DML query.
     *
     * @param event
     */
    @FXML
    private void submitQueryDML(ActionEvent event) {
        String query = this.textAreaQueryDML.getText();

        if (!"".equals(query)) {
            this.textAreaQueryDML.setText("");
            responseThread(query, this.textAreaResponseDML, Type.DML, 0);
        }
    }

    /**
     * submit DDL query.
     *
     * @param event
     */
    @FXML
    private void submitQueryDDL(ActionEvent event) {
        String query = this.textAreaQueryDDL.getText();

        if (!"".equals(query)) {
            this.textAreaQueryDDL.setText("");
            responseThread(query, this.textAreaResponseDDL, Type.DDL, 0);
        }
    }

    /**
     * runs DML script.
     *
     * @param event
     */
    @FXML
    private void runScriptDML(ActionEvent event) {
        ScriptRunner sr       = new ScriptRunner(this.server);
        String       filePath = this.locateFile("Select DML script", "dml");
        String       output   = sr.DMLScript(filePath);

        this.textAreaQueryDML.clear();
        this.textAreaResponseDML.clear();
        this.textAreaResponseDML.appendText(output);
    }

    /**
     * runs DDL script.
     *
     * @param event
     */
    @FXML
    private void runScriptDDL(ActionEvent event) {
        ScriptRunner sr       = new ScriptRunner(this.server);
        String       filePath = this.locateFile("Select DDL script", "ddl");
        String       output   = sr.DDLScript(filePath);

        this.textAreaQueryDDL.clear();
        this.textAreaResponseDDL.clear();
        this.textAreaResponseDDL.appendText(output);
    }

    /**
     * opens file browser window to select script file.
     *
     * @param title window title
     * @return
     */
    private String locateFile(String title, String requestType) {

        File        file;
        FileChooser chooser = new FileChooser();

        chooser.setTitle(title);

        if (this.userFilesPath != null) {
            chooser.setInitialDirectory(this.userFilesPath);
        }

        //Choose request type.
        if (requestType == "dml") {

            file = chooser.showOpenDialog(
                    this.textAreaQueryDML.getScene().getWindow());
        } else {

            file = chooser.showOpenDialog(
                    this.textAreaQueryDDL.getScene().getWindow());
        }

        this.userFilesPath = file.getParentFile();

        return file.getPath();
    }

    /**
     * Create for each table a button.
     *
     * @param tables - the tables from the server.
     */
    private void createTablesBoxes(String tables) {
        vBoxes = new LinkedList<>();
        boxTables = new HBox();
        if (tables == null) {
            System.out.println("Daniel Hermon The King!!!\n");
        }
        String array[] = tables.split(",");
        vBoxNumber = 0;

        for (String table : array) {
            Button button = new Button();
            button.setText(table);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    describeTable(table, vBoxNumber);
                }
            });
            VBox vBox = new VBox(button);
            vBoxes.add(vBox);
            boxTables.getChildren().add(vBox);
            boxTables.setAlignment(Pos.BASELINE_LEFT);
            vBoxNumber++;
        }
    }

    /**
     * Create for each value check box.
     *
     * @param values      - values from the select table.
     * @param tableNumber - table number.
     */
    private void createCheckBoxes(String values, int tableNumber) {
        String array[] = values.split(",");
        for (String value : array) {
            CheckBox checkBox = new CheckBox(value);
            checkBoxes.add(checkBox);
            vBoxes.get(tableNumber).getChildren().add(checkBox);
        }
    }

    @FXML
    public void backToHomePage(ActionEvent event) throws IOException {

        Stage  stage;
        Parent root;
        FXMLLoader fxmlLoader;
        String path;
        HomeController controller;

        path = "../Fxml/HomePage.fxml";

        //get reference to the button's stage
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();

        //load up OTHER FXML document
        fxmlLoader = new FXMLLoader(getClass().getResource(path));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setServer(this.server);

        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

       /* String                 path;
        Stage                  stage;
        FXMLLoader             fxmlLoader;
        Parent                 root;

        path = "../Fxml/HomePage.fxml";

        fxmlLoader = new FXMLLoader(getClass().getResource(path));
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        root = fxmlLoader.load();

        Scene scene = new Scene(root);

        //Set scene.
        stage.setScene(scene);
        stage.show();*/
    }

    /**
     * Sends request to server and appends response in a separate thread.
     *
     * @param query
     * @param responseArea
     * @param type
     */
    private void responseThread(String query, TextArea responseArea, Type
            type, int tableNumber) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = "", output;

                switch (type) {
                    case DDL:
                        response = server.sendRequestDDL(query);
                        break;
                    case DML:
                        response = server.sendRequestDML(query);
                        break;
                    case TABLES:
                        response = server.showTablesRequest(query);
                        createTablesBoxes(response);
                    case VALUES:
                        response = server.showTablesRequest(query);
                        createCheckBoxes(response, tableNumber);
                    case SIMPLE:
                        response = server.showTablesRequest(query);
                    default:
                }
                if (responseArea != null)
                    responseArea.clear();
                    responseArea.appendText(response);
            }
        });
        t.start();
    }
}
