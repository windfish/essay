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
        /**
         * LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder)
         * accessOrder 为true，表示缓存记录按访问顺序；false，表示缓存顺序按插入顺序
         * 在put 和putAll 之后，将调用removeEldestEntry 方法，移除最旧的缓存记录
         */
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
    
    public int getMaxSize() {
        return size;
    }
    
    public int getSize(){
        return cache.size();
    }

    /**
     * LRU 缓存构造器，对应的缓存为{@link LRUCache}
     * 
     * @author xuliang
     * @since 2018年10月16日 上午9:18:22
     */
    public static class LRUCacheBuilder<K, V> {
        
        private int size;
        
        /**
         * 实例化LRU 缓存构造器
         */
        public static LRUCacheBuilder<Object, Object> newBuild(){
            return new LRUCacheBuilder<Object, Object>();
        }
        
        /**
         * 设置缓存的最大容量
         */
        public LRUCacheBuilder<K, V> maxSize(int size){
            this.size = size;
            return this;
        }
        
        /**
         * 构造一个LRU 缓存
         */
        public <K1 extends K, V1 extends V> LRUCache<K1, V1> build() {
            return new LRUCache<>(this.size);
        }
        
        /**
         * 构造一个LRU 缓存，该缓存支持key 不存在时，自动根据默认加载方式加载数据到缓存
         * 
         * @param loader 缓存加载器，用来加载新的数据到缓存
         */
        public <K1 extends K, V1 extends V> LRUCache<K1, V1> build(LRUCacheLoader<K1, V1> loader) {
            return new LRUCache<>(this.size, loader);
        }
        
    }
    
    /**
     * 缓存加载器接口
     * 
     * @author xuliang
     * @since 2018年10月16日 上午9:27:53
     */
    public interface LRUCacheLoader<K, V> {
        V load(K key) throws Exception;
    }
    
}
