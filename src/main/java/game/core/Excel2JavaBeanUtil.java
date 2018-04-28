package game.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excel2JavaBeanUtil {
    private static List<String> fields = new ArrayList<>();// 字段名称 第一行
    private static List<String> fieldAnnotates = new ArrayList<>();// 字段注释第二行
    private static List<String> fieldTypes = new ArrayList<>();// 字段类型 第二行

    //    private static String XLS_PATH = "src/resources/excel";
//    private static String packagePath = "com.game.server.provider.domain";
//    private static String classPath = "src/java/";
    private static String XLS_PATH = "";
    private static String packagePath = "";
    private static String classPath = "";

    private static boolean isHasListType;// 是否有list类型
    private static boolean isHasMapType;// 是否有map类型

    public static void main(String[] args) {
        // read("D:/workspace/GameCore/src/java/com/game/server/util/poi2Excel/announcement.xlsx");
//        run();
        System.out.println("-------------");
        // readXml("d:/test2.xls");
    }

    public static void run(String XLS_PATH, String packagePath, String classPath) {
        Excel2JavaBeanUtil.XLS_PATH = XLS_PATH;
        Excel2JavaBeanUtil.packagePath = packagePath;
        Excel2JavaBeanUtil.classPath = classPath;
        File directory = new File("");
        String s = packagePath.replace(".", "/");
        String p = directory.getAbsolutePath() + "/" + classPath + "/" + s;
        File d = new File(p);
        if (!d.exists()) {
            d.mkdirs();
        }

        createAbs();

        File file = new File(XLS_PATH);
        File[] f = file.listFiles();
        for (int i = 0; i < f.length; i++) {
            String allFileName = f[i].getName();
            int dot = allFileName.lastIndexOf('.');
            String fileName = allFileName.substring(0, dot);
            String prefix = allFileName
                    .substring(dot + 1, allFileName.length());
            if (prefix.equals("xlsx")) {
                read(fileName);
            }
        }
    }

    @SuppressWarnings("resource")
    public static void read(String fileName) {
        try {
            InputStream input = new FileInputStream(XLS_PATH + "/" + fileName
                    + ".xlsx"); // 建立输入流
            Workbook wb = null;
            // 根据文件格式(2003或者2007)来初始化
            wb = new XSSFWorkbook(input);
            // else
            // wb = new HSSFWorkbook(input);
            System.out.println("------------------------------");
            System.out.println("fileName:" + fileName);
            readField(wb.getSheetAt(0), 0);
            readFieldAnnotate(wb.getSheetAt(0), 1);
            readFieldType(wb.getSheetAt(0), 2);
            generateJavaBean(fileName);
            System.out.println("------------------------------end");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void readField(Sheet sheet, int rowNum) {
        fields.clear();
        Row row = sheet.getRow(rowNum); // 获得第一个表单的迭代器
        if (row == null) {
            System.err.println("没有字段,rowNum" + rowNum);
            return;
        }
        Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
        while (cells.hasNext()) {
            Cell cell = cells.next();
            if (cell.toString() == null || cell.toString().equals("")) {
                // System.err.println("没有字段,rowNumIsNull" + rowNum + "cell:"
                //      + cell.getColumnIndex());
                continue;
            }
            fields.add(cell.toString());
            // System.out.println("Cell #" + cell.getColumnIndex());
            // System.out.println("field数据:" + cell.toString());
        }
    }

    private static void readFieldAnnotate(Sheet sheet, int rowNum) {
        fieldAnnotates.clear();
        Row row = sheet.getRow(rowNum); // 获得第一个表单的迭代器
        if (row == null) {
            System.err.println("没有注解,rowNum" + rowNum);
            return;
        }
        Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
        while (cells.hasNext()) {
            Cell cell = cells.next();
            fieldAnnotates.add(cell.toString().trim());
            // System.out.println("Cell #" + cell.getColumnIndex());
            // System.out.println("fieldAnnotate数据:" + cell.toString());
        }
    }

    private static void readFieldType(Sheet sheet, int rowNum) {
        fieldTypes.clear();
        isHasListType = false;
        isHasMapType = false;
        Row row = sheet.getRow(rowNum); // 获得第一个表单的迭代器
        if (row == null) {
            System.err.println("没有类型,rowNum" + rowNum);
            return;
        }
        Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
        while (cells.hasNext()) {
            Cell cell = cells.next();
            fieldTypes.add(cell.toString().trim());
            checkType(cell.toString());
            // System.out.println("Cell #" + cell.getColumnIndex());
            // System.out.println("fieldType数据:" + cell.toString());
        }
    }

    private static void generateJavaBean(String tableName) {
        String content = parse(tableName);
        try {
            File directory = new File(classPath);
            String s = packagePath.replace(".", "/");
            String p = directory.getAbsolutePath() + "/" + s + "/"
                    + initcap(tableName) + ".java";

//            FileWriter fw = new FileWriter(p);
//            PrintWriter pw = new PrintWriter(fw);
            PrintWriter pw = new PrintWriter((new OutputStreamWriter(new FileOutputStream(p), "utf-8")));
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建父类
     *
     * @return
     */
    private static void createAbs() {
        File cf = new File("");
        String cs = packagePath.replace(".", "/");
        String cp = cf.getAbsolutePath() + "/" + classPath + "/" + cs + "/abs";
        File d = new File(cp);
        if (!d.exists()) {
            d.mkdirs();
        }

        // AbsGameData
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("package ").append(packagePath).append(".abs;");
            sb.append("\r\n\r\n");
            sb.append("/**\r\n");
            sb.append("* AbsGameData实体类\r");
            sb.append("* create by GenerateJavaBeanUtil " + "\r\n");
            sb.append("*/ \r");
            sb.append("public class AbsGameData " + "{\r\n");
            processAllMethod(sb);
            sb.append("}\r\n");

            File directory = new File(classPath);
            String s = packagePath.replace(".", "/");
            String p = directory.getAbsolutePath() + "/" + s + "/abs/"
                    + "AbsGameData.java";
            FileWriter fw;

//            fw = new FileWriter(p);
//            PrintWriter pw = new PrintWriter(fw);
            PrintWriter pw = new PrintWriter((new OutputStreamWriter(new FileOutputStream(p), "utf-8")));
            pw.println(sb.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String parse(String tableName) {
        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packagePath).append(";");
        sb.append("\r\n\r\n");
        if (isHasListType) {
            sb.append("import ").append("java.util.List").append(";");
            sb.append("\r\n");
            sb.append("import ").append("java.util.ArrayList").append(";");
            sb.append("\r\n");
        }
        if (isHasMapType) {
            sb.append("import ").append("java.util.Map").append(";");
            sb.append("import ").append("java.util.HashMap").append(";");
        }
        sb.append("import ").append(packagePath + ".abs.AbsGameData")
                .append(";");
        sb.append("\r\n");

        sb.append("\r\n\r\n");
        sb.append("/**\r\n");
        sb.append("* " + tableName + " 实体类\r");
        // sb.append("* "
        // + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        // .format(new Date()) + "\r\n");
        sb.append("* create by GenerateJavaBeanUtil " + "\r\n");
        sb.append("*/ \r");
        // sb.append("public class " + initcap(tableName) + "{\r\n");

        sb.append("public class " + initcap(tableName) + " extends AbsGameData"
                + "{\r\n");

        processAllAttrs(sb);
        sb.append("\r\n");
        processAllMethod(sb);
        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * @param sb
     * @return void 返回类型
     * @throws
     * @Title: processAllAttrs
     * @Description: TODO 生成属性
     */
    private static void processAllAttrs(StringBuffer sb) {
        for (int i = 0; i < fields.size(); i++) {
            if (!fieldTypes.get(i).equals("no")) {
                sb.append("\t\r\n\r\n");
                sb.append("\t/**\r\n");
                sb.append("\t* " + fieldAnnotates.get(i) + " \r");
                // sb.append("\t* "
                // + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                // .format(new Date()) + "\r\n");
                sb.append("\t* create by GenerateJavaBeanUtil " + "\r\n");
                sb.append("\t*/ \r");
                sb.append("\tprivate ");// 权限
                sb.append(toJavaType(fieldTypes.get(i).trim()) + " ");// 属性类型
                sb.append(fields.get(i) + " = ");// 字段名
                sb.append(toJavaFieldInit(fieldTypes.get(i).trim()));// 初始值

                sb.append(";\r\n");
            }
        }
    }

    /**
     * @param sb
     * @return void 返回类型
     * @throws
     * @Title: processAllMethod
     * @Description: TODO 生成方法类
     */
    private static void processAllMethod(StringBuffer sb) {
        for (int i = 0; i < fields.size(); i++) {
            if (!fieldTypes.get(i).equals("no")) {
                sb.append("\tpublic void set" + initcap(fields.get(i)) + "("
                        + toJavaType(fieldTypes.get(i)) + " " + fields.get(i)
                        + "){\r\n");
                sb.append("\t\tthis." + fields.get(i) + "=" + fields.get(i)
                        + ";\r\n");
                sb.append("\t}\r\n");
                sb.append("\r\n");
                sb.append("\tpublic " + toJavaType(fieldTypes.get(i)) + " get"
                        + initcap(fields.get(i)) + "(){\r\n");
                sb.append("\t\treturn " + fields.get(i) + ";\r\n");
                sb.append("\t}\r\n");
                sb.append("\r\n");
            }
        }
    }

    /**
     * @param str
     * @return String 返回类型
     * @throws
     * @Title: initcap
     * @Description: TODO 首字母大写
     */
    private static String initcap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 变换成java的类型
     *
     * @param type
     * @return
     */
    private static String toJavaType(String type) {
        String retType = "";
        if (type.equals("int")) {
            retType = "int";
        } else if (type.equals("String")) {
            retType = "String";
        } else if (type.equals("byte")) {
            retType = "byte";
        } else if (type.equals("boolean")) {
            retType = "boolean";
        } else if (type.equals("short")) {
            retType = "short";
        } else if (type.equals("long")) {
            retType = "long";
        } else if (type.equals("float")) {
            retType = "float";
        } else if (type.equals("List<int>")) {
            retType = "List<Integer>";
        } else if (type.equals("List<String>")) {
            retType = "List<String>";
        } else if (type.equals("Map<int,int>")) {
            retType = "Map<Integer, Integer>";
        } else if (type.equals("Map<String,int>")) {
            retType = "Map<String, Integer>";
        } else if (type.equals("Map<String,String>")) {
            retType = "Map<String, String>";
        } else {
            System.err.println("type异常");
        }
        return retType;
    }

    private static String toJavaFieldInit(String type) {
        String init = "";
        if (type.equals("int")) {
            init = "0";
        } else if (type.equals("String")) {
            init = "\"\"";
        } else if (type.equals("byte")) {
            init = "0";
        } else if (type.equals("boolean")) {
            init = "false";
        } else if (type.equals("short")) {
            init = "0";
        } else if (type.equals("long")) {
            init = "0L";
        } else if (type.equals("float")) {
            init = "0";
        } else if (type.equals("List<int>")) {
            init = "new ArrayList<Integer>()";
        } else if (type.equals("List<String>")) {
            init = "new ArrayList<String>()";
        } else if (type.equals("Map<int,int>")) {
            init = "new HashMap<Integer, Integer>()";
        } else if (type.equals("Map<String,int>")) {
            init = "new HashMap<String, Integer>()";
        } else if (type.equals("Map<String,String>")) {
            init = "new HashMap<String, String>()";
        } else {
            System.err.println("type异常");
        }
        return init;
    }

    private static boolean checkType(String type) {
        if (type.contains("List")) {
            isHasListType = true;
            return true;
        } else if (type.contains("Map")) {
            isHasMapType = true;
            return true;
        } else {
            return false;
        }
    }

    // public static void read(String fileName) {
    // boolean isE2007 = false; // 判断是否是excel2007格式
    // if (fileName.endsWith("xlsx"))
    // isE2007 = true;
    // try {
    // InputStream input = new FileInputStream(fileName); // 建立输入流
    // Workbook wb = null;
    // // 根据文件格式(2003或者2007)来初始化
    // if (isE2007)
    // wb = new XSSFWorkbook(input);
    // else
    // wb = new HSSFWorkbook(input);
    // Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
    // Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
    // while (rows.hasNext()) {
    // Row row = rows.next(); // 获得行数据
    // System.out.println("Row #" + row.getRowNum()); // 获得行号从0开始
    // Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
    // while (cells.hasNext()) {
    // Cell cell = cells.next();
    // System.out.println("Cell #" + cell.getColumnIndex());
    // System.out.println("数据:" + cell.toString());
    //
    // // switch (cell.getCellType()) { // 根据cell中的类型来输出数据
    // // case HSSFCell.CELL_TYPE_NUMERIC:
    // // //System.out.println(cell.getNumericCellValue());
    // // break;
    // // case HSSFCell.CELL_TYPE_STRING:
    // // //System.out.println(cell.getStringCellValue());
    // // break;
    // // case HSSFCell.CELL_TYPE_BOOLEAN:
    // // //System.out.println(cell.getBooleanCellValue());
    // // break;
    // // case HSSFCell.CELL_TYPE_FORMULA:
    // // //System.out.println(cell.getCellFormula());
    // // break;
    // // default:
    // // //System.out.println("unsuported sell type");
    // // break;
    // // }
    // }
    // }
    // } catch (IOException ex) {
    // ex.printStackTrace();
    // }
    // }
}
