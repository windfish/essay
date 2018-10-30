package com.demon.algorithm;

import java.util.BitSet;

/**
 * <pre>
 * bitmap 基础实现
 *
 * 二进制下，数字M，M * 2^N = M << N
 * 二进制下，数字M，M / 2^N = M >> N
 * 二进制下，数字M，M % 2^N = M & (2^N - 1)
 * </pre>
 * 
 * @author xuliang
 * @since 2018年10月30日 上午10:44:12
 *
 */
public class BitMap {

    private byte[] arr;
    /*容量，即最多能存多少个数据*/
    private int capacity;
    
    public BitMap(int capacity) {
        this.capacity = capacity;
        // 一个byte 可以存8个数据，容量实际上指多少个bit
        arr = new byte[(capacity / 8) + 1];
    }
    
    /**
     * 先找n 在arr 中的下标index，然后再找到n 在arr[index] 中的位置position<br>
     * index = n / 8 = n >> 3<br>
     * position = n % 8 = n & 0x07<br>
     * 然后把数字0 的position 位的值改为1（数字1 左移position 位），得到的值与arr[index] 做 “或（OR、|）”操作<br>
     */
    public void add(int n){
        int index = n >> 3;
        int position = n & 0x07;
        arr[index] |= 1 << position;
    }
    
    /**
     * 把数字0 的position 位的值改为1（数字1 左移position 位），然后取反，得到的值与arr[index] 做“与（AND、&）”操作
     */
    public void remove(int n){
        int index = n >> 3;
        int position = n & 0x07;
        arr[index] &= ~(1 << position);
    }
    
    /**
     * 数字1 左移position 位，然后与arr[index] 做“与（AND、&）”操作，如果结果不为0，则表示存在
     */
    public boolean constain(int n){
        int index = n >> 3;
        int position = n & 0x07;
        return (arr[index] & (1 << position)) != 0;
    }
    
    public int size(){
        return this.capacity;
    }
    
    public static void main(String[] args) {
        BitMap bitMap = new BitMap(16);
        bitMap.add(13);
        bitMap.add(1);
        System.out.println(bitMap.constain(13));
        System.out.println(bitMap.constain(12));
        System.out.println(bitMap.constain(1));
        bitMap.remove(1);
        System.out.println(bitMap.constain(1));
        
        System.out.println("---------------------------");
        // java 提供的
        BitSet bitSet = new BitSet();
        bitSet.set(1);
        System.out.println(bitSet.get(1));
    }
    
}
