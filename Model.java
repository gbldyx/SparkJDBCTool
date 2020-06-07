package Assignment2;

import Assignment2.Controller;

import javax.validation.constraints.Null;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Model {
    private final Controller controller;
    private static final String databaseName = "";
    private static final String url = "jdbc:postgresql://bigdata28.depts.bingosoft.net:25432/"+databaseName;
    private static final String username = "";
    private static final String pass = "";
    private static Connection conn;

    public Model(Controller c) {
        controller = c;
        DBinit();
    }

    public void showTables() {
        //DBinit();
        try {
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet rs = dbMetaData.getTables(null, null, null,new String[] { "TABLE" });
            List<String> tableNames = new ArrayList<>();
            List<String> fields= new ArrayList<>();
            HashMap<String,List<String>>tree=new HashMap<>();
            while(rs.next())
            {
                String tableName=rs.getString("TABLE_NAME");
                tableNames.add(tableName);
                System.out.println(tableName);
            }
            for(String record:tableNames) {
                if(record.equalsIgnoreCase("__gp_log_master_ext")||
                        record.equalsIgnoreCase("__gp_log_segment_ext")||
                        record.equalsIgnoreCase("gp_disk_free"))
                    continue;
                System.out.println(record);
                String columnName;
                String columnType;
                ResultSet rsForFields = dbMetaData.getColumns(null, "%", record, "%");
                while (rsForFields.next()) {
                    columnName = rsForFields.getString("COLUMN_NAME");
                    System.out.println('\t'+columnName);
                    fields.add(columnName);
                }
                tree.put(record,new ArrayList<>(fields));
                System.out.println(tree.get(record));
                fields.clear();
            }
            controller.renderTree(databaseName,tree);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //DBclose();
    }

    public void query(String statement) {
        //DBinit();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);
            controller.render(rs);
            stmt.close();
        } catch (SQLException throwables){
            throwables.printStackTrace();
        }
        //DBclose();
    }

    public static void DBinit(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if(conn==null||!conn.isClosed()){
                conn = DriverManager.getConnection(url,username,pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DBclose(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
