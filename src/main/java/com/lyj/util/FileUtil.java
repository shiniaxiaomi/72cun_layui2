package com.lyj.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    //合并路径下的所有文件
    public static boolean unionFile(String outfile, String dictionary) throws IOException {
        boolean result = false;
        List<File> fileList = getFiles(dictionary);
        File fout = new File(outfile);
        FileWriter fw = new FileWriter(fout);
        for (File f : fileList) {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                fw.append(line + "\n");
                line = br.readLine();
            }
            fr.close();
            System.out.println(f.getName()+": 合并成功");
        }
        fw.close();
        result = true;
        return result;
    }

    //根据文件路径获取路径下的所有文件
    public static List<File> getFiles(String path) {
        File root = new File(path);
        List<File> files = new ArrayList<File>();
        if (!root.isDirectory()) {
            files.add(root);
        } else {
            File[] subFiles = root.listFiles();
            for (File f : subFiles) {
                files.addAll(getFiles(f.getAbsolutePath()));
            }
        }
        return files;
    }


    //替换文本文件中的内容
    public static void replacTextContent(String needReplace,String str,String dictionary) throws IOException{
        List<File> fileList = getFiles(dictionary);
        for (File f : fileList) {
            // 读
            FileReader in = new FileReader(f);
            BufferedReader bufIn = new BufferedReader(in);
            // 内存流, 作为临时流
            CharArrayWriter tempStream = new CharArrayWriter();
            // 替换
            String line = null;
            while ((line = bufIn.readLine()) != null) {
                // 替换每行中, 符合条件的字符串
                line = line.replaceAll(needReplace, str);
                // 将该行写入内存
                tempStream.write(line);
                // 添加换行符
                tempStream.append(System.getProperty("line.separator"));
            }
            // 关闭 输入流
            bufIn.close();
            // 将内存中的流 写入 文件
            FileWriter out = new FileWriter(f);
            tempStream.writeTo(out);
            out.close();
            System.out.println(f.getName()+": 内容替换成功");
        }


    }

    //删除指定文件
    public static void deleteFile(String path){
        File file=new File(path);
        if(file.exists()&&file.isFile()){
            boolean delete = file.delete();
            if(delete){
                System.out.println(file.getName()+": 文件删除成功");
            }
        }
    }



    //完成所有操作
    public static void main(String[] args) throws IOException {
        String oldVersion="1.0.1";
        String version="1.0.2";//资源文件版本,更新版本,防止缓存

        //删除原来版本的js
        deleteFile("F:\\lyj_CodeArea\\72cun_mybatis\\src\\main\\resources\\static\\js\\commonElement_"+oldVersion+".js");

        //合并文件,并重命名
        System.out.println(unionFile("F:\\lyj_CodeArea\\72cun_mybatis\\src\\main\\resources\\static\\js\\commonElement_"+version+".js",
                "F:\\lyj_CodeArea\\72cun_mybatis\\src\\main\\resources\\vue"));

        //替换html中原来资源文件的版本
        replacTextContent("commonElement_"+oldVersion+".js","commonElement_"+version+".js",
                "F:\\lyj_CodeArea\\72cun_mybatis\\src\\main\\resources\\templates");
    }
}
