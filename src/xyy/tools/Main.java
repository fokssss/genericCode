package xyy.tools;

import java.sql.SQLException;

public class Main {
    //数据库连接
    public static final java.lang.String JDBC_URL = "jdbc:mysql://47.92.67.238:3306/chnbs";
    public static final String NAME = "root";
    public static final String PASS = "biscuit";
    public static final String DRIVER = "com.mysql.jdbc.Driver";

    public static void main(String[] args) {
//        String dbUrl = "jdbc:mysql://localhost:3306/chnbs";
//        String outPath = "/Users/xyy/workspace/chnsball/src/";
        String outPath = "D:\\workspace\\chnsb\\src\\";

        GenEntityMysql generic = new GenEntityMysql(outPath);

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
