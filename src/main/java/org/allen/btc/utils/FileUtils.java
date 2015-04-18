package org.allen.btc.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @auther lansheng.zj
 */
public class FileUtils {

    public static final String file2String(final String fileName) {
        File file = new File(fileName);
        return file2String(file);
    }


    public static final String file2String(final File file) {
        if (file.exists()) {
            char[] data = new char[(int) file.length()];
            boolean result = false;

            FileReader fileReader = null;
            try {
                fileReader = new FileReader(file);
                int len = fileReader.read(data);
                result = (len == data.length);
            }
            catch (IOException e) {
                // e.printStackTrace();
            }
            finally {
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (result) {
                String value = new String(data);
                return value;
            }
        }
        return null;
    }


    public static final void string2File(final String str, final String fileName) throws IOException {
        // 先写入临时文件
        String tmpFile = fileName + ".tmp";
        string2FileNotSafe(str, tmpFile);

        // 备份之前的文件
        String bakFile = fileName + ".bak";
        String prevContent = file2String(fileName);
        if (prevContent != null) {
            string2FileNotSafe(prevContent, bakFile);
        }

        // 删除正式文件
        File file = new File(fileName);
        file.delete();

        // 临时文件改为正式文件
        file = new File(tmpFile);
        file.renameTo(new File(fileName));
    }


    public static final void string2FileNotSafe(final String str, final String fileName) throws IOException {
        File file = new File(fileName);
        File fileParent = file.getParentFile();
        if (fileParent != null) {
            fileParent.mkdirs();
        }
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(str);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (IOException e) {
                    throw e;
                }
            }
        }
    }
}
