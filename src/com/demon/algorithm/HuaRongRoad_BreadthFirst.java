package com.demon.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;

/**
 * 广度优先搜索实现
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
public class HuaRongRoad_BreadthFirst {
    
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
     * 代表广度优先搜索的每一步，通过lastItem 链接起来
     * @author xuliang
     * @since 2018年10月11日 下午4:39:28
     *
     */
    private class SearchItem {
        private Integer status;
        private Integer direction;
        private SearchItem lastItem;
        
        SearchItem(Integer status, Integer direction, SearchItem lastItem) {
            this.status = status;
            this.direction = direction;
            this.lastItem = lastItem;
        }

        public Integer getStatus() {
            return status;
        }
        public Integer getDirection() {
            return direction;
        }
        public SearchItem getLastItem() {
            return lastItem;
        }
        
    }
    
    // 广度优先搜索的存储队列
    private List<SearchItem> statusToSearch = new LinkedList<>();
    
    public HuaRongRoad_BreadthFirst(int[][] array) {
        this.array = array;
        getXY();
    }
    
    /**
     * 0代表空格，先找出空格位置
     */
    private void getXY(){
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
    }
    
    /**
     * 向某个方向的移动，不校验是否可移动
     * @param direction 移动的方向
     */
    private void moveForward(int direction){
        move(direction);
        moveArray.add(direction);
    }
    
    /**
     * 向某个方向的回退，与moveForward 相反的操作，不校验是否可移动
     * @param direction
     */
    private void moveBack(int direction){
        move(getBackDirection(direction));
        moveArray.remove(moveArray.size() - 1);
    }
    
    /**
     * 获取移动的相反方向
     * @param direction 移动的方向
     */
    private Integer getBackDirection(int direction){
        switch(direction){
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            case UP: return DOWN;
            case DOWN: return UP;
            default: return 0;
        }
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
     * 广度优先搜索
     * 1、将当前位置写入队列中
     * 2、从队列中取出位置信息，判断是否完成，若未完成，则向四个方向移动
     * 3、在可以移动的方向，校验是否是走过的路径，若不是，则将位置信息放入队列，等待再次执行2
     * 4、若是，则不处理
     */
    private boolean search(){
        while(statusToSearch.size() > 0){
            SearchItem item = statusToSearch.remove(0);
            Integer status = item.getStatus();
            if(FINAL_STATUS.equals(status)){
                System.out.println(JSON.toJSONString(item));
                recordRoute(item);
                return true;
            }
            // 九宫格恢复为当前搜索时的情况
            recoverStatus(status);
            // 向四个方向进行遍历
            for(int i=1;i<=4;i++){
                if(canMove(i)){
                    moveForward(i);
                    status = getStatus();
                    if(statusSet.contains(status)){
                        moveBack(i);
                        continue;
                    }
                    statusSet.add(status);
                    statusToSearch.add(new SearchItem(status, i, item));
                    moveBack(i);
                }
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
        statusToSearch.add(new SearchItem(status, null, null));
        return search();
    }
    
    /**
     * 根据链表记录移动路径
     */
    private void recordRoute(SearchItem item){
        while(item.getLastItem() != null){
            moveArray.add(0, item.getDirection());
            item = item.getLastItem();
        }
    }
    
    /**
     * 根据九宫格状态，恢复九宫格
     */
    private void recoverStatus(Integer status){
        for(int i=array.length-1;i>=0;i--){
            for(int j=array.length-1;j>=0;j--){
                array[i][j] = status % 10;
                status = status / 10;
            }
        }
        getXY();
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
        HuaRongRoad_BreadthFirst road = new HuaRongRoad_BreadthFirst(array);
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
