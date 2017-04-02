package xyy.tools;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/chnbs";
//        String outPath = "/Users/xyy/workspace/chnsball/src/";
        String outPath = ""

        GenEntityMysql generic = new GenEntityMysql(dbUrl, outPath);

        try {
            generic.build();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("done.");

    }
}
