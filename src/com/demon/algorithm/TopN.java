package com.demon.algorithm;

import com.alibaba.fastjson.JSON;

/**
 * 小顶堆
 * 
 * @author xuliang
 * @since 2018年10月22日 下午5:18:16
 *
 */
public class TopN {

    /*
     * 数组模仿小顶堆，数组起始位置为堆的根节点
     * 0：根节点，左孩子1，右孩子2
     * 1：父节点0，左孩子3，右孩子4
     * 2：父节点0，左孩子5，右孩子6
     * 3：父节点1，左孩子7，右孩子8
     * 4：父节点1，左孩子9，右孩子10
     * 5：父节点2，左孩子11，右孩子12
     */
    
    // 父节点
    private int parent(int n){
        return (n -1) / 2;
    }
    
    // 左孩子
    private int left(int n){
        return n * 2 + 1;
    }
    
    // 右孩子
    private int right(int n){
        return n * 2 + 2;
    }
    
    /**
     * 构造小顶堆，每一个节点都比它的左右子节点要小
     * @param n 堆容量
     * @param data 数据
     */
    private void buildHeap(int n, int[] data){
        for(int i=1;i<n;i++){
            int t = i;
            while(t != 0 && data[parent(t)] > data[t]){
                int temp = data[t];
                data[t] = data[parent(t)];
                data[parent(t)] = temp;
                t = parent(t);
            }
        }
    }
    
    /**
     * 调整小顶堆
     * @param i 当前值
     * @param n 顶堆容量
     * @param data 数据
     */
    private void adjust(int i, int n, int[] data){
        if(data[i] <= data[0]){
            return;
        }
        // 置换堆顶
        int temp = data[i];
        data[i] = data[0];
        data[0] = temp;
        // 调整堆栈
        int t=0;
        // 若父节点比子节点大，那么需要调整
        while((left(t) < n && data[t] > data[left(t)])
                || (right(t) < n && data[t] > data[right(t)])){
            if(right(t) < n && data[right(t)] < data[left(t)]){
                // 右孩子比左孩子小，那么替换右孩子
                temp = data[t];
                data[t] = data[right(t)];
                data[right(t)] = temp;
                t = right(t);
            }else{
                // 替换左孩子
                temp = data[t];
                data[t] = data[left(t)];
                data[left(t)] = temp;
                t = left(t);
            }
        }
    }
    
    public void topN(int n, int[] data){
        // 构建小顶堆
        buildHeap(n, data);
        for(int i=n;i<data.length;i++){
            adjust(i, n, data);
        }
    }
    
    public static void main(String[] args) {
        TopN t = new TopN();
        
        int[] data1 = new int[]{23,45,67,8,9,3445,23,4356,67,879,9,123,22,345,345,67,89,890,890,234};
        System.out.println("源数据："+JSON.toJSONString(data1));
        t.topN(7, data1);
        System.out.println("调整后数据："+JSON.toJSONString(data1));
    }
    
}
