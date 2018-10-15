package com.demon.algorithm;

import java.util.LinkedList;
import java.util.List;

/**
 * 红包算法
 * 
 * @author xuliang
 * @since 2018年10月15日 下午2:03:21
 *
 */
public class RedPackage {

    /**
     * 最大的红包倍数因子，默认为2倍，即最大红包金额为平均值的2倍
     */
    private static final double DEFAULT_FACTOR = 2;
    
    /**
     * 默认的最小红包金额
     */
    private static final int DEFAULT_MIN_MONEY = 1;
    
    private int totalMoney;
    private int count;
    private List<Integer> results;
    
    private double factor;
    private int minMoney;
    private int maxMoney;
    
    private int retryCount = 0;
    
    public static RedPackage build(){
        return new RedPackage();
    }
    
    /**
     * <pre>
     * 默认的红包构造器
     * 最大的红包倍数因子，默认为2倍，即最大红包金额为平均值的2倍
     * 最小红包金额，默认是1分
     * </pre>
     * @param totalMoney 总金额，单位：分
     * @param count 总红包个数
     */
    public RedPackage initDefault(int totalMoney, int count){
        // TODO validate input params
        this.totalMoney = totalMoney;
        this.count = count;
        this.factor = DEFAULT_FACTOR;
        this.minMoney = DEFAULT_MIN_MONEY;
        this.maxMoney = (int)((totalMoney / count) * this.factor);
        results = new LinkedList<>();
        return this;
    }
    
    /**
     * 自定义红包构造器
     * @param totalMoney 总金额，单位：分
     * @param count 总红包个数
     * @param factor 最大的红包倍数因子，即最大红包金额为平均值的N倍。maxMoney 有值，则次参数不启作用
     * @param minMoney 最小红包金额，单位：分。minMoney 为0，则取默认值
     * @param maxMoney 最大红包金额，单位：分。maxMoney 大于0，则factor 参数不启作用
     * @return
     */
    public RedPackage initCustom(int totalMoney, int count, double factor, int minMoney, int maxMoney){
        // TODO validate input params
        this.totalMoney = totalMoney;
        this.count = count;
        this.factor = factor < 1 ? DEFAULT_FACTOR : factor;
        this.minMoney = minMoney <= 0 ? DEFAULT_MIN_MONEY : minMoney;
        if(maxMoney > 0){
            this.maxMoney = maxMoney;
        }else{
            this.maxMoney = (int)((totalMoney / count) * this.factor);
        }
        results = new LinkedList<>();
        return this;
    }
    
    public RedPackage generate(){
        int totalMoney = this.totalMoney;
        for(int i=0;i<this.count;i++){
            int money = generateRedPacket(totalMoney, this.minMoney, this.maxMoney, this.count - i);
            this.results.add(money);
            totalMoney -= money;
            System.out.println((i+1)+" 次红包: "+ money);
        }
        return this;
    }
    
    private int generateRedPacket(int totalMoney, int minMoney, int maxMoney, int count){
        if(count == 1){
            return totalMoney;
        }
        if(minMoney == maxMoney){
            return minMoney;
        }
        maxMoney = maxMoney > totalMoney ? totalMoney : maxMoney;
        int money = (int)(Math.random() * (maxMoney - minMoney) + minMoney);
        int lastMoney = totalMoney - money;
        Status checkMoney = checkMoney(lastMoney, count - 1);
        if(Status.OK == checkMoney){
            return money;
        }else if(Status.LESS == checkMoney){
            // 剩余金额的平均值小于最小金额，则表示生成的红包金额过大，则将生成的红包金额作为最大金额重新生成红包金额
            retryCount++;
            System.out.println("retryCount=" + retryCount);
            return generateRedPacket(totalMoney, minMoney, money, count);
        }else if(Status.MORE == checkMoney){
            // 剩余金额的平均值大于最大金额，则表示生成的红包金额过小，则将生成的红包金额作为最小金额重新生成红包金额
            retryCount++;
            System.out.println("retryCount=" + retryCount);
            return generateRedPacket(totalMoney, money, maxMoney, count);
        }
        return money;
    }
    
    private Status checkMoney(int lastMoney, int count){
        int avg = lastMoney / count;
        if(avg < minMoney){
            return Status.LESS;
        }
        if(avg > maxMoney){
            return Status.MORE;
        }
        return Status.OK;
    }
    
    public int getTotalMoney() {
        return totalMoney;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getResults() {
        return results;
    }

    public double getFactor() {
        return factor;
    }

    public int getMinMoney() {
        return minMoney;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    private static enum Status {
        LESS,
        MORE,
        OK,
        ;
    }
    
    public static void main(String[] args) {
//        RedPackage instance = RedPackage.build().initDefault(1000, 20).generate();  // [2, 101, 9, 20, 94, 48, 35, 85, 76, 51, 62, 2, 32, 48, 33, 95, 89, 34, 50, 34]
        RedPackage instance = RedPackage.build().initCustom(1000, 20, 0, 15, 100).generate();
//        RedPackage instance = RedPackage.build().initCustom(1000, 20, 2.5, 0, 0).generate();
        System.out.println(instance.getTotalMoney());
        System.out.println(instance.getCount());
        System.out.println(instance.getMinMoney());
        System.out.println(instance.getMaxMoney());
        System.out.println(instance.getResults());
        int sum = 0;
        for(int r : instance.getResults()){
            sum += r;
        }
        System.out.println("sum="+sum);
    }
    
}
