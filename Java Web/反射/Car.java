/**
 * Created by 11981 on 2017/3/5.
 */
public class Car {
    private String brand;
    private String color;
    private int maxSpeed;

    public Car(){

    }

    public Car(String brand, String color, int maxSpeed){
        this.brand = brand;
        this.color = color;
        this.maxSpeed = maxSpeed;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void introduce(){
        System.out.println("brand:"+brand+";color:"+color+";maxSpeed"+maxSpeed);

    }
}
