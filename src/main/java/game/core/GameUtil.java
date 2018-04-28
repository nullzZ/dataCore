package game.core;

public class GameUtil {
//    public static final String XLS_PATH = "src/resources/excel";
//    private static final String packagePath = "com.game.server.provider.domain";
//    private static final String classPath = "src/java/";
//
//    private static final String packagePathPro = "com.game.server.provider";
//    private static final String abstractDataProviderPath = "com.game.server.util.dataProvider.AbstractDataProvider";


    private static final String abstractDataProviderPath = "game.core.dataProvider.AbstractDataProvider";

    public static void main(String[] args) {
        String XLS_PATH = args[0];//excel的路径
        String packagePath = args[1];//类包,com.game.server
        String outPath = args[2];//
        boolean isU = false;
        if (args.length == 4) {
            String isUrl = args[3];//是不是通过url地址
            isU = isUrl.equals("1");
        }
        Excel2JavaBeanUtil.run(XLS_PATH, packagePath + ".provider.domain", outPath);
        GenerateDataProviderUtil.run(XLS_PATH, packagePath + ".provider", outPath, abstractDataProviderPath, isU);
    }
}
