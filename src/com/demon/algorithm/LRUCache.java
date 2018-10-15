package com.demon.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * LRU 缓存
 * 
 * @author xuliang
 * @since 2018年10月15日 下午5:15:44
 *
 */
public class LRUCache<K, V> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 最大缓存大小
     */
    private int size;
    
    private LinkedHashMap<K, V> cache;
    
    private LRUCacheLoader<K, V> loader = null;
    
    public LRUCache(int size) {
        this.size = size;
        cache = initCache(size);
    }
    
    public LRUCache(int size, LRUCacheLoader<K, V> loader) {
        this.size = size;
        this.loader = loader;
        cache = initCache(size);
    }
    
    private LinkedHashMap<K, V> initCache(int size){
        return new LinkedHashMap<K, V>(16, 0.75f, true){
            private static final long serialVersionUID = 945362892431345540L;
            
            @Override
            protected boolean removeEldestEntry(Entry<K, V> eldest) {
                if(size + 1 == cache.size()){
                    return true;
                }else{
                    return false;
                }
            }
        };
    }
    
    public void put(K key, V value){
        cache.put(key, value);
    }
    
    public V get(K key){
        V value = cache.get(key);
        if(value == null && loader != null){
            try{
                V load = loader.load(key);
                put(key, load);
                return load;
            }catch (Exception e) {
                logger.error("LRUCache load exception", e);
            }
        }
        return value;
    }
    
    public Collection<Map.Entry<K, V>> getAll(){
        return new ArrayList<Map.Entry<K,V>>(cache.entrySet());
    }
    
    public int getSize() {
        return size;
    }

    public static class LRUCacheBuilder<K, V> {
        
        private int size;
        
        public static LRUCacheBuilder<Object, Object> newBuild(){
            return new LRUCacheBuilder<Object, Object>();
        }
        
        public LRUCacheBuilder<K, V> maxSize(int size){
            this.size = size;
            return this;
        }
        
        public <K1 extends K, V1 extends V> LRUCache<K1, V1> build() {
            return new LRUCache<>(this.size);
        }
        
        public <K1 extends K, V1 extends V> LRUCache<K1, V1> build(LRUCacheLoader<K1, V1> loader) {
            return new LRUCache<>(this.size, loader);
        }
        
    }
    
    public interface LRUCacheLoader<K, V> {
        V load(K key) throws Exception;
    }
    
    public static void main(String[] args) {
        /*LRUCache<Integer, Integer> cache = LRUCacheBuilder.newBuild().maxSize(3).build();
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.get(1);
        cache.put(4, 4);
        System.out.println(cache.getSize());
        System.out.println(JSON.toJSONString(cache.getAll(), true));*/
        
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.put(4, 4);
        LRUCache<Integer, Integer> cache = LRUCacheBuilder.newBuild().maxSize(3)
                .build(new LRUCacheLoader<Integer, Integer>() {
                    @Override
                    public Integer load(Integer key) throws Exception {
                        return map.get(key);
                    }
                });
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
        System.out.println(cache.get(1));
        System.out.println(cache.get(4));
        System.out.println(cache.getSize());
        System.out.println(JSON.toJSONString(cache.getAll(), true));
    }
    
}
