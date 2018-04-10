package countdownlatch;

/**
 * Created by 11981 on 2018/4/10.
 */
public class Main {
    public static void main(String[] args) {
        boolean result = false;
        try {
            result = ApplicationStartupUtil.checkExternalSercices();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("External services validation completed !! Result was :: "+ result);
    }
}
