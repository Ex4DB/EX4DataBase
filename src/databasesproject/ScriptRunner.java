/**
 * Daniel Marzayev 318687134 89-281-02
 * Dani Perov 318810637 89-281-02
 */
package databasesproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class to handle running scripts.
 */
public class ScriptRunner {
    private Server server;
    
    /***
     * Constructor.
     * @param server
     */
    public ScriptRunner(Server server) {
        this.server = server;
    }
    
    /***
     * The function runs a script of DML instructions.
     * @param filepath The file where the script is located.
     * @return A string representing the result of the DML instructions given.
     */
    public String DMLScript(String filepath) {
        StringBuilder sb = new StringBuilder();
        String response;
        
        Path file = Paths.get(filepath);
        try (InputStream in = Files.newInputStream(file);
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            
            //While I have not reached the end of the script...
            while ((line = reader.readLine()) != null) {
                response = server.sendRequestDML(line);
                sb.append(response);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        
        return sb.toString();
    }
    
    /***
     * The function runs a script of DDL instructions.
     * @param filepath The file where the script is located.
     * @return A string representing the result of the DDL instructions given.
     */
    public String DDLScript(String filepath) {
        StringBuilder sb = new StringBuilder();
        String response;
        
        Path file = Paths.get(filepath);
        try (InputStream in = Files.newInputStream(file);
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            
            //While I have not reached the end of the script...
            while ((line = reader.readLine()) != null) {
                response = server.sendRequestDDL(line);
                sb.append(response);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        
        return sb.toString();
    }
}
