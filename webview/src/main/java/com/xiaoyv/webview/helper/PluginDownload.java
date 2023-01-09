//package com.xiaoyv.webview.helper;
//
//import android.content.Context;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class PluginDownload extends Thread {
//    private long max;
//    private String url;
//    private OnDownload onDownload;
//    private Context context;
//    private File pluginFile;
//
//    public PluginDownload(Context context, String url, OnDownload onDownload) {
//        this.onDownload = onDownload;
//        this.url = url;
//        this.context = context;
//        pluginFile = new File(context.getCacheDir(), "" + System.currentTimeMillis());
//    }
//
//    @Override
//    public void run() {
//        try {
//            URL url = new URL(this.url);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//
//            max = connection.getContentLength();
//            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
//                read(connection.getInputStream(), pluginFile);
//            }
//        } catch (Exception e) {
//            onDownload.error(e);
//            e.fillInStackTrace();
//        }
//    }
//
//    /**
//     * 文件写入
//     *
//     * @param inputStream 数据流
//     * @param file        储存地址
//     * @throws IOException
//     */
//    private void read(InputStream inputStream, File file) throws IOException {
//        FileOutputStream out = new FileOutputStream(file);
//        byte bt[] = new byte[1024];
//        int d;
//        while ((d = inputStream.read(bt)) > 0) {
//            out.write(bt, 0, d);
//            java.text.DecimalFormat df = new java.text.DecimalFormat("##");//传入格式模板
//            String result2 = df.format((float) d / (float) max);
//            onDownload.onProgressChange(Integer.parseInt(result2), max);
//        }
//        onDownload.onSuccess(file);
//        inputStream.close();
//        out.close();
//    }
//
//}
//
