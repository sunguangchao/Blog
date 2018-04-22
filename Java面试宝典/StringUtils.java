

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by sunguangchao on 2018/4/22.
 * 输入一个文件名和字符串，统计这个字符串在这个文件中的次数
 *
 */
public final class StringUtils {

    // 工具类中的方法都是静态方式访问的因此将构造器私有不允许创建对象(绝对好习惯)
    private StringUtils(){
        throw new AssertionError();
    }

    public static int countWordInFile(String filename, String word){
        int counter = 0;
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                int index = -1;
                while (line.length() >= word.length() && (index = line.indexOf(word)) >= 0) {
                    counter++;
                    line = line.substring(index + word.length());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {

        }
        return counter;
    }

    /**
     * 列出一个目录下的所有文件
     * @param f
     */
    public static void showDirectory(File f){
        _waikDirectory(f, 0);
    }

    private static void _waikDirectory(File f, int level){
        if (f.isDirectory()){
            for (File temp: f.listFiles()){
                _waikDirectory(temp, level + 1);
            }
        }else{
            for (int i = 0; i < level; i++){
                System.out.print("\t");
            }
            System.out.println(f.getName());
        }

    }
    public static void main(String[] args) {
        String filepath = "E:\\test\\c.txt";
        System.out.println(countWordInFile(filepath, "hello"));
        String path = "E:\\test";
        File file = new File(path);
        showDirectory(file);
    }
}
