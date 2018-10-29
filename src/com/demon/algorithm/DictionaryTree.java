package com.demon.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 字典树
 * 应用场景：大量字符串的统计和查找；字符串前缀的匹配
 * 优点：节省空间，提高遍历效率
 * 
 * @author xuliang
 * @since 2018年10月26日 下午5:39:05
 *
 */
public class DictionaryTree {

    // 字典树的节点
    private class Node {
        // 是否是单词
        private boolean isWord;
        // 单词计数
        private int count;
        // 字符串
        private String str;
        // 子节点
        private Map<String, Node> childs;
        // 父节点
        private Node parent;
        
        public Node() {
            childs = new HashMap<>();
        }
        
        public Node(boolean isWord, int count, String str) {
            this();
            this.isWord = isWord;
            this.count = count;
            this.str = str;        
        }
        
        public void addChild(String key, Node node) {
            childs.put(key, node);
            node.parent = this;
        }
        
        public void removeChild(String key) {
            childs.remove(key);
        }

        @Override
        public String toString() {
            return "Node [isWord=" + isWord + ", count=" + count + ", str=" + str + "]";
        }
        
    }
    
    // 根节点
    private Node root;
    
    public DictionaryTree() {
        root = new Node();
    }
    
    /**
     * 在节点上添加字符串
     * @param word 待添加的字符串
     * @param node 待添加的节点
     */
    private void addStr(String word, Node node){
        node.count++;
        String str = node.str;
        if(str != null){
            // 查找公共前缀
            String commonPrefix = "";
            for(int i=0;i<word.length();i++){
                if(str.length() > i && word.charAt(i) == str.charAt(i)){
                    commonPrefix += word.charAt(i);
                }else{
                    break;
                }
            }
            
            if(commonPrefix.length() > 0){
                if(commonPrefix.length() == str.length() && commonPrefix.length() == word.length()){
                    // 重复的词
                }else if(commonPrefix.length() == str.length() && commonPrefix.length() < word.length()){
                    // 剩余的字符串
                    String remainingWord = word.substring(commonPrefix.length());
                    // 剩余的串在子节点中继续找
                    childAddStr(remainingWord, node);
                    
                }else if(commonPrefix.length() < str.length()){
                    // 节点裂变
                    Node splitNode = new Node(true, node.count, commonPrefix);
                    // 处理节点间的关系
                    splitNode.parent = node.parent;
                    splitNode.parent.addChild(commonPrefix, splitNode);
                    node.parent.removeChild(node.str);
                    node.count--;
                    // 节点裂变后剩余的字符串
                    String remainingStr = node.str.substring(commonPrefix.length());
                    node.str = remainingStr;
                    splitNode.addChild(remainingStr, node);
                    // 单词裂变后剩余的字符串
                    if(commonPrefix.length() < word.length()){
                        splitNode.isWord = false;
                        String remainingWord = word.substring(commonPrefix.length());
                        Node remainingNode = new Node(true, 1, remainingWord);
                        splitNode.addChild(remainingWord, remainingNode);
                    }
                }
            }else{
                // 没有共同前缀，直接添加节点
                Node newNode = new Node(true, 1, word);
                node.addChild(word, newNode);
            }
        }else{
            // 根节点
            if(node.childs.size() > 0){
                childAddStr(word, node);
            }else{
                Node newNode = new Node(true, 1, word);
                node.addChild(word, newNode);
            }
        }
    }
    
    /**
     * 在子节点中添加字符串
     * @param word 待添加的字符串
     * @param node 待添加的节点
     */
    private void childAddStr(String word, Node node) {
        boolean isFind = false;
        if(node.childs.size() > 0){
            // 遍历子节点
            for(Map.Entry<String, Node> entry: node.childs.entrySet()){
                Node childNode = entry.getValue();
                // 首字母相同，则在该节点中继续添加字符串
                if(word.charAt(0) == childNode.str.charAt(0)){
                    isFind = true;
                    addStr(word, childNode);
                    break;
                }
            }
        }
        // 没有首字母相同的子节点，则将其变为子节点
        if(!isFind){
            Node newNode = new Node(true, 1, word);
            node.addChild(word, newNode);
        }
    }
    
