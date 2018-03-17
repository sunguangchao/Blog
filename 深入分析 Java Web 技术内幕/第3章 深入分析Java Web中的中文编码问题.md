[TOC]
在Java中需要编码的场景
==========
在I/O操作中存在的编码
-------------
```java
public class CharSetTest {
    public static void main(String[] args) {
        try {
            String file = "F:\\Temp\\test.txt";
            File f = new File(file);
            if (!f.exists()){
                f.createNewFile();
            }
            String charset = "utf-8";
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
            writer.write("这是要保存的中文字符串");
            writer.close();

            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            StringBuffer sb = new StringBuffer();
            char[] buf = new char[64];
            int count = 0;
            try {
                while ((count = reader.read(buf)) != -1){
                    sb.append(buf, 0, count);
                }
            }finally {
                reader.close();
            }
            System.out.println(sb.toString());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
```

在内存中操作的编码
----------

```java
    public static void transfer() throws UnsupportedEncodingException{
        String s  = "这是要保存的中文字符串";
        byte[] b = s.getBytes("UTF-8");
        String n = new String(b, "UTF-8");
        System.out.println(n);

        Charset charSet = Charset.forName("UTF-8");
        ByteBuffer byteBuffer = charSet.encode(s);
        CharBuffer charBuffer = charSet.decode(byteBuffer);
        String result = charBuffer.toString();
        System.out.println(result);
    }
```


