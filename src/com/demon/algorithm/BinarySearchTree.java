package com.demon.algorithm;

import java.util.LinkedList;

/**
 * 二叉搜索树
 * 
 * @author xuliang
 * @since 2018年11月15日 上午11:48:43
 *
 */
public class BinarySearchTree {

    /**
     * @author xuliang
     * @since 2018年11月15日 下午3:02:52
     *
     */
    class Node {
        public int key;
        public Object data;
        public Node parent;
        public Node leftChild;
        public Node rightNode;
        
        public Node() {
        }
        
        public Node(int key, Object data) {
            this.key = key;
            this.data = data;
        }
        
        @Override
        public String toString() {
            return "Node ["+key+":"+data+"]";
        }

        public void displayNode() {
            System.out.println("Node ["+key+":"+data+"]");
        }
    }
    
    // 根节点
    private Node root;
    
    public BinarySearchTree() {
        root = null;
    }
    
    /**
     * 二叉搜索树查找的时间复杂度为O(logN)
     * @param key 给定的key
     * @return 查找的结果
     */
    public Node find(int key){
        Node current = root;
        if(current == null){
            return null;
        }
        while(current.key != key){
            if(key < current.key){
                current = current.leftChild;
            }else{
                current = current.rightNode;
            }
            if(current == null){
                return null;
            }
        }
        return null;
    }
    
    /**
     * 插入数据
     * @param key
     * @param value
     */
    public void insert(int key, Object data){
        Node node = new Node(key, data);
        if(root == null){
            root = node;
            return;
        }
        Node current = root;
        Node parent;
        for(;;){
            parent = current;
            if(key < current.key){
                current = current.leftChild;
                if(current == null){
                    parent.leftChild = node;
                    node.parent = parent;
                    return;
                }
            }else{
                current = current.rightNode;
                if(current == null){
                    parent.rightNode = node;
                    node.parent = parent;
                    return;
                }
            }
        }
    }
    
    /**
     * <pre>
     * 删除节点，主要分为三种情况：
     * 1、该节点没有子节点
     * 2、该节点有一个子节点
     * 3、该节点有两个子节点
     * 删除节点的时间复杂度是O(logN)
     * 
     * 避免删除的处理：
     * Node 增加字段来标识该节点是否删除，其他操作会先判断该节点是否删除，这样删除不会改变树的结构，但是会对存储造成浪费，适用于删除操作较少的情况
     * </pre>
     * @param key
     * @return
     */
    public boolean remove(int key){
        Node current = root;
        if(current == null){
            return false;
        }
        boolean isLeftChild = true;
        // 查找要删除的节点
        while(current.key != key){
            if(key < current.key){
                isLeftChild = true;
                current = current.leftChild;
            }else{
                isLeftChild = false;
                current = current.rightNode;
            }
            if(current == null){
                return false;
            }
        }
        
        if(current.leftChild == null && current.rightNode == null){
            // 1、要删除的节点没有子节点
            return removeNodeWithNoChild(current, isLeftChild);
        }else if(current.leftChild != null && current.rightNode != null){
            // 3、该节点有两个子节点
            return removeNodeWithTwoChild(current, isLeftChild);
        }else{
            // 2、该节点有一个子节点
            return removeNodeWithOneChild(current, isLeftChild);
        }
    }
    
    /**
     * 删除无子节点的节点
     * @param node 待删除的节点
     * @param isLeftChild 是否是左子节点
     */
    private boolean removeNodeWithNoChild(Node node, boolean isLeftChild) {
        if(node == root){
            root = null;
            return true;
        }
        if(isLeftChild){
            node.parent.leftChild = null;
        }else{
            node.parent.rightNode = null;
        }
        return true;
    }
    
    /**
     * 删除一个子节点的节点
     * @param node 待删除的节点
     * @param isLeftChild 是否是左子节点
     */
    private boolean removeNodeWithOneChild(Node node, boolean isLeftChild) {
        if(node.leftChild == null){
            if(node == root){
                root = node.rightNode;
                node.parent = null;
                return true;
            }
            if(isLeftChild){
                node.parent.leftChild = node.rightNode;
            }else{
                node.parent.rightNode = node.rightNode;
            }
            node.rightNode.parent = node.parent;
        }else{
            if(node == root){
                root = node.leftChild;
                node.parent = null;
                return true;
            }
            if(isLeftChild){
                node.parent.leftChild = node.leftChild;
            }else{
                node.parent.rightNode = node.leftChild;
            }
            node.leftChild.parent = node.parent;
        }
        return true;
    }
    
