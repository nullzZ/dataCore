package game.core;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenerateDataProviderUtil {
//    public static String XLS_PATH = "src/resources/excel";
//    private static String packagePath = "com.game.server.provider";
//    private static String packagePathDomain = "com.game.server.provider.domain";
//    private static String classPath = "src/java/";
//    private static String abstractDataProviderPath = "com.game.server.util.dataProvider.AbstractDataProvider";// AbstractDataProvider的包

    public static String XLS_PATH = "";
    private static String packagePath = "";
    private static String packagePathDomain = "";
    private static String classPath = "";
    private static String abstractDataProviderPath = "com.game.server.util.dataProvider.AbstractDataProvider";// AbstractDataProvider的包

    private static List<String> allProvider = new ArrayList<>();// 存放�?��生成的类
    private static List<String> allDataName = new ArrayList<>();// 存放�?��数据名称
    private static boolean isURL = false;

    public static void run(String xls_path, String packagePath, String classPath, String abstractDataProviderPath, boolean isURL) {
        GenerateDataProviderUtil.packagePath = packagePath;
        GenerateDataProviderUtil.packagePathDomain = packagePath + ".domain";
        GenerateDataProviderUtil.classPath = classPath;
        GenerateDataProviderUtil.abstractDataProviderPath = abstractDataProviderPath;
        GenerateDataProviderUtil.XLS_PATH = xls_path;
        GenerateDataProviderUtil.isURL = isURL;
        File file = new File(XLS_PATH);
        System.out.println("!!" + XLS_PATH);
        File[] f = file.listFiles();
        System.out.println("@@" + f);
        for (int i = 0; i < f.length; i++) {
            String allFileName = f[i].getName();
            int dot = allFileName.lastIndexOf('.');
            String fileName = allFileName.substring(0, dot);
            String prefix = allFileName
                    .substring(dot + 1, allFileName.length());
            if (prefix.equals("xlsx")) {
                generateJavaBean(fileName);
            }
        }
        generateMain();
    }

    private static void generateJavaBean(String tableName) {
        String className = initcap(tableName) + "Provider";
        String content = parse(tableName, className);
        try {
            File directory = new File(classPath);
            String s = packagePath.replace(".", "/");
            String p = directory.getAbsolutePath() + "/" + s + "/" + className
                    + ".java";
            allProvider.add(className);
            allDataName.add(tableName);
//            FileWriter fw = new FileWriter(p);
////            PrintWriter pw = new PrintWriter(fw);
            PrintWriter pw = new PrintWriter((new OutputStreamWriter(new FileOutputStream(p), "utf-8")));
            pw.println(content);
            pw.flush();
            pw.close();
            System.out.println("Generate Success! " + className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parse(String tableName, String className) {
        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packagePath).append(";");
        sb.append("\r\n\r\n");

        sb.append("import ").append(packagePathDomain).append(".*;");
        sb.append("\r\n\r\n");

        sb.append("import ").append(abstractDataProviderPath).append(";");
        sb.append("\r\n\r\n");

        sb.append("/**\r\n");
        sb.append("* " + className + " 实体类\r");
        // sb.append("* "
        // + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        // .format(new Date()) + "\r\n");
        sb.append("* create by GenerateDataProviderUtil " + "\r\n");
        sb.append("*/ \r");

        sb.append("public class " + className + " ");

        sb.append(" extends AbstractDataProvider" + "<" + initcap(tableName)
                + ">" + "{\r\n");

        sb.append("\tprivate static final " + className + " instance "
                + " = new " + className + "();");
        sb.append("\r\n\r\n");

        sb.append("\tpublic static " + className + " getInstance() {");
        sb.append("\r");
        sb.append("\t\treturn instance;");
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("\tprivate " + className + "() {");
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("\t@Override");
        sb.append("\r");
        sb.append("\tpublic String getFileName() {");
        sb.append("\r");
        sb.append("\t\treturn \"" + tableName + "\";");
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * @param str
     * @return String 返回类型
     * @throws
     * @Title: initcap
     * @Description: TODO 首字母大�?
     */
    private static String initcap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 生成启动�?加载这些静�?数据的�?�?
     */
    public static void generateMain() {
        String className = "MainProvider";

        StringBuffer sb = new StringBuffer();
        sb.append("package ").append(packagePath).append(";");
        sb.append("\r\n\r\n");

        sb.append("import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;");
        sb.append("\r\n\r\n");

        sb.append("/**\r\n");
        sb.append("* " + className + " 实体类\r\n");
        sb.append("* "
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()) + "\r\n");
        sb.append("* create by GenerateDataProviderUtil " + "\r\n");
        sb.append("*/ \r");

        sb.append("public class " + className + " {");
        sb.append("\r\n\r\n");

        sb.append("\tprivate static final Logger logger = LoggerFactory.getLogger(" +
                className + ".class);");
        sb.append("\r\n\r\n");

        sb.append("\tprivate static final " + className + " instance "
                + " = new " + className + "();");
        sb.append("\r\n\r\n");

        sb.append("\tpublic static " + className + " getInstance() {");
        sb.append("\r");
        sb.append("\t\treturn instance;");
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("\tprivate " + className + "() {");
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("\r");
        sb.append("\tpublic void run(String url) {");
        sb.append("\r");
        for (String name : allProvider) {
            if (isURL) {
                sb.append("\t\t" + name + ".getInstance().init(url,true);");
            } else {
                sb.append("\t\t" + name + ".getInstance().init(url,false);");
            }

            sb.append("\r");
            sb.append("\t\tlogger.info(\"加载\"+\"" + name + "-----\"");
            sb.append("+" + name);
            sb.append(".getInstance().getDatas().size()");
            sb.append(");");
            sb.append("\r");
        }
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("\r");
        sb.append("\tpublic void reload(String dataName) {");
        sb.append("\r");
        sb.append("\t\tswitch (dataName) {");
        int i = 0;
        for (String name : allProvider) {
            sb.append("\r");
            sb.append("\t\t\tcase ");
            sb.append("\"" + allDataName.get(i) + "\":");
            sb.append("\n");
            sb.append("\t\t\t\t" + name + ".getInstance().reload();");
            sb.append("\n");
            sb.append("\t\t\t\tbreak;");
            sb.append("\r");
            i++;
        }
        sb.append("\r");
        sb.append("\t}");
        sb.append("\r\n\r\n");

        sb.append("}\r\n");
        sb.append("}\r\n");
        try {
            File directory = new File(classPath);
            String s = packagePath.replace(".", "/");
            String p = directory.getAbsolutePath() + "/" + s + "/" + className
                    + ".java";

//            FileWriter fw = new FileWriter(p);
//            PrintWriter pw = new PrintWriter(fw);
            PrintWriter pw = new PrintWriter((new OutputStreamWriter(new FileOutputStream(p), "utf-8")));
            pw.println(sb.toString());
            pw.flush();
            pw.close();
            System.out.println("Generate main Success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
