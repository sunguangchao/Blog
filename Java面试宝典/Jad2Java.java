package xmlmap;

import java.io.*;

/**
 * Created by 11981 on 2018/3/23.
 * 编写一个程序，将d:\java目录下所有的Java文件复制到d:\jad目录下，
 * 并将原来的扩展名从.java改为.jad
 */
public class Jad2Java {
    public static void main(String[] args) throws Exception{
        File srcDir = new File("java");
        if (!(srcDir.exists()) && srcDir.isDirectory())
            throw new Exception("目录不存在");
        //得到目录下的所有java文件
        //策略模式
        File[] files = srcDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        });
        System.out.println(files.length);
        File destDir = new File("jad");
        if (!destDir.exists())
            destDir.mkdir();
        for (File f : files){
            FileInputStream fis = new FileInputStream(f);
            //根据源文件名得到目的文件名
            String destFileName = f.getName().replaceAll("\\.java$", "jad");
            //要在硬盘中准确地创建一个文件，需要知道文件名和文件的目录
            FileOutputStream fos = new FileOutputStream(new File(destDir, destFileName));
            //将源文件的流拷贝到目标文件的流
            copy(fis, fos);
            fis.close();
            fos.close();
        }
    }

    private static void copy(InputStream ips, OutputStream ops) throws Exception{
        int len = 0;
        byte[] buf = new byte[1024];
        while ((len = ips.read(buf)) != -1){
            ops.write(buf, 0, len);
        }
    }
}