    /**
     * 添加单词
     * @param word 待添加的单词
     */
    public void add(String word){
        addStr(word, root);
    }
    
    /**
     * 在节点中查找字符串
     * @param word 待查找的字符串
     * @param node 待查找的节点
     */
    private boolean findStr(String word, Node node){
        boolean isMatch = true;
        String remaining = word;
        String str = node.str;
        if(str != null){
            if(word.indexOf(str) != 0){
                // 字符串与单词不匹配
                isMatch = false;
            }else{
                // 匹配，则计算剩余单词
                remaining = word.substring(str.length());
            }
        }
        if(isMatch){
            if(remaining.length() > 0){
                // 如果还有剩余单词，遍历子节点继续查找
                for(Map.Entry<String, Node> entry: node.childs.entrySet()){
                    Node childNode = entry.getValue();
                    boolean isChildFind = findStr(remaining, childNode);
                    if(isChildFind){
                        return true;
                    }
                }
            }else{
                // 如果没有剩余单词长度，则表示已经匹配完成，直接返回节点是否单词
                return node.isWord;
            }
        }
        return false;
    }
    
    /**
     * 查找单词
     * @param word 待查找的单词
     */
    public boolean findWord(String word){
        return findStr(word, root);
    }
    
    /**
     * 统计子节点单词数
     * @param prefix 单词前缀
     * @param node 节点
     */
    private int countChildStr(String prefix, Node node){
        for(Map.Entry<String, Node> entry: node.childs.entrySet()){
            Node childNode = entry.getValue();
            int childCount = countStr(prefix, childNode);
            if(childCount != 0){
                return childCount;
            }
        }
        return 0;
    }
    
    /**
     * 统计节点单词数
     * @param prefix 单词前缀
     * @param node 节点
     */
    private int countStr(String prefix, Node node) {
        String str = node.str;
        if(str != null){
            if(prefix.indexOf(str) != 0 && str.indexOf(prefix) != 0){
                // 前缀与字符串不匹配
                return 0;
            }else if(str.indexOf(prefix) == 0){
                // 前缀匹配字符串，且前缀较短
                return node.count;
            }else if(prefix.indexOf(str) == 0){
                // 前缀匹配字符串，且前缀较长，继续匹配剩余字符串
                String remaining = prefix.substring(str.length());
                if(remaining.length() > 0){
                    return countChildStr(remaining, node);
                }
            }
        }else{
            // 根节点，直接在子节点中查找
            return countChildStr(prefix, node);
        }
        return 0;
    }
    
    /**
     * 统计单词书
     * @param prefix 单词前缀
     */
    public int count(String prefix){
        // 特殊情况
        if(prefix == null || prefix.trim().isEmpty()){
            return root.count;
        }
        // 从根节点遍历匹配
        return countStr(prefix, root);
    }
    
    /**
     * 打印节点信息
     * @param node 节点
     * @param layer 节点层级
     */
    private void printNode(Node node, int layer){
        for(int i=0;i<layer;i++){
            System.out.print(" ");
        }
        System.out.println(node);
        // 递归打印子节点
        for(Map.Entry<String, Node> entry: node.childs.entrySet()){
            printNode(entry.getValue(), layer + 1);
        }
    }
    
    /**
     * 打印字典树
     */
    public void print(){
        printNode(root, 0);
    }
    
    public static void main(String[] args) {
        DictionaryTree tree = new DictionaryTree();
        tree.add("interest");
        tree.add("interesting");
        tree.add("interested");
        tree.add("int");
        tree.add("test");
        tree.add("inter");
        tree.add("inside");
        tree.add("insert");
        
        tree.add("中文测试");
        tree.add("中文");
        tree.add("中");
        tree.add("文");
        tree.add("文");
        
        tree.print();
        
        boolean findWord = tree.findWord("inside");
        System.out.println("find inside: " + findWord);
    }
    
}
