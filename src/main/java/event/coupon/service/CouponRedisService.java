package event.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponRedisService {

    private final RedisTemplate<String, String> redisTemplate;


    public void setCouponStock(Long couponId, Long couponStock){
        String redisKey = "coupon:stock:" + couponId;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(couponStock));
    }
}
