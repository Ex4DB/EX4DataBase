/**
 * Daniel Marzayev 318687134 89-281-02
 * Danny Perov 318810637 89-281-02
 */
package databasesproject;

/**
 * Interface for communication with SQL server.
 */
public interface Server {
    public static final String LOGICAL_ERROR = "LOGICAL ERROR";
    public static final String STRUCTURE_ERROR = "WRONG QUERY STRUCTURE";
    
    String sendRequestDDL(String request);
    String sendRequestDML(String request);
    String showTablesRequest(String query);
}
