package Assignment2;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class Controller {
    private Model model;
    private View  view;

    public Controller(){
        this.model = new Model(this);
        this.view =new View(this);
        showTables();
    }

    public void query(String statement){
        model.query(statement);
    }

    public void render(ResultSet rs){
        view.render(rs);
    }

    public void start(){
        view.start();
    }

    public void  showTables(){
        model.showTables();
    }

    public void renderTree(String databaseName, HashMap<String, List<String>> map){
        view.renderTree(databaseName,map);
    }
}
