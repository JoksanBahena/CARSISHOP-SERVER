package mx.edu.utez.carsishop.Jwt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class JwtBlackList {
    private static final LoadingCache<String, Boolean> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Boolean>() {
                public Boolean load(String token) {
                    return Boolean.FALSE;
                }
            });

    public static boolean isTokenBlacklisted(String token) throws ExecutionException {
        return blacklist.get(token);
    }

    public static void addToBlacklist(String token) {
        blacklist.put(token, Boolean.TRUE);
    }
}