    /**
     * 删除两个子节点的节点。
     */
    private boolean removeNodeWithTwoChild(Node node, boolean isLeftChild) {
        Node successor = getSuccessor(node);
        if(node == root){
            successor.leftChild = root.leftChild;
            successor.rightNode = root.rightNode;
            successor.parent = null;
            root = successor;
        }else if(isLeftChild){
            node.parent.leftChild = successor;
        }else{
            node.parent.rightNode = successor;
        }
        successor.leftChild = node.leftChild;
        return true;
    }
    
    /**
     * <pre>
     * 获得要删除节点的后继节点，中序后继来代替要删除的节点
     * 首先得找到要删除的节点的右子节点，它的关键字值一定比待删除节点的大。
     * 然后转到待删除节点右子节点的左子节点那里（如果有的话），然后到这个左子节点的左子节点，
     * 以此类推，顺着左子节点的路径一直向下找，这个路径上的最后一个左子节点就是待删除节点的后继。
     * 如果待删除节点的右子节点没有左子节点，那么这个右子节点本身就是后继。
     * </pre>
     */
    private Node getSuccessor(Node delNode){
        Node successor = delNode;
        Node current = delNode.rightNode;
        while(current != null){
            successor = current;
            current = current.leftChild;
        }
        if(successor != delNode.rightNode){
            successor.parent.leftChild = successor.rightNode;
            if(successor.rightNode != null){
                successor.rightNode.parent = successor.parent;
            }
            successor.rightNode = delNode.rightNode;
        }
        return successor;
    }
    
    /**
     * 查找最小值，二叉排序树最小值的节点是左边没有子节点的节点
     */
    public Node minNode(){
        Node current = root;
        Node parent = root;
        while(current != null){
            parent = current;
            current = current.leftChild;
        }
        return parent;
    }
    
    /**
     * 查找最大值，二叉排序树最大值的节点是右边没有子节点的节点
     */
    public Node maxNode(){
        Node current = root;
        Node parent = root;
        while(current != null){
            parent = current;
            current = current.rightNode;
        }
        return parent;
    }
    
    /**
     * 遍历二叉树
     * @param type 遍历方式 {@link TraverseType}
     */
    public void traverse(TraverseType type){
        switch (type) {
            case Level:
                System.out.println("Level traverse:");
                levelTraverse(root);
                break;
            case Preorder:
                System.out.println("Preorder traverse:");
                preorderTraverse(root);
                break;
            case Inorder:
                System.out.println("Inorder traverse:");
                inorderTraverse(root);
                break;
            case Postorder:
                System.out.println("Postorder traverse:");
                postorderTraverse(root);
                break;
            default:
                System.out.println("Preorder traverse:");
                preorderTraverse(root);
                break;
        }
    }
    
    /**
     * 层序遍历，按二叉树从上到下，从左到右遍历
     */
    private void levelTraverse(Node node) {
        if(node == null){
            return;
        }
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(node);
        Node current;
        while(!queue.isEmpty()){
            current = queue.poll();
            System.out.println("current data:" + current.toString());
            
            if(current.leftChild != null){
                queue.add(current.leftChild);
            }
            if(current.rightNode != null){
                queue.add(current.rightNode);
            }
        }
    }
    
    /**
     * 先序遍历，首先访问根节点，然后遍历左子树，最后遍历右子树
     */
    private void preorderTraverse(Node node) {
        if(node == null){
            return;
        }
        System.out.println("current data:" + node.toString());
        preorderTraverse(node.leftChild);
        preorderTraverse(node.rightNode);
    }
    
    /**
     * 中序遍历，首先遍历左子树，然后访问根节点，最后遍历右子树
     */
    private void inorderTraverse(Node node) {
        if(node == null){
            return;
        }
        inorderTraverse(node.leftChild);
        System.out.println("current data:" + node.toString());
        inorderTraverse(node.rightNode);
    }
    
    /**
     * 后序遍历，首先遍历左子树，然后遍历右子树，最后访问根节点
     */
    private void postorderTraverse(Node node) {
        if(node == null){
            return;
        }
        postorderTraverse(node.leftChild);
        postorderTraverse(node.rightNode);
        System.out.println("current data:" + node.toString());
    }
    
    /**
     * Level, Preorder, Inorder, Postorder
     * @author xuliang
     * @since 2018年11月15日 下午3:20:38
     *
     */
    enum TraverseType {
        Level,
        Preorder,
        Inorder,
        Postorder
        ;
    }
    
}
