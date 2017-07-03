/**
 * Daniel Marzayev 318687134 89-281-02
 * Dani Perov 318810637 89-281-02
 */
package databasesproject;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class QueryHandlerController implements Initializable {
    private final Server server;
    private int vBoxNumber;

    public enum Type {DML, DDL, TABLES, VALUES, SIMPLE}

    ;
    private File userFilesPath = null;

    @FXML
    private TextArea textAreaQueryDML;
    @FXML
    private TextArea textAreaQueryDDL;
    @FXML
    private TextArea textAreaResponseDML;
    @FXML
    private TextArea textAreaResponseDDL;
    @FXML
    private HBox boxTables;
    @FXML
    private TextArea textAreaSimpleQuery;
    @FXML
    private TextArea textAreaResponseSimpleQuery;
    private VBox[] vBoxes;
    private List<String> tables;
    private List<CheckBox> checkBoxes;

    /**
     * Constructor.
     */
    public QueryHandlerController() {
        super();
        this.server = new ConnectDataBase();
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
     * @param event
     */
    @FXML
    public void showTables(ActionEvent event) {
        String query = "show tables;";
        responseThread(query, null, Type.TABLES, 0);
    }

    /**
     * Send to server request for tables values.
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
     * @param event
     */
    @FXML
    public void sendSimpleQuery(ActionEvent event) {
        String select = "SELECT ";
        for (CheckBox value : checkBoxes) {
            if(value.isSelected())
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
            responseThread(query, this.textAreaResponseSimpleQuery, Type.SIMPLE, 0);
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
        ScriptRunner sr = new ScriptRunner(this.server);
        String filePath = this.locateFile("Select DML script");
        String output = sr.DMLScript(filePath);

        this.textAreaResponseDML.appendText(output);
    }

    /**
     * runs DDL script.
     *
     * @param event
     */
    @FXML
    private void runScriptDDL(ActionEvent event) {
        ScriptRunner sr = new ScriptRunner(this.server);
        String filePath = this.locateFile("Select DDL script");
        String output = sr.DDLScript(filePath);

        this.textAreaResponseDDL.appendText(output);
    }

    /**
     * opens file browser window to select script file.
     *
     * @param title window title
     * @return
     */
    private String locateFile(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);

        if (this.userFilesPath != null) {
            chooser.setInitialDirectory(this.userFilesPath);
        }
        File file = chooser.showOpenDialog(
                this.textAreaQueryDDL.getScene().getWindow());
        this.userFilesPath = file.getParentFile();

        return file.getPath();
    }

    /**
     * Create for each table a button.
     * @param tables - the tables from the server.
     */
    private void createTablesBoxes(String tables) {
        String array[] = tables.split(",");
        vBoxNumber = 0;
        for (int i = 0; i < array.length; i++) {
            vBoxes[i] = new VBox();
        }

        for (String table : array) {
            Button button = new Button(table);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    describeTable(table, vBoxNumber);
                }
            });
            vBoxes[vBoxNumber].getChildren().add(button);
            boxTables.getChildren().add(vBoxes[vBoxNumber]);
            boxTables.setAlignment(Pos.BASELINE_LEFT);
            vBoxNumber++;
        }
    }

    /**
     * Create for each value check box.
     * @param values - values from the select table.
     * @param tableNumber - table number.
     */
    private void createCheckBoxes(String values, int tableNumber) {
        String array[] = values.split(",");
        for (String value : array) {
            CheckBox checkBox = new CheckBox(value);
            checkBoxes.add(checkBox);
            vBoxes[tableNumber].getChildren().add(checkBox);
        }
    }

    /**
     * Sends request to server and appends response in a separate thread.
     *
     * @param query
     * @param responseArea
     * @param type
     */
    private void responseThread(String query, TextArea responseArea, Type type, int tableNumber) {
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
                    responseArea.appendText(response);
            }
        });
        t.start();
    }
}
