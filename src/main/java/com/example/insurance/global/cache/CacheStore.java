package com.example.insurance.global.cache;

import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.units.qual.K;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.validation.constraints.NotNull;

public class CacheStore<K, V> {
    private final Cache<K, V> cache;

    public CacheStore(int expiryDuration, TimeUnit timeUnit) {

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration, timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    public V get(@NotNull K key) {
        return cache.getIfPresent(key);
    }

    public void put(@NotNull K key, @NotNull V value) {
        cache.put(key, value);
    }

    public void evict(@NotNull K key) {
        cache.invalidate(key);
    }

}
