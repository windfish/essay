package com.demon.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 深度优先搜索实现
 * 
 * <pre>
 * Q：九宫格内，1到8个数字
 *    —————————————
 *    | 3 | 4 | 1 |
 *    | 5 | 6 |   |
 *    | 8 | 2 | 7 |
 *    —————————————
 *   空格可以和上下左右数字交换，如果移动成
 *    —————————————
 *    | 1 | 2 | 3 |
 *    | 4 | 5 | 6 |
 *    | 7 | 8 |   |
 *    —————————————
 *   则游戏胜利
 * 
 * </pre>
 * 
 * @author xuliang
 * @since 2018年10月11日 上午10:05:30
 *
 */
public class HuaRongRoad_DepthFirst {
    
    // 定义移动方向
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int UP = 3;
    private static final int DOWN = 4;
    
    // 九宫格
    private int[][] array;
    
    // 记录空格位置
    private int x;
    private int y;
    
    /**
     *  定义移动的数组
     */
    private List<Integer> moveArray = new LinkedList<>();
    // 保存已经搜索过的状态
    private Set<Integer> statusSet = new HashSet<>();
    // 定义结果状态
    private static final Integer FINAL_STATUS = 123456780;
    
    /**
     * 初始化，0代表空格，先找出空格位置
     */
    public HuaRongRoad_DepthFirst(int[][] array) {
        this.array = array;
        for(int i=0;i<array.length;i++){
            for(int j=0;j<array.length;j++){
                if(array[i][j] == 0){
                    x = i;
                    y = j;
                    return;
                }
            }
        }
    }
    
    /**
     * 判断是否可以向某个方向移动
     * @param direction 移动的方向
     */
    private boolean canMove(int direction){
        switch(direction){
            case LEFT: return y > 0;
            case RIGHT: return y < 2;
            case UP: return x > 0;
            case DOWN: return x < 2;
            default: return false;
        }
    }
    
    /**
     * 朝某个方向移动，不判断是否可以移动，直接移动
     * @param direction 移动的方向
     */
    private void move(int direction){
        int temp;
        switch(direction){
            case LEFT:
                temp = array[x][y - 1];
                array[x][y - 1] = 0;
                array[x][y] = temp;
                y = y - 1;
                break;
            case RIGHT:
                temp = array[x][y + 1];
                array[x][y + 1] = 0;
                array[x][y] = temp;
                y = y + 1;
                break;
            case UP:
                temp = array[x - 1][y];
                array[x - 1][y] = 0;
                array[x][y] = temp;
                x = x - 1;
                break;
            case DOWN:
                temp = array[x + 1][y];
                array[x + 1][y] = 0;
                array[x][y] = temp;
                x = x + 1;
                break;
        }
        // 记录移动的方向
        moveArray.add(direction);
    }
    
    /**
     * 回退move 的操作，不判断是否可以移动，直接移动
     * @param direction 移动的方向 
     */
    private void moveBack(int direction){
        int temp;
        switch(direction){
            case LEFT:
                temp = array[x][y + 1];
                array[x][y + 1] = 0;
                array[x][y] = temp;
                y = y + 1;
                break;
            case RIGHT:
                temp = array[x][y - 1];
                array[x][y - 1] = 0;
                array[x][y] = temp;
                y = y - 1;
                break;
            case UP:
                temp = array[x + 1][y];
                array[x + 1][y] = 0;
                array[x][y] = temp;
                x = x + 1;
                break;
            case DOWN:
                temp = array[x - 1][y];
                array[x - 1][y] = 0;
                array[x][y] = temp;
                x = x -1;
                break;
        }
        // 回滚记录的移动的方向
        moveArray.remove(moveArray.size() - 1);
    }
    
    /**
     * 获取九宫格状态，按顺序将9宫格数字组成一个整数来代表
     */
    private Integer getStatus(){
        int status = 0;
        for(int i=0;i<array.length;i++){
            for(int j=0;j<array.length;j++){
                status = status * 10 + array[i][j];
            }
        }
        return status;
    }
    
    /**
     * 深度优先搜索
     * 搜索逻辑：
     * 1、先记录下此次搜索的方向
     * 2、如果有路径可走，返回true；如果没路径可走，执行3
     * 3、回退此次搜索，将记录的方向删除
     * 4、返回false
     * 
     * @param direction 移动的方向
     */
    private boolean search(int direction){
        // 校验是否可以往这个方向移动
        if(canMove(direction) && moveArray.size() < 100){
            move(direction);
            // 移动后的状态
            Integer status = getStatus();
            // 若已经是最终结果，则返回true
            if(FINAL_STATUS.equals(status)){
                return true;
            }
            // 若是之前已经走过的状态，返回false
            if(statusSet.contains(status)){
                // 这一步走错了，回退这一步
                moveBack(direction);
                return false;
            }
            // 记录当前状态，并继续向四个方向移动，利用递归来持续移动和回退之前移动错的方向
            statusSet.add(status);
            boolean searchResult = search(LEFT) || search(RIGHT) || search(UP) || search(DOWN);
            if(searchResult){
                return true;
            }else{
                // 这一步走错，回退
                moveBack(direction);
                return false;
            }
        }
        return false;
    }
    
    public boolean entrance(){
        Integer status = getStatus();
        if(FINAL_STATUS.equals(status)){
            return true;
        }
        // 初始状态先记录
        statusSet.add(status);
        // 向四个方向移动
        return search(LEFT) || search(RIGHT) || search(UP) || search(DOWN);
    }
    
    public void printRoute(){
        for(int i=0;i<moveArray.size();i++){
            System.out.print(parseMove(moveArray.get(i)));
            System.out.print(" ");
        }
        System.out.println();
    }
    
    private String parseMove(int direction){
        switch (direction) {
            case LEFT: return "左";
            case RIGHT: return "右";
            case UP: return "上";
            case DOWN: return "下";
            default: return null;
        }
    }
    
    // 打印当前华容道的状态
    public void print() {
        for(int i=0; i<array.length; i++) {
            for(int j=0; j<array.length; j++) {
                System.out.print(array[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
//        int[][] array = {{1,2,3},{4,5,6},{0,7,8}};
        int[][] array = {{3,4,1},{5,6,0},{8,2,7}};
        HuaRongRoad_DepthFirst road = new HuaRongRoad_DepthFirst(array);
        road.print();
        try{
            if(road.entrance()){
                System.out.print("可以胜利，移动路径：");
                road.printRoute();
                road.print();
            }else{
                System.out.print("不能胜利，已移动路径：");
                road.printRoute();
                road.print();
            }
        }catch (Exception e) {
            road.print();
        }
    }
    
}
