package com.net.redis.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedissonLockWrapper {
    private RLock lock;
    private RedissonClient redissonClient;
    private static final int MAX_WAITING_TIME=360;
    private static final int MAX_RELEASE_TIME=180;
    public RedissonLockWrapper(RedissonClient redissonClient,String name) throws InterruptedException {
        this.redissonClient=redissonClient;
        this.lock=redissonClient.getLock(name);
    }
    public boolean lock(){
        try {
            return lock.tryLock(360,180, TimeUnit.SECONDS);
        }catch (Exception e){
            return false;
        }
    }
    public void unlock(){
        if(lock.isLocked()){
            lock.unlock();
        }
    }
}
