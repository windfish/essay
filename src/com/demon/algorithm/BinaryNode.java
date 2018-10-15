package com.demon.algorithm;

import java.util.LinkedList;

import com.alibaba.fastjson.JSON;

/**
 * 二叉树遍历
 * 
 * @author xuliang
 * @since 2018年10月15日 下午3:49:09
 *
 */
public class BinaryNode {

    private Object data;
    private BinaryNode left;
    private BinaryNode right;
    
    public BinaryNode() {
    }
    
    public BinaryNode(Object data, BinaryNode left, BinaryNode right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public BinaryNode getLeft() {
        return left;
    }

    public void setLeft(BinaryNode left) {
        this.left = left;
    }

    public BinaryNode getRight() {
        return right;
    }

    public void setRight(BinaryNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinaryNode [data=" + data + ", left=" + left + ", right=" + right + "]";
    }
    
    /**
     * 二叉树层序遍历，按二叉树从上到下，从左到右遍历
     */
    public static void levelTraversal(BinaryNode node){
        LinkedList<BinaryNode> queue = new LinkedList<>();
        queue.add(node);
        BinaryNode current;
        while(!queue.isEmpty()){
            current = queue.poll();
            System.out.println("current data: "+current.getData());
            
            if(current.getLeft() != null){
                queue.add(current.getLeft());
            }
            if(current.getRight() != null){
                queue.add(current.getRight());
            }
        }
    }
    
    /**
     * 二叉树先序遍历，首先访问根结点然后遍历左子树，最后遍历右子树
     */
    public static void preorderTraversal(BinaryNode node){
        if(node == null){
            return;
        }
        System.out.println("current data: "+node.getData());
        preorderTraversal(node.getLeft());
        preorderTraversal(node.getRight());
    }
    
    /**
     * 二叉树中序遍历，首先遍历左子树，然后访问根结点，最后遍历右子树
     */
    public static void inorderTraversal(BinaryNode node){
        if(node == null){
            return;
        }
        inorderTraversal(node.getLeft());
        System.out.println("current data: "+node.getData());
        inorderTraversal(node.getRight());
    }
    
    /**
     * 二叉树后序遍历，首先遍历左子树，然后遍历右子树，最后访问根结点
     */
    public static void postorderTraversal(BinaryNode node){
        if(node == null){
            return;
        }
        postorderTraversal(node.getLeft());
        postorderTraversal(node.getRight());
        System.out.println("current data: "+node.getData());
    }
    
    public static BinaryNode createNode(){
        BinaryNode node = new BinaryNode("1",null,null) ;
        BinaryNode node2 = new BinaryNode("2",null,null) ;
        BinaryNode node3 = new BinaryNode("3",null,null) ;
        BinaryNode node4 = new BinaryNode("4",null,null) ;
        BinaryNode node5 = new BinaryNode("5",null,null) ;
        BinaryNode node6 = new BinaryNode("6",null,null) ;
        node.setLeft(node2) ;
        node2.setLeft(node4);
        node2.setRight(node6);
        node.setRight(node3);
        node3.setRight(node5) ;
        return node ;
    }
    
    public static void main(String[] args) {
        BinaryNode node = BinaryNode.createNode();
        System.out.println(JSON.toJSONString(node, true));
        
        System.out.println("------层序遍历---------");
        BinaryNode.levelTraversal(node);
        
        System.out.println("------先序遍历---------");
        BinaryNode.preorderTraversal(node);
        
        System.out.println("------中序遍历---------");
        BinaryNode.inorderTraversal(node);
        
        System.out.println("------后序遍历---------");
        BinaryNode.postorderTraversal(node);
    }
    
}
