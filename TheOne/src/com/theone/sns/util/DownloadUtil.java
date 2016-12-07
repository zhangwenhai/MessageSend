package com.theone.sns.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhangwenhai on 2014/10/10.
 */
public class DownloadUtil {

    public static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/theone/audiocache/";

    private static int FILESIZE = 4 * 1024;

    public interface DownloadListener {
        void onComplete();

        void onFailure();
    }

    public static void downFile(String urlStr, String fileName, DownloadListener mDownloadListener) {
        InputStream inputStream = null;
        try {
            if (isFileExist(fileName)) {
                mDownloadListener.onComplete();
                return;
            } else {
                inputStream = getInputStreamFromURL(urlStr);
                File resultFile = write2SDFromInput(fileName, inputStream);
                if (resultFile == null) {
                    mDownloadListener.onFailure();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mDownloadListener.onFailure();
        } finally {
            try {
                if (null != inputStream)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mDownloadListener.onComplete();
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 根据URL得到输入流
     *
     * @param urlStr
     * @return
     */
    public static InputStream getInputStreamFromURL(String urlStr) {
        HttpURLConnection urlConn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            inputStream = urlConn.getInputStream();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param fileName
     * @param input
     * @return
     */
    public static File write2SDFromInput(String fileName, InputStream input) {
        File file = null;
        File mDescFile = null;
        OutputStream output = null;
        try {
            createSDDir();
            mDescFile = createSDFile(fileName);
            file = createSDFile(fileName + ".tmp");
            output = new FileOutputStream(file);
            byte[] buffer = new byte[FILESIZE];
            int length;
            while ((length = (input.read(buffer))) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
            file.renameTo(mDescFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @return
     */
    public static File createSDDir() {
        File dir = new File(SDPATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }
}
