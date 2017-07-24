/**
 * Daniel Marzayev 318687134 89-281-02
 * Danny Perov 318810637 89-281-02
 */
package databasesproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for communication with remote database server.
 */
public class ConnectDataBase implements Server {
    private Connection connection;
    private String host;
    private String uName;
    private String uPass;
    private List<String> updateTriggerDML;
    private List<String> executeTriggerDDL;

    /**
     * Constructor. Initializes filter lists for DML and DDL methods.
     * The lists are used to distinguish between executeUpdate and executeQuery methods.
     */
    public ConnectDataBase() {
        this.connector();
        this.updateTriggerDML = new ArrayList();
        this.updateTriggerDML.add("INSERT");
        this.updateTriggerDML.add("UPDATE");
        this.updateTriggerDML.add("DELETE");

        this.executeTriggerDDL = new ArrayList();
        this.executeTriggerDDL.add("SHOW");
        this.executeTriggerDDL.add("DESCRIBE");
    }

    /**
     * Connects to remote SQL server.
     * Reads login credentials from conf.txt file.
     */
    private void connector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
        this.connection = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("conf.txt"));
            String line;
            line = reader.readLine();
            if (line == null) {
                return;
            }
            String host = line;
            line = reader.readLine();
            if (line == null) {
                return;
            }
            String uName = line;
            line = reader.readLine();
            if (line == null) {
                return;
            }
            String uPass = line;
            reader.close();
            this.connection = DriverManager.getConnection(host, uName, uPass);
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends DDL query to remote SQL server.
     *
     * @param query the query.
     * @return the response.
     */
    public String sendToDataDDL(String query) {
        String result;
        String temp;
        String triggerQuery = query.split(" ")[0].toUpperCase();

        try {
            Statement st = this.connection.createStatement();

            // check if the first word 
            if (this.executeTriggerDDL.contains(triggerQuery)) {

                // query prints output
                ResultSet rs = st.executeQuery(query);
                StringBuilder strBuild = new StringBuilder();
                int columnsNumber = rs.getMetaData().getColumnCount();

                // iterate result set and  build response
                while (rs.next()) {
                    for (int i = 1; i < columnsNumber; ++i) {
                        temp = rs.getString(i) + ", ";
                        strBuild.append(temp);
                    }
                    strBuild.append(rs.getString(columnsNumber));
                    strBuild.append("\n");
                }
                result = strBuild.toString();
            } else {
                // query should not print anything
                int out = st.executeUpdate(query);
                result = "Rows affected: " + Integer.toString(out);
            }
        } catch (SQLException err) {
            // error handling
            String error = err.toString();
            System.out.println(error);
            if (error.contains("syntax")) {
                result = STRUCTURE_ERROR;
            } else {
                result = LOGICAL_ERROR;
            }
            // format error
            return formatResponse(query, result, error);
        }
        // format response
        return formatResponse(query, result, null);
    }

    /**
     * Sends DML query to remote SQL server.
     *
     * @param query
     * @return
     */
    public String sendToDataDML(String query) {
        String result;
        String temp;
        String triggerQuery = query.split(" ")[0].toUpperCase();

        try {
            Statement st = connection.createStatement();

            if (this.updateTriggerDML.contains(triggerQuery)) {
                // query should not print anything.
                int out = st.executeUpdate(query);
                result = "Rows affected: " + Integer.toString(out);
            } else {
                // query returns data
                ResultSet rs = st.executeQuery(query);
                StringBuilder strBuild = new StringBuilder();
                int columnsNumber = rs.getMetaData().getColumnCount();

                // iterate result set and  build response
                while (rs.next()) {
                    for (int i = 1; i < columnsNumber; ++i) {
                        temp = rs.getString(i) + ", ";
                        strBuild.append(temp);
                    }
                    strBuild.append(rs.getString(columnsNumber));
                    strBuild.append("\n");
                }
                result = strBuild.toString();
            }
        } catch (SQLException err) {
            // error handling
            String error = err.toString();
            System.out.println(error);
            if (error.contains("syntax")) {
                result = STRUCTURE_ERROR;
            } else {
                result = LOGICAL_ERROR;
            }
            // format error
            return formatResponse(query, result, error);
        }
        // format response
        return formatResponse(query, result, null);
    }

    /**
     * @param query
     * @return
     */
    public String sendToDataTables(String query) {
        String result;
        String temp;
        try {
            Statement st = connection.createStatement();

            // query returns data
            ResultSet rs = st.executeQuery(query);

            if(query.startsWith("DESC")) {
                String dataBaseArray = "";
                while (rs.next()) {
                    dataBaseArray += (rs.getString(1) + ',');
                }
                return dataBaseArray;
            }

            StringBuilder strBuild = new StringBuilder();
            int columnsNumber = rs.getMetaData().getColumnCount();

            // iterate result set and  build response
            while (rs.next()) {
                for (int i = 1; i < columnsNumber; ++i) {
                    temp = rs.getString(i) + ", ";
                    strBuild.append(temp);
                }
                strBuild.append(rs.getString(columnsNumber));
                strBuild.append(",");
            }
            result = strBuild.toString();

        } catch (SQLException err) {
            // error handling
            String error = err.toString();
            System.out.println(error);
            if (error.contains("syntax")) {
                result = STRUCTURE_ERROR;
            } else {
                result = LOGICAL_ERROR;
            }
            // format error
            return formatResponse(query, result, error);
        }
        // format response
        return result;
    }

    /**
     * interface method.
     *
     * @param request the request
     * @return response string from server
     */
    @Override
    public String sendRequestDDL(String request) {
        return sendToDataDDL(request);
    }

    /**
     * interface method.
     *
     * @param request the request
     * @return response string from server
     */
    @Override
    public String sendRequestDML(String request) {
        return sendToDataDML(request);
    }

    /**
     *
     * @return
     */
    @Override
    public String showTablesRequest(String query) {
        return sendToDataTables(query);
    }

    /**
     * formats response from server.
     *
     * @param query
     * @param response
     * @param error
     * @return
     */
    public static String formatResponse(String query, String response, String error) {
        String output;
        if (error == null)
            error = "";
        else
            error = error + "\n";

        if (Server.LOGICAL_ERROR.equals(response)
                || Server.STRUCTURE_ERROR.equals(response))
            response = response + ": \n" + error;

        output = ">" + query + "\n";
        output = output + "OUTPUT:\n" + response + "\n";

        return output;
    }
}
