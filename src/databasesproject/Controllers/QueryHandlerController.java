/**
 * Daniel Marzayev 318687134 89-281-02
 * Danny Perov 318810637 89-281-02
 */
package databasesproject.Controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import databasesproject.ScriptRunner;
import databasesproject.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    private TextArea         textAreaQueryDML;
    @FXML
    private TextArea         textAreaQueryDDL;
    @FXML
    private TextArea         textAreaResponseDML;
    @FXML
    private TextArea         textAreaResponseDDL;
    @FXML
    private HBox             boxTables;
    @FXML
    private TextArea         textAreaSimpleQuery;
    @FXML
    private TextArea         textAreaResponseSimpleQuery;
    @FXML
    private Button           dmlBtn;
    @FXML
    private ComboBox<String> tablesCBox;
    @FXML
    private VBox             simpleQueryVBox;
    @FXML
    private Label            chooseColumnsLbl;
    @FXML
    private ScrollPane       columnsScrollPane;
    @FXML
    private VBox             tableColumnsVBox;
    @FXML
    private Label            whereLbl;
    @FXML
    private TextArea         whereTxtArea;
    @FXML
    private Button           simpleQueryBtn;
    @FXML
    private Label            queryResultLbl;
    @FXML
    private TextArea         queryResultTxtArea;

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
     */
    @FXML
    public void showTables() {
        String query = "show tables;";
        getTables(query, tablesCBox, simpleQueryVBox, Type.TABLES);
    }

    /**
     * Send to server request for tables values.
     *
     * @param event
     */
    @FXML
    public void describeTable(ActionEvent event) {

        //Get selected value from comboBox.
        String tableName = tablesCBox.getSelectionModel().getSelectedItem();

        //Check if selected value is not null.
        if (tableName != null) {

            String query = "DESC " + tableName + ";";
            //tables.add(tableName); TODO what is that for?
            getTables(query, tablesCBox, simpleQueryVBox, Type.VALUES);
            //responseThread(query, null, Type.VALUES, tableNumber);
        }

    }

    /**
     * Create for each table a button.
     *
     * @param tables - the tables from the server.
     */
    private void PopulateTablesNames(String tables) {

        //TODO check if response is not an error message

        String tablesNames[] = tables.split(",");

        //Clear comboBox.
        this.tablesCBox.getItems().clear();

        //Insert tables names into comboBox
        this.tablesCBox.getItems().addAll(tablesNames);



      /*  vBoxes = new LinkedList<>();
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
        }*/
    }

    /**
     * Creates checkBoxes for table's columns.
     *
     * @param values List of values.
     */
    private void createCheckBoxes(String values) {

        String tableColumns[] = values.split(",");

        //Clear items from VBox.
        this.tableColumnsVBox.getChildren().clear();

        //TODO test scrollpane in case of many column values

        //TODO find another way other than jumping over 6 elements in the array
        for (int i = 0; i < tableColumns.length; i += 6) {

            CheckBox checkBox = new CheckBox(tableColumns[i]);

            //Set margins.
            VBox.setMargin(checkBox, new Insets(4, 0, 0, 8));

            //Add checkBox to VBox.
            this.tableColumnsVBox.getChildren().add(checkBox);
            //vBoxes.get(tableNumber).getChildren().add(checkBox);
        }

        //Set elements to visible.
        for (Node child : this.simpleQueryVBox.getChildren()){

            if(!child.isVisible()){

                child.setVisible(true);
            }
        }
       /* this.columnsScrollPane.setVisible(true);
        this.chooseColumnsLbl.setVisible(true);
        this.whereLbl.setVisible(true);
        this.whereTxtArea.setVisible(true);
        this.simpleQueryBtn.setVisible(true);
        this.queryResultLbl.setVisible(true);
        this.queryResultTxtArea.setVisible(true);*/
    }

    /**
     * Send to server simple query.
     *
     * @param event
     */
    @FXML
    public void sendSimpleQuery(ActionEvent event) {

        StringBuilder select = new StringBuilder("SELECT ");
        String        tableName;

        for (Node child : tableColumnsVBox.getChildren()) {

            //Check if a CheckBox instance.
            if (child instanceof CheckBox) {

                //Check if selected.
                if (((CheckBox) child).isSelected())
                    select.append(((CheckBox) child).getText()).append(", ");
            }
        }

        //TODO check it works correctly when choosing more than one column.
        //Fix string.
        select.delete(select.length() - 2, select.length() - 1);

        //TODO check if user can select only one table or multiple ones, if so then change comboBox to checkBoxes.
        //Get selected value from comboBox.
        tableName = tablesCBox.getSelectionModel().getSelectedItem();

        select.append("FROM ").append(tableName);
       /* for (String table : tables) {
            select.append(table).append(" ");
        }*/

        String where = " WHERE " + this.whereTxtArea.getText() + ";";
        String query = select + where;
        //TODO build query and sent to server
        if (!"".equals(query)) {
            // this.textAreaQueryDML.setText("");

            //TODO delete print
            System.out.println(query);

            getTables(query, null, null, Type.SIMPLE);
           /* responseThread(query, this.textAreaResponseSimpleQuery, Type
                    .SIMPLE, 0);*/
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

    @FXML
    public void backToHomePage(ActionEvent event) throws IOException {

        Stage          stage;
        Parent         root;
        FXMLLoader     fxmlLoader;
        String         path;
        HomeController controller;

        path = "../Fxml/HomePage.fxml";

        //get reference to the button's stage
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

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

        Platform.runLater(
                () -> {

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
                            PopulateTablesNames(response);
                        case VALUES:
                            response = server.showTablesRequest(query);
                            //createCheckBoxes(response, tableNumber);
                        case SIMPLE:
                            response = server.showTablesRequest(query);
                        default:
                    }
                    if (responseArea != null){

                        responseArea.clear();

                        //Check server response.
                        if(isValidServerResponse(response)) {

                            responseArea.appendText(response);
                        }
                    }
                });
    }

    private void getTables(String query, ComboBox comboBox, VBox vBox, Type type) {

        Platform.runLater(
                () -> {
                    String response;

                    switch (type) {

                        case TABLES:
                            response = server.showTablesRequest(query);
                            PopulateTablesNames(response);
                            break;

                        case VALUES:
                            response = server.showTablesRequest(query);
                            createCheckBoxes(response);
                            System.out.println(response);
                            break;

                        case SIMPLE:
                            response = server.showTablesRequest(query);
                            this.whereTxtArea.clear();
                            this.queryResultTxtArea.clear();

                            //Check the server response.
                            if(isValidServerResponse(response)) {

                                this.queryResultTxtArea.appendText(response);
                            }
                            break;

                        default:
                    }
//                if (responseArea != null)
//                    responseArea.clear();
//                responseArea.appendText(response);
                }
        );
    }

    /**
     * Check if the server response is valid.
     * @param response Server response.
     * @return Is the response valid.
     */
    boolean isValidServerResponse(String response){

        if(response.contains("WRONG QUERY STRUCTURE")){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("WRONG QUERY STRUCTURE");
            alert.setContentText(response); //TODO show only the message itself.

            alert.showAndWait();
            return false;
        }
        else if(response.contains("LOGICAL ERROR")){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("LOGICAL ERROR");
            alert.setContentText(response); //TODO show only the message itself.

            alert.showAndWait();
            return false;
        }

        return true;
    }
}
