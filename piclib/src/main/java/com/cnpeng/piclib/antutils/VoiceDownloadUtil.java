package com.cnpeng.piclib.antutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 语音下载工具类
 * Created by Shorr on 16/4/21.
 */
public class VoiceDownloadUtil {

    /**
     * 开始下载
     *
     * @param downloadUrlString
     * @param saveUrlString
     */
    public static void startDownload(final String downloadUrlString, String saveUrlString) {

        final File voiceFile = new File(saveUrlString);
        if (!voiceFile.getParentFile().exists()) {
            if (!voiceFile.getParentFile().mkdirs()) {
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL downloadUrl = new URL(downloadUrlString);
                    HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                    //在网络获取文件输入流
                    InputStream inputStream = connection.getInputStream();
                    //创建文件
                    voiceFile.createNewFile();
                    //创建往存储中的文件输出流
                    OutputStream outputStream = new FileOutputStream(voiceFile);
                    byte[] buffer = new byte[1024 * 4];

                    int length;
                    while ((length = (inputStream.read(buffer))) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
