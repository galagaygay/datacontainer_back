package njnu.opengms.container.utils;

import java.io.File;

/**
 * Created by SongJie on 2019/4/11 21:08
 */
public class MyFileUtils {
    //查找指定后缀的文件  返回绝对路径
    public static String getSpecificFile(String dirPath, String fileExt) {
        File file = new File(dirPath);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (!f.isDirectory()) {
                String fName = f.getName();
                String extension = fName.substring(fName.lastIndexOf('.') + 1);
                if (fileExt.equals(extension)) {
                    return f.getAbsolutePath();
                }
            }
        }
        return "";
    }
}
