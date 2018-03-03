package com.demon.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
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
