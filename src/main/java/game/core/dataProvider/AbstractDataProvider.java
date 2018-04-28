package game.core.dataProvider;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * GameDataProvider必须继承的类
 *
 * @author nullzZ
 */
public abstract class AbstractDataProvider<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProvider.class);
    public static final String XLS_PATH = Thread.currentThread()
            .getContextClassLoader().getResource("excel").getPath();
    public static final int START_LINE = 3;
    private List<String> fieldsList = new ArrayList<>();
    private Map<Integer, String> fields = new HashMap<>();
    private Map<Integer, String> fieldTypes = new HashMap<>();

    protected Map<Object, T> datas = new HashMap<>();
    private String path = "";

    public void init(String path, boolean isUrl) {
        try {
            this.path = path;
            if (isUrl) {
                readUrl(this.getFileName());
            } else {
                read(this.getFileName());
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("异常", e);
            }
        }
    }

    public void readUrl(String fileName) throws Exception {
        InputStream input = doPostInputStream(path + "/" + fileName
                + ".xlsx");
        this.read(input);
    }

    public void read(String fileName) throws Exception {
        File file = new File(path + "/" + fileName
                + ".xlsx");
        this.read(new FileInputStream(file));
    }


    @SuppressWarnings("resource")
    public void read(InputStream input) throws Exception {
//        try {
        this.clear();
//        InputStream input = null;
//            try {
//        input = doPostInputStream(url + "/" + fileName
//                + ".xlsx");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        Workbook wb = null;
//            if(fileName.endsWith(".xls")) {
//                wb = new HSSFWorkbook();
//            } else if(fileName.endsWith(".xlsx")) {
//                wb = new XSSFWorkbook();
//            } else {
//                throw new Exception("文件类型错误！");
//            }

        // 根据文件格式(2003或者2007)来初始化
        wb = new XSSFWorkbook(input);
        // else
        // wb = new HSSFWorkbook(input);
        // System.out.println("------------------------------");
        // System.out.println("fileName:" + fileName);
        readField(wb.getSheetAt(0), 0);
        readFieldType(wb.getSheetAt(0), 2);

        Type genType = this.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType)
                .getActualTypeArguments();
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) params[0];
        String className = clazz.getCanonicalName();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            readData(wb.getSheetAt(i), className);
        }

        // System.out.println("------------------------------end");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    private void readField(Sheet sheet, int rowNum) {
        fields.clear();
        Row row = sheet.getRow(rowNum); // 获得第一个表单的迭代器
        if (row == null) {
            // System.err.println("没有字段,rowNum" + rowNum);
            return;
        }
        Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
        while (cells.hasNext()) {
            Cell cell = cells.next();
            if (cell.toString() == null || cell.toString().equals("")) {
                //System.err.println("没有字段,rowNumIsNull" + rowNum + "cell:"
                //  + cell.getColumnIndex());
                continue;
            }
            fields.put(cell.getColumnIndex(), cell.toString());
            fieldsList.add(cell.toString().trim());
            // System.out.println("Cell #" + cell.getColumnIndex());
            // System.out.println("field数据:" + cell.toString());
        }
    }

    private void readFieldType(Sheet sheet, int rowNum) {
        fieldTypes.clear();
        Row row = sheet.getRow(rowNum); // 获得第一个表单的迭代器
        if (row == null) {
            System.err.println("没有类型,rowNum" + rowNum);
            return;
        }
        Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
        while (cells.hasNext()) {
            Cell cell = cells.next();
            fieldTypes.put(cell.getColumnIndex(), cell.toString().trim());
            // System.out.println("Cell #" + cell.getColumnIndex());
            // System.out.println("fieldType数据:" + cell.toString());
        }
    }

    public void readData(Sheet sheet, String className) {
        int index = 0;
        Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
        while (rows.hasNext()) {
            Row row = rows.next(); // 获得行数据
            if (index != START_LINE) {
                index++;
                continue;
            }
            makeAndAddBean(row, className);
        }
    }

    public void makeAndAddBean(Row row, String className) {
        try {
            Class<?> class1 = Class.forName(className);
            @SuppressWarnings("unchecked")
            T obj = (T) class1.newInstance();
            boolean isFirst = true;
            Object key = null;
            Object value = null;

            for (int i = 0; i < fieldsList.size(); i++) {
                String fieldtype = fieldTypes.get(i);
                if (fieldtype == null) {
                    if (logger.isErrorEnabled()) {
                        logger.error("[readData]type == null|" + className
                                + "|" + i);
                    }
                } else {
                    if (fieldtype.equals("no")) {
                        continue;
                    } else {
                        String name = fieldsList.get(i);
                        Field personNameField = null;
                        try {
                            personNameField = class1.getDeclaredField(name);
                        } catch (NoSuchFieldException e) {
                            Class<?> superclass = class1.getSuperclass();
                            personNameField = superclass.getDeclaredField(name);
                        }
                        personNameField.setAccessible(true);
                        String type = personNameField.getGenericType()
                                .toString();
                        // Object value = null;
                        Cell cell = row.getCell(i);

                        if (cell == null) {
                            continue; // cell===null跳过,代表没有数据
                        }

                        // switch (cell.getCellType()) { // 根据cell中的类型来输出数据
                        // case HSSFCell.CELL_TYPE_NUMERIC:
                        // System.out.println(cell.getNumericCellValue());
                        // break;
                        // case HSSFCell.CELL_TYPE_STRING:
                        // System.out.println(cell.getStringCellValue());
                        // break;
                        // case HSSFCell.CELL_TYPE_BOOLEAN:
                        // System.out.println(cell.getBooleanCellValue());
                        // break;
                        // case HSSFCell.CELL_TYPE_FORMULA:
                        // System.out.println(cell.getCellFormula());
                        // break;
                        // default:
                        // System.out.println("unsuported sell type");
                        // break;
                        // }
                        String content = cell.toString().trim();
                        if (content.equals("")) {
                            continue;
                        }
                        if (type.equals("int")) {
                            value = getInteger(content);
                        } else if (type.equals("class java.lang.String")) {
                            value = content;
                        } else if (type.equals("byte")) {
                            value = getByte(content);
                        } else if (type.equals("boolean")) {
                            value = getBoolean(content);
                        } else if (type.equals("short")) {
                            value = getShort(content);
                        } else if (type.equals("long")) {
                            value = getLong(content);
                        } else if (type.equals("float")) {
                            value = getFloat(content);
                        } else if (type
                                .equals("java.util.List<java.lang.Integer>")) {
                            value = getListInteger(content);
                            personNameField.set(obj, value);
                        } else if (type
                                .equals("java.util.List<java.lang.String>")) {
                            value = getListString(content);
                            personNameField.set(obj, value);
                        } else if (type
                                .equals("java.util.Map<java.lang.Integer, java.lang.Integer>")) {
                            value = getMapII(content);
                            personNameField.set(obj, value);
                        } else if (type
                                .equals("java.util.Map<java.lang.String, java.lang.Integer>")) {
                            value = getMapSI(content);
                            personNameField.set(obj, value);
                        } else if (type
                                .equals("java.util.Map<java.lang.String, java.lang.String>")) {
                            value = getMapSII(content);
                            personNameField.set(obj, value);
                        } else {
                            if (logger.isErrorEnabled()) {
                                logger.error("makeAndAddBean---erro!!!"
                                        + this.getFileName());
                            }
                        }

                        personNameField.set(obj, value);
                        if (isFirst) {
                            key = value;
                            isFirst = false;
                        }
                    }
                }
            }

            if (key != null) {
                datas.put(key, obj);
            } else {
                // logger.error("makeAndAddBean2---erro!!!" +
                // this.getFileName());
                return;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * 类型转换统一方法：
     * </p>
     * 1.检测Cell是否有效 2.类型转换是否成功 3.任何异常都将抛给上层处理，打印异常信息并停止当前Cell的实例化
     *
     * @return
     * @throws Exception
     */
    protected final Integer getInteger(String content) throws Exception {
        return (int) Double.parseDouble(content.trim());
    }

    /**
     * <p>
     * 类型转换统一方法：
     * </p>
     * 1.检测Cell是否有效 2.类型转换是否成功 3.任何异常都将抛给上层处理，打印异常信息并停止当前Cell的实例化
     *
     * @return
     * @throws Exception
     */
    protected final Long getLong(String content) throws Exception {
        return (long) Double.parseDouble(content.trim());
    }

    /**
     * <p>
     * 类型转换统一方法：
     * </p>
     * 1.检测Cell是否有效 2.类型转换是否成功 3.任何异常都将抛给上层处理，打印异常信息并停止当前Cell的实例化
     *
     * @return
     * @throws Exception
     */
    protected final Float getFloat(String content) {
        return Float.parseFloat(content.trim());

    }

    /**
     * <p>
     * 类型转换统一方法：
     * </p>
     * 1.检测Cell是否有效 2.类型转换是否成功 3.任何异常都将抛给上层处理，打印异常信息并停止当前Cell的实例化
     *
     * @return
     * @throws Exception
     */
    protected final Byte getByte(String content) throws Exception {
        return (byte) Float.parseFloat(content.trim());
    }

    protected final short getShort(String content) throws Exception {
        return (short) Float.parseFloat(content.trim());
    }

    // protected final Date getDate(Cell[] cells, String columnName)
    // throws Exception {
    // int column = getColumnByName(columnName);
    // if (isEmpty(cells[column])) {
    // return null;
    // // throw new Exception(errorMsg(column, row));
    // }
    // String dateStr = cells[column].getContents();
    // Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
    // return date;
    // }

    protected final boolean getBoolean(String content) {
        String boolStr = content.trim();
        if (boolStr.equals("true") || boolStr.equals("TRUE")) {
            return true;
        } else if (boolStr.equals("false") || boolStr.equals("FALSE")) {
            return false;
        }
        if (logger.isErrorEnabled()) {
            logger.error("getBoolean" + this.getFileName());
        }
        return false;
    }

    /**
     * <p>
     * 类型转换统一方法：
     * </p>
     * 1.检测Cell是否有效 2.类型转换是否成功 3.任何异常都将抛给上层处理，打印异常信息并停止当前Cell的实例化
     *
     * @return
     * @throws Exception
     */
    protected final List<Integer> getListInteger(String content)
            throws Exception {
        List<Integer> list = new ArrayList<>();
        String[] idArray = content.trim().split(";");
        for (String id : idArray) {
            int value = (int) Double.parseDouble(id);
            list.add(value);
        }
        return list;
    }

    /**
     * <p>
     * 类型转换统一方法：
     * </p>
     * 1.检测Cell是否有效 2.类型转换是否成功 3.任何异常都将抛给上层处理，打印异常信息并停止当前Cell的实例化
     *
     * @return
     * @throws Exception
     */
    protected final List<String> getListString(String content) throws Exception {
        return Arrays.asList(content.trim().split(";"));
    }

    protected final Map<String, Integer> getMapSI(String content)
            throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String[] entrys = content.trim().split(";");
        for (String entry : entrys) {
            String[] kv = entry.trim().split(",");
            if (kv.length == 2) {
                int value = (int) Float.parseFloat(kv[1]);
                map.put(kv[0], value);
            }
        }
        return map;
    }

    protected final Map<Integer, Integer> getMapII(String content)
            throws Exception {
        Map<Integer, Integer> map = new HashMap<>();
        String[] entrys = content.trim().split(";");
        for (String entry : entrys) {
            String[] kv = entry.trim().split(",");
            if (kv.length == 2) {
                int value = (int) Double.parseDouble(kv[1]);
                map.put(Integer.valueOf(kv[0]), value);
            }
        }
        return map;
    }

    protected final Map<String, String> getMapSII(String content)
            throws Exception {
        Map<String, String> map = new HashMap<>();
        String[] entrys = content.trim().split(";");
        for (String entry : entrys) {
            String[] kv = entry.trim().split(",");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    /**
     * 实现该方法，返回excel文件名，不带后缀“.xls”
     *
     * @return
     */
    public abstract String getFileName();

    public Map<Object, T> getDatas() {
        return datas;
    }

    public T getDatas(Object key) {
        return datas.get(key);
    }

    public boolean containsKey(Object key) {
        return datas.containsKey(key);
    }

    public void clear() {
        datas.clear();
        fieldsList.clear();
        fields.clear();
        fieldTypes.clear();
    }

    public void reload() {
        try {
            this.read(getFileName());
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("reload异常", e);
            }
        }
    }

    private InputStream doPostInputStream(String url) throws Exception {
        URL dataUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) dataUrl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Proxy-Connection", "Keep-Alive");
        con.setRequestProperty("accept", "*/*");
        con.setRequestProperty("connection", "Keep-Alive");
        // con.setConnectTimeout(5000);
        // con.setRequestProperty("Content-type", "application/json"
        // + ";charset=UTF-8");
        con.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        con.setDoInput(true);

        con.disconnect();
        return con.getInputStream();
    }

}
