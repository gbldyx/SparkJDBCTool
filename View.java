package Assignment2;

import Assignment2.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View {
    private final JFrame jf;
    private final JTable resultTable;
    private JTree tableTree;
    private final Controller controller;

    public View(Controller c){
        controller = c;

        jf = new JFrame("SparkSQL查询分析器");
        jf.setSize(1280, 720);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);

        JPanel panelSql = panelSQL();

        resultTable = new JTable();
        resultTable.setPreferredScrollableViewportSize(new Dimension(920, 330));
        JScrollPane panelResult = new JScrollPane(resultTable);
        panelResult.setBounds(335, 365, 920, 310);

        JPanel panelTables = panelDb();

        JPanel panel = new JPanel(null);
        panel.add(panelSql);
        panel.add(panelTables);
        panel.add(panelResult);
        jf.setContentPane(panel);
    }

    public void start(){
        jf.setVisible(true);
    }

    public void render(ResultSet rs){
        ArrayList<Object[]> list = new ArrayList<>();
        ArrayList<String> header = new ArrayList<>();
        int numCols = 1;
        try{
            ResultSetMetaData md = rs.getMetaData();
            numCols = md.getColumnCount();
            for(int i=1; i<=numCols; i++){
                header.add(md.getColumnName(i));
            }
            while(rs.next()){
                Object[] result = new Object[numCols];
                for(int i = 1; i<=numCols; i++) {
                    result[i-1] = rs.getObject(i);
                }
                list.add(result);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int numRows = list.size();
        Object[][] data = new Object[numRows][numCols];
        for(int i=0; i<numRows; i++){
            if (numCols >= 0) System.arraycopy(list.get(i), 0, data[i], 0, numCols);
        }
        resultTable.setModel(new DefaultTableModel(data, header.toArray()));
    }

    public void renderTree(String databaseName, HashMap<String, List<String>> map) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(databaseName);
        for(Map.Entry<String, List<String>> entry : map.entrySet()){
            DefaultMutableTreeNode table = new DefaultMutableTreeNode(entry.getKey());
            List<String> cols = entry.getValue();
            for(String name : cols){
                table.add(new DefaultMutableTreeNode(name));
            }
            root.add(table);
        }
        TreeModel treeModel = new DefaultTreeModel(root);
        tableTree.setModel(treeModel);
    }

    private JPanel panelSQL() {
        JTextArea textArea = new JTextArea();

        JButton button = new JButton("运行");
        button.setBounds(0, 0, 80, 30);
        button.addActionListener(e -> controller.query(textArea.getText()));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0, 30, 920, 325);

        JPanel panel = new JPanel(null);
        panel.setBounds(335, 5, 920, 350);
        panel.add(button);
        panel.add(scrollPane);

        return panel;
    }

    private JPanel panelDb() {
        JButton button = new JButton("刷新");
        button.setBounds(0, 0, 80, 30);
        button.addActionListener(e -> controller.showTables());

        tableTree = new JTree();
        tableTree.setShowsRootHandles(true);
        tableTree.setEditable(false);
        JScrollPane panelTables = new JScrollPane(tableTree);
        panelTables.setBounds(0, 30, 320, 640);

        JPanel panel = new JPanel(null);
        panel.setBounds(5, 5, 320, 670);
        panel.add(button);
        panel.add(panelTables);
        return panel;
    }
}
