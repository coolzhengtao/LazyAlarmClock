package net.rabraffe.lazyalarmclock.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 文件读写
 *
 * @author 郑韬
 */
public class FileUtil {

    /**
     * 保存对象到磁盘
     *
     * @param path     路径
     * @param filename 文件名
     * @param obj      要保存的可序列化对象
     * @return
     */
    public static boolean saveObjectToFile(String path, String filename, Serializable obj) {
        boolean result = false;
        try {
            File file = new File(path + "/" + filename);
            if (file.exists()) {
                file.delete();
            }
            ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(file));
            oo.writeObject(obj);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从文件中获取序列化过的对象
     *
     * @param path     路径
     * @param filename 文件名
     * @return 若不存在文件则返回空
     */
    public static Object getObjectFromFile(String path, String filename) {
        try {
            File file = new File(path + "/" + filename);
            if (!file.exists()) return null;
            ObjectInputStream oo = new ObjectInputStream(new FileInputStream(file));
            try {
                return oo.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据文件名称读取文件内容
     *
     * @return 文件内容
     */
    @SuppressWarnings("resource")
    public static String getStrByFileName(String path, String filename) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path, filename);
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                InputStream in = null;// 创建输出流
                in = new FileInputStream(file);
                int filelen = in.available();
                byte[] filetext = new byte[filelen];
                in.read(filetext);
                String text = new String(filetext, 0, filetext.length, "utf-8");
                return text;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean saveStrToFileName(String path, String filename,
                                            String filestr) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                OutputStreamWriter out = null;// 创建输入流
                try {
                    out = new OutputStreamWriter(new FileOutputStream(file),
                            "utf-8");// 获取输入流
                    out.write(filestr);// 将数据变为字符串后保存
                    if (out != null) {
                        out.close();// 关闭输出
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if (out != null) {
                        out.close();// 关闭输出
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            OutputStreamWriter out = null;// 创建输入流
            try {
                out = new OutputStreamWriter(new FileOutputStream(file),
                        "utf-8");// 获取输入流
                out.write(filestr);// 将数据变为字符串后保存
                if (out != null) {
                    out.close();// 关闭输出
                }
                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (out != null) {
                try {
                    out.close();// 关闭输出
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * 在指定目录下创建文件
     *
     * @param path
     * @param filename
     * @return
     */
    public static boolean createFile(String path, String filename) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 在指定目录下删除文件
     *
     * @param path
     * @param filename
     * @return
     */
    public static boolean deleteFile(String path, String filename) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        file = new File(path, filename);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static String getBytesByFile(String path) {
        // 使用InputStream从文件中读取数据，在已知文件大小的情况下，建立合适的存储字节数组
        File f = new File(path);
        byte b[];
        try {
            InputStream in = new FileInputStream(f);
            b = new byte[(int) f.length()];
            in.read(b); // 读取文件中的内容到b[]数组
            in.close();
            return new String(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件名称
     *
     * @param pathandname 文件路径
     * @return
     */
    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            String name = pathandname.substring(start + 1);
            int eng = name.indexOf(",");
            int cha = name.indexOf("，");
            if (cha == -1 && eng == -1) {
                return name;
            }
            return null;
        } else {
            return null;
        }
    }


}
