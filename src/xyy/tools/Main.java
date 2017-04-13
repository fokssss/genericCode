package xyy.tools;

import java.sql.SQLException;
import java.util.HashMap;

public class Main {
    //数据库连接
    public static final java.lang.String JDBC_URL = "jdbc:mysql://47.92.67.238:3306/chnbs";
    public static final String NAME = "root";
    public static final String PASS = "biscuit";
    public static final String DRIVER = "com.mysql.jdbc.Driver";

    public static HashMap<String, EntityInfo> entityInfos = new HashMap<>();

    static {
        EntityInfo item = new EntityInfo("status", "1");
        entityInfos.put(
                "CSUser", item);
        item = new EntityInfo("status", "1");
        entityInfos.put(
                "CSParty", item);
        item = new EntityInfo("status", "1");
        entityInfos.put(
                "CSUserParty", item);
    }

    public static void main(String[] args) {
//        String dbUrl = "jdbc:mysql://localhost:3306/chnbs";
        String outPath = "/Users/xyy/githubs/chnsbwin/src/";
//        String outPath = "D:\\github\\chnsbwin\\src\\";

        GenericObject generic = new GenericObject(outPath);

        try {
            generic.build();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("done.");

    }

    public static String getDefaultValue(String entityName, String colname) {
        EntityInfo info = entityInfos.get(entityName);
        if (info == null) {
            return null;
        }
        String value = info.getDefultValue(colname);
        return value;
    }
}
