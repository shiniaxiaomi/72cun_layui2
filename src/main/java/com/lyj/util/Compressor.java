package com.lyj.util;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * js,css压缩工具类
 *
 * 要压缩的时候,吧static文件夹复制出来,在进行压缩,然后压缩完之后的文件直接就可以丢到nginx上即可
 *
 */
public class Compressor {

    private static final String encoding = "utf-8";
    private static final String[] suffixArray = { ".js", ".css" };

    //需要压缩的文件夹(会压缩此文件夹下的所有js、css文件)
//    private String filePath="F:\\lyj_CodeArea\\72cun_buff\\static";//个人
//    private String filePath="F:\\code\\72cun_buff\\static";
    private String filePath="C:\\Users\\lyj80\\Desktop\\eleTree.js";

    static int linebreakpos = 1000;//-1;换行：负数表示不换行
    static boolean munge=true;
    static boolean verbose=false;
    static boolean preserveAllSemiColons=false;
    static boolean disableOptimizations=false;

    public static int count=0;

    public static void main(String[] args) {
        new Compressor().compress();
    }

    /**
     * 压缩方法
     */
    public void compress(){
        Date startTime = new Date();
        File file = new File(filePath);

        init(file);//压缩

        Date endTime = new Date();
        Long cost = endTime.getTime() - startTime.getTime();
        System.out.println("压缩完成，耗时：" + cost + "ms，共压缩文件个数：" + count);
    }

    //初始化，获取文件目录下的所有需要压缩的文件 js css
    public static void init(File file){

        if(file.isDirectory()){//是文件夹,继续遍历
            File[] files = file.listFiles();
            // 如果某个文件夹是空文件夹，则跳过
            if (files == null) {
                return;
            }
            for (File f : files) {
                init(f);
            }

        }else{//不是文件夹,则压缩文件
            compress(file);
        }
    }

    //压缩
    public static void compress(File file){
        try {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            List<String> suffixList = Arrays.asList(suffixArray);
            if (suffixList.contains(suffix) && !fileName.endsWith("min" + suffix)) {//去除掉已经压缩过的文件(以min.js结尾的)

                Reader in = new InputStreamReader(new FileInputStream(file), encoding);//以UTF-8格式读取，否则压缩出来会乱码
                String filePath=file.getAbsolutePath();
                File tempFile = new File(filePath+".tempFile");
                Writer out = new OutputStreamWriter(new FileOutputStream(tempFile),encoding);

                if (fileName.endsWith(".js")) {

                    System.out.println(file.getName());//打印压缩过的文件名称
                    count++;

                    //js compressor
                    JavaScriptCompressor jscompressor = new JavaScriptCompressor(in, new ErrorReporter() {
                        //压缩出错后的操作
                        @Override
                        public void warning(String message, String sourceName,int line, String lineSource, int lineOffset) {
                            if (line < 0) {
                                System.err.println("\n[WARNING] " + message);
                            } else {
                                System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
                            }
                        }

                        @Override
                        public void error(String message, String sourceName,
                                          int line, String lineSource, int lineOffset) {
                            if (line < 0) {
                                System.err.println("\n[ERROR] " + sourceName + "-" + message);
                            } else {
                                System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + sourceName + "-" + message);
                            }
                        }

                        @Override
                        public EvaluatorException runtimeError(String message, String sourceName,
                                                               int line, String lineSource, int lineOffset) {
                            error(message, sourceName, line, lineSource, lineOffset);
                            return new EvaluatorException(message);
                        }
                    });

                    //开始压缩
                    jscompressor.compress(out,linebreakpos, munge, verbose, preserveAllSemiColons, disableOptimizations);
                } else if (fileName.endsWith(".css") && !fileName.endsWith("min.css") ) {

                    System.out.println(file.getName());//打印压缩过的文件名称
                    count++;

                    //css compressor
                    CssCompressor csscompressor = new CssCompressor(in);
                    csscompressor.compress(out, linebreakpos);
                }
                in.close();
                out.close();
                file.delete();
                tempFile.renameTo(file);
                tempFile.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}