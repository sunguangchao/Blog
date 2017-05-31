package entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 11981 on 2017/5/30.
 * 购物车类
 */
public class Cart {
    //购买商品的集合
    private HashMap<Items, Integer>  goods;
    private double totalPrice;
    //显式初始化
    public Cart(){
        goods = new HashMap<Items,Integer>();
        totalPrice = 0.0;
    }

    public HashMap<Items, Integer> getGoods() {
        return goods;
    }

    public void setGoods(HashMap<Items, Integer> goods) {
        this.goods = goods;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    //添加商品进购物车的方法
    public boolean addGoodsInCart(Items item, int number){
        if (goods.containsKey(item)){
            goods.put(item, goods.get(item)+number);
        }else{
            goods.put(item, number);
        }
        calTotalPrice();//重新计算购物车总金额
        return true;
    }

    public boolean removeGoodsFromCart(Items item){
        goods.remove(item);
        calTotalPrice();
        return true;
    }
    //计算购物车总金额
    public double calTotalPrice(){
        double sum = 0.0;
        //迭代器
        Set<Items> keys = goods.keySet();
        Iterator<Items> it = keys.iterator();
        while (it.hasNext()){
            Items i = it.next();
            sum += i.getPrice() * goods.get(i);
        }
        this.setTotalPrice(sum);
        return this.totalPrice;
    }

    //测试用例
    public static void main(String[] args){
        Items i1 = new Items(1, "沃特篮球鞋", "温州", 200, 500, "001.jpg");
        Items i2 = new Items(2, "李宁篮球鞋", "广州", 300, 500, "002.jpg");
        Items i3 = new Items(1, "沃特篮球鞋", "温州", 200, 500, "001.jpg");

        Cart c = new Cart();
        c.addGoodsInCart(i1,1);
        c.addGoodsInCart(i2,2);
        c.addGoodsInCart(i3,3);


        Set<Map.Entry<Items, Integer>> items = c.getGoods().entrySet();
        for (Map.Entry<Items, Integer> obj:items){
            System.out.println(obj);
        }

        System.out.println("购物车的总金额："+c.getTotalPrice());
    }
}
