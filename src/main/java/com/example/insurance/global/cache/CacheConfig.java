
package com.example.insurance.global.cache;

import java.util.concurrent.TimeUnit;

public class CacheConfig {

    public CacheStore<String, Integer> userCache() {
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }

}