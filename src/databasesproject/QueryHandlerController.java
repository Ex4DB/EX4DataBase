/**
 * Daniel Marzayev 318687134 89-281-02
 * Dani Perov 318810637 89-281-02
 */
package databasesproject;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class QueryHandlerController implements Initializable {
    private final Server server;
    public enum Type {DML, DDL};
    private File userFilesPath = null;
    
    @FXML
    private TextArea textAreaQueryDML;
    @FXML
    private TextArea textAreaQueryDDL;
    @FXML
    private TextArea textAreaResponseDML;
    @FXML
    private TextArea textAreaResponseDDL;
    
    /**
     * Constructor.
     */
    public QueryHandlerController(){
        super();
        this.server = new ConnectDataBase();
    }
    
    /**
     * interface method.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    /**
     * submits DML query.
     * @param event 
     */
    @FXML
    private void submitQueryDML(ActionEvent event){
        String query = this.textAreaQueryDML.getText();
        
        if(!"".equals(query)){
            this.textAreaQueryDML.setText("");
            responseThread(query, this.textAreaResponseDML, Type.DML);
        }
    }
    
    /**
     * submit DDL query.
     * @param event 
     */
    @FXML
    private void submitQueryDDL(ActionEvent event){
        String query = this.textAreaQueryDDL.getText();
        
        if(!"".equals(query)){
            this.textAreaQueryDDL.setText("");
            responseThread(query, this.textAreaResponseDDL, Type.DDL);
        }
    }
    
    /**
     * runs DML script.
     * @param event 
     */
    @FXML
    private void runScriptDML(ActionEvent event){
        ScriptRunner sr = new ScriptRunner(this.server);
        String filePath = this.locateFile("Select DML script");
        String output = sr.DMLScript(filePath);
        
        this.textAreaResponseDML.appendText(output);
    }
    
    /**
     * runs DDL script.
     * @param event 
     */
    @FXML
    private void runScriptDDL(ActionEvent event){
        ScriptRunner sr = new ScriptRunner(this.server);
        String filePath = this.locateFile("Select DDL script");
        String output = sr.DDLScript(filePath);
        
        this.textAreaResponseDDL.appendText(output);
    }
    
    /**
     * opens file browser window to select script file.
     * @param title window title
     * @return 
     */
    private String locateFile(String title){
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        
        if(this.userFilesPath != null){
            chooser.setInitialDirectory(this.userFilesPath);
        }
        File file = chooser.showOpenDialog(
                                this.textAreaQueryDDL.getScene().getWindow());
        this.userFilesPath = file.getParentFile();
        
        return file.getPath();
    }
    
    /**
     * Sends request to server and appends response in a separate thread.
     * @param query
     * @param responseArea
     * @param type 
     */
    private void responseThread(String query, TextArea responseArea, Type type){
        Thread t = new Thread(new Runnable(){
                @Override
                public void run(){
                    String response, output;
                    
                    if(type == Type.DML){
                        response = server.sendRequestDML(query);
                    }else{
                        response = server.sendRequestDDL(query);
                    }
                    responseArea.appendText(response);
                }
            });
            t.start();
    }
}
