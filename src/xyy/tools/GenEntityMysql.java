package xyy.tools;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenEntityMysql {

    private String enitityPackageName = "xyy.chnbs.biz";//指定实体生成所在包的路径
    private String authorName = "xiongyy";//作者名字

    private boolean f_util = true; // 是否需要导入包java.util.*
    private boolean f_sql = true; // 是否需要导入包java.sql.*


    //输出文件的路径
    private String mOutPath = "";

    //数据库连接
    private String jdbcUrl = "jdbc:mysql://localhost:3306/test";
    private static final String NAME = "root";
    private static final String PASS = "root";
    private static final String DRIVER = "com.mysql.jdbc.Driver";

    /*
     * 构造函数
     */
    public GenEntityMysql(String url, String outpath) {
        jdbcUrl = url;
        mOutPath = outpath;
    }

    public void build() throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        String sql = "show tables";

        //查要生成实体类的表
        PreparedStatement pStemt = con.prepareStatement(sql);
        ResultSet rs = pStemt.executeQuery();
        while (rs.next()) {
            build(rs.getString(1));
        }
        rs.close();
        pStemt.close();
        closeConnection();
    }

    private void build(String tablename) throws SQLException, ClassNotFoundException {
        //创建连接
        Connection con = getConnection();

        //查要生成实体类的表
        String sql = "select * from " + tablename;
        PreparedStatement pStemt = con.prepareStatement(sql);
        buildTable(pStemt, tablename);
        pStemt.close();
    }

    private void buildTable(PreparedStatement pStemt, String entityName) {
        System.out.println("build - " + entityName);

        try {
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();    //统计列

            String[] colnames; // 列名数组
            String[] colTypes; //列名类型数组
            int[] colSizes; //列名大小数组

            colnames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];
            for (int i = 0; i < size; i++) {
                colnames[i] = rsmd.getColumnName(i + 1);
                colTypes[i] = rsmd.getColumnTypeName(i + 1);

                if (colTypes[i].equalsIgnoreCase("datetime")) {
                    f_util = true;
                }
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
                    f_sql = true;
                }
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }

            String content = parse(entityName, colnames, colTypes, colSizes);
            buildJavaObjectFile(entityName, content);

            buildBatisMappingFile(entityName, colnames, colTypes, colSizes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //    <?xml version="1.0" encoding="UTF-8" ?>
//<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
//            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
//
//<mapper namespace="com.chnbs.biz.mapping.CS_USER_MAPPING">
//    <select id="selectUserByID" parameterType="int" resultType="CS_USER">
//    select * from `CS_USER` where pkid = #{id}
//    </select>
    private void buildBatisMappingFile(String entityName, String[] colnames, String[] colTypes, int[] colSizes) {
        String path = mOutPath + enitityPackageName.replace(".", "/") + "/mapping";
        new File(path).mkdirs();
        String clsName = initcap(entityName);
        String mappingFileName = path + "/" + clsName + ".xml";
        System.out.println("create xml mapping file - " + mappingFileName);
        List<String> cols = removePkID(colnames);
        FileWriter fw = null;
        try {
            fw = new FileWriter(mappingFileName);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            pw.println("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            pw.println("");
            pw.println("<mapper namespace=\"" + enitityPackageName + ".mapping." + clsName + "_MAPPING\">");
            pw.println("\t<select id=\"selectByID\" parameterType=\"int\" resultType=\"" + clsName + "\">");
            pw.println("\t\tselect * from `" + entityName + "` where pkid = #{id}");
            pw.println("\t</select>");
            pw.println("\t<select id=\"getAll\" resultType=\"" + clsName + "\">");
            pw.println("\t\tselect * from `" + clsName + "`");
            pw.println("\t</select>");
//    <insert id="addItem" parameterType="CS_USER">
//        <selectKey resultType="java.lang.Short" order="AFTER" keyProperty="pkid">
//                    SELECT LAST_INSERT_ID() AS pkid
//        </selectKey>
//                    insert into CS_USER (login, passwd,
//                    userKey)
//            values (#{login},
//        #{passwd},
//        #{userKey}
//        )
//    </insert>
            pw.println("\t<insert id=\"addItem\" parameterType=\"" + clsName + "\">");
            pw.println("\t\t<selectKey resultType=\"java.lang.Short\" order=\"AFTER\" keyProperty=\"pkid\">");
            pw.println("\t\t\tSELECT LAST_INSERT_ID() AS pkid");
            pw.println("\t\t</selectKey>");
            pw.println("\t\tinsert into " + clsName + " (");
            for (int i = 0; i < cols.size(); i++) {
                if (i == 0) {
                    pw.print("\t\t\t" + cols.get(i) + "");
                } else {
                    pw.print("," + cols.get(i) + "");
                }
            }
            pw.println(") ");
            pw.println("\t\tvalues (");
            for (int i = 0; i < cols.size(); i++) {
                if (i == 0) {
                    pw.print("\t\t\t#{" + cols.get(i) + "}");
                } else {
                    pw.print(",#{" + cols.get(i) + "}");
                }
            }
            pw.println(") ");
            pw.println("\t</insert>");
//             <update id="updateItem" parameterType="CS_USER">
//                    update CS_USER set login=#{login} where pkid=#{pkid}
//    </update>
//    <delete id="deleteItem" parameterType="CS_USER">
//                    delete from CS_USER where pkid=#{pkid}
//    </delete>
            pw.println("\t<update id=\"updateItem\" parameterType=\"" + clsName + "\">");
            pw.print("\t\tupdate " + clsName + " set ");
            for (int i = 0; i < cols.size(); i++) {
                if ((i) % 5 == 0) {
                    pw.println("");
                    pw.print("\t\t\t");
                }
                String item = cols.get(i);
                if (i == 0) {
                    pw.print("" + item + "=#{" + cols.get(i) + "}");
                } else {
                    pw.print("," + item + "=#{" + cols.get(i) + "}");
                }

            }
            pw.println(" where pkid=#{pkid}");
            pw.println("\t</update>");
            pw.println("\t<delete id=\"deleteItem\" parameterType=\"" + clsName + "\">");
            pw.println("\t\tdelete from " + clsName + " where pkid=#{pkid}");
            pw.println("\t</delete>");
            pw.println("");
            pw.println("");
            pw.println("</mapper>");
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> removePkID(String[] colnames) {
        ArrayList<String> rs = new ArrayList<String>();
        for (int i = 0; i < colnames.length; i++) {
            if (colnames[i].equalsIgnoreCase("pkid")) {
                continue;
            }
            rs.add(colnames[i]);
        }
        return rs;
    }

    private void buildJavaObjectFile(String entityName, String content) {
        try {
            String path = mOutPath + enitityPackageName.replace(".", "/") + "/model";
            new File(path).mkdirs();
            String javaFileName = path + "/" + initcap(entityName) + ".java";
            System.out.println("create java file - " + javaFileName);
            FileWriter fw = new FileWriter(javaFileName);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection _conn = null;

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        if (_conn == null) {
            Class.forName(DRIVER);
            _conn = DriverManager.getConnection(jdbcUrl, NAME, PASS);
        }
        return _conn;
    }

    private void closeConnection() {
        try {
            _conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        _conn = null;
    }

    /**
     * 功能：生成实体类主体代码
     *
     * @param colnames
     * @param colTypes
     * @param colSizes
     * @return
     */
    private String parse(String tablename, String[] colnames, String[] colTypes, int[] colSizes) {
        StringBuffer s = new StringBuffer();

        s.append("package " + this.enitityPackageName + ".model;\r\n");
        s.append("\r\n");
        //判断是否导入工具包
        if (f_util) {
            s.append("import java.util.Date;\r\n");
        }
        if (f_sql) {
            s.append("import java.sql.*;\r\n");
        }
        s.append("import xyy.base.TextUtils;\r\n");
        s.append("import java.util.HashMap;\r\n");

        s.append("\r\n");
        //注释部分
        s.append("   /**\r\n");
        s.append("    * " + tablename + " 实体类\r\n");
        s.append("    * " + new Date() + " " + this.authorName + "\r\n");
        s.append("    */ \r\n");
        //实体部分
        String clsName = initcap(tablename);
        s.append("\r\n\r\npublic class " + clsName + "{\r\n");
        processAllAttrs(s, colnames, colTypes);//属性
        processAllMethod(s, colnames, colTypes);//get set方法

        //创建构造方法
//        public static CSUser from(HashMap<String, ?> data) {
//            CSUser rs = new CSUser();
//            rs.setLogin(TextUtils.getString(data, "login", ""));
//            rs.setStatus(TextUtils.getInt(data, "status", 0));
//            return rs;
//        }
        s.append("\r\n");
        s.append("\tpublic static " + clsName + " from(HashMap<String, ?> data) {\r\n");
        s.append("\t\t" + clsName + " rs = new " + clsName + "();\r\n");
        for (int i = 0; i < colnames.length; i++) {
            String javaType = sqlType2JavaType(colTypes[i]);
            switch (javaType) {
                case "int":
                    s.append("\t\trs.set" + initcap(colnames[i]) + "(TextUtils.getInt(data, \"" + colnames[i] + "\", 0));\r\n");
                    break;
                default:
                    s.append("\t\trs.set" + initcap(colnames[i]) + "(TextUtils.getString(data, \"" + colnames[i] + "\", \"\"));\r\n");
            }
            s.append("\r\n");
        }
        s.append("\t\treturn rs;\r\n");
        s.append("\t}\r\n");
        //类结束
        s.append("}\r\n");

        //System.out.println(stringBuffer.toString());
        return s.toString();
    }

    /**
     * 功能：生成所有属性
     *
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb, String[] colnames, String[] colTypes) {

        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n");
        }

    }

    /**
     * 功能：生成所有方法
     *
     * @param sb
     */
    private void processAllMethod(StringBuffer sb, String[] colnames, String[] colTypes) {

        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tpublic void set" + initcap(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " " +
                    colnames[i] + "){\r\n");
            sb.append("\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");
            sb.append("\t\treturn " + colnames[i] + ";\r\n");
            sb.append("\t}\r\n");
        }

    }

    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    private String initcap(String str) {

        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }

        return new String(ch);
    }

    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {

        if (sqlType.equalsIgnoreCase("bit")) {
            return "boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "short";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "int";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "float";
        } else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "double";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        } else if (sqlType.equalsIgnoreCase("BLOB")) {
            return "byte[]";
        }

        throw new Error("not found sql type - " + sqlType);
    }


}