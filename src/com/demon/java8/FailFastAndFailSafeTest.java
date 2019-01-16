package com.demon.java8;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 * ArrayList 删除remove 元素时，会修改modCount 列表修改次数；
 * 而创建Iterator 迭代器时，会将modCount 赋值给expectedModCount；
 * 在迭代过程中，会校验expectedModCount 是否等于modCount，若不相等，则会抛出ConcurrentModificationException 异常，迭代快速失败fail-fast；
 * 而使用Iterator 删除remove 元素时，会将迭代器的expectedModCount 置为modCount，因此不会fail-fast
 * </pre>
 * 
 * @author xuliang
 * @since 2018年2月27日 上午9:56:31
 *
 */
public class FailFastAndFailSafeTest {

    public static void main(String[] args) {
        failFast();
        fastSafe();
    }
    
    private static void failFast(){
        System.out.println("-------fail fast begin-------------");
        List<String> l = new ArrayList<String>();
        l.add("1");
        l.add("2");
        l.add("3");
        l.add("4");
        
        Iterator<String> iterator = l.iterator();
        int i=0;
        while(iterator.hasNext()){
            System.out.println(iterator.next());
            if(i == 1){
//                l.remove(iterator.next());    // list 的remove，会抛出ConcurrentModificationException
                iterator.remove();            // iterator 的remove，不会抛出ConcurrentModificationException，因为 ArrayList的迭代器，会将 expectedModCount = modCount
            }
            i++;
        }
        System.out.println("list size: " + l.size());
        System.out.println(l.toString());
        System.out.println("-------fail end begin-------------");
    }
    
    private static void fastSafe(){
        System.out.println("-------fail safe begin-------------");
        List<String> l = new CopyOnWriteArrayList<>();
        l.add("1");
        l.add("2");
        l.add("3");
        l.add("4");
        
        Iterator<String> iterator = l.iterator();
        int i=0;
        while(iterator.hasNext()){
            if(i == 1){
                l.remove(iterator.next());
            }
            System.out.println(iterator.next());
            i++;
        }
        System.out.println("list size: " + l.size());
        System.out.println(l.toString());
        System.out.println("-------fail safe end-------------");
    }
    
}
