package com.demon.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author xuliang
 * @since 2018年3月8日 上午10:08:35
 *
 */
public class RandomTest {

    public static void main(String[] args) {
        List<Item> items = new ArrayList<>();
        Item A = new Item("A", 15);
        Item B = new Item("B", 5);
        Item C = new Item("C", 32.16);
        Item D = new Item("D", 22.2);
        Item E = new Item("E", 26.64);
        items.add(A);
        items.add(B);
        items.add(C);
        items.add(D);
        items.add(E);
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return (int) (o1.prop - o2.prop);
            }
        });
        System.out.println("元数据："+ JSON.toJSONString(items, true));
        
        probDynamic(items, -2);
        System.out.println("概率动态化："+JSON.toJSONString(items, true));
        
        /*
        probDistribute(items);
        System.out.println("概率分布：" + JSON.toJSONString(items, true));
        
        // 根据概率，获取中奖物品
        HashMap<String, Integer> statMap = new HashMap<>();
        statMap.put("A", 0);
        statMap.put("B", 0);
        statMap.put("C", 0);
        statMap.put("D", 0);
        statMap.put("E", 0);
        for(int i=0;i<1000;i++){
            double random = Math.random();
            if(i % 100 == 0){
                System.out.println(random);
            }
            for(Item item: items){
                if(item.start == item.end){
                    continue;
                }
                if(random >= item.start && random < item.end){
                    if(i % 100 == 0){
                        System.out.println(JSON.toJSON(item));
                    }
                    statMap.put(item.name, statMap.get(item.name).intValue() + 1);
                }
            }
        }
        System.out.println("1000次中奖结果："+JSON.toJSON(statMap));
        
        System.out.println("-------------------------------------------");
        items.remove(0);
        items.remove(2);
        System.out.println("概率转化元数据："+JSON.toJSONString(items));
        probTransform(items);
        probDistribute(items);
        System.out.println("概率分布：" + JSON.toJSONString(items, true));*/
    }
    
    /**
     * 概率分布：根据概率，在 0~1 之间，计算分布区间
     */
    public static void probDistribute(List<Item> items){
        double start = 0;
        double end = 0;
        for(int i=0; i< items.size(); i++){
            Item item = items.get(i);
            if(i == 0){
                // 第一个
                start = 0;
                end = item.prop / 100;
            }else{
                start = end;
                end = end + item.prop / 100;
            }
            item.start = start;
            item.end = end;
        }
    }
    
    /**
     * 概率转化：概率总和不为100%时，根据各自的概率，转化为总和100%的概率值
     */
    public static void probTransform(List<Item> items){
        double defaultTotal = 100;
        double total = 0;
        for(Item item: items){
            total += item.prop;
        }
        double rate = total / defaultTotal;
        System.out.println("当前总概率："+total+" 转化比率："+rate);
        
        for(Item item: items){
            double result = item.prop / rate;
            System.out.println(item.name + " 转化后概率：" + result);
            item.prop = result;
        }
    }
    
    /**
     * 概率动态化：根据原始物品概率和玩家幸运因子，计算概率的最终值
     */
    public static void probDynamic(List<Item> items, double factor){
        double temp = factor;
        // 从概率小的开始，减去幸运因子
        for(int i=0; i<items.size(); i++){
            Item item = items.get(i);
            double d = item.prop - temp;
            if(d == 0 || d == 100){
                item.prop = d;
                break;
            }else if(d < 0){
                temp = temp - item.prop;
                item.prop = 0;
            }else if(d > 100){
                hundredPerCent(items, item);
                return;
            }else{
                item.prop = d;
                break;
            }
        }
        temp = factor;
        // 从概率大的开始，加上幸运因子
        for(int i=items.size()-1; i>=0; i--){
            Item item = items.get(i);
            double d = item.prop + temp;
            if(d == 0 || d == 100){
                item.prop = d;
                break;
            }else if(d < 0){
                temp = temp + item.prop;
                item.prop = 0;
            }else if(d > 100){
                hundredPerCent(items, item);
                return;
            }else{
                item.prop = d;
                break;
            }
        }
    }
    
    private static void hundredPerCent(List<Item> items, Item item){
        for(Item it: items){
            if(it.name.equals(item.name)){
                it.prop = 100;
            }else{
                it.prop = 0;
            }
        }
    }
    
    static class Item {
        public String name;
        public double prop;
        public double start;
        public double end;
        
        public Item(String name, double prop) {
            this.name = name;
            this.prop = prop;
        }
        
    }
    
}
