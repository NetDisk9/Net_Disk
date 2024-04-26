package com.net.file.support;

import com.net.file.entity.UserFileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMap<K,V> {
    private Map<K, List<V>> map=new HashMap<>();

    public void put(K key,V value){
        List<V> list;
        if(!map.containsKey(key)){
            list=new ArrayList<>();
            map.put(key,list);
        }
        else{
            list=map.get(key);
        }
        list.add(value);
    }
    public List<V> get(K key){
        List<V> list = map.get(key);
        if(list==null){
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public String toString() {
        return "ListMap{" +
                "map=" + map +
                '}';
    }
}
