package event.coupon.service;

import event.coupon.domain.RedisKeyPrefix;
import event.coupon.domain.TryAcquireStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponRedisService {

    private final RedisTemplate<String, String> redisTemplate;


    public void generateCoupon(Long couponId, Long plannedCount){
        String redisKey = "coupon:stock:" + couponId;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(plannedCount));
    }

    public void restockCoupon(Long couponId, Long plannedCount){
        String redisKey = "coupon:stock:" + couponId;
        redisTemplate.opsForValue().increment(redisKey, plannedCount);
    }


    public TryAcquireStatus tryAcquire(Long couponId, Long userId) {
        String stockKey = RedisKeyPrefix.STOCK_KEY.of(couponId);
        String userKey = RedisKeyPrefix.USER_KEY.of(couponId,userId);

        Boolean notIssued = redisTemplate
                .opsForValue()
                .setIfAbsent(userKey, "1", Duration.ofMinutes(30));// 최초 발급자는 true 반환.

        if (!Boolean.TRUE.equals(notIssued)) return TryAcquireStatus.ISSUED;

        Long remain = safeDecrementStock(stockKey); // Lua 기반 원자적 감소
        if (remain != null && remain >= 0) {
            return TryAcquireStatus.REMAIN;
        }

        redisTemplate.delete(userKey); // 처음 발급 받은 사용자가 재고가 없어서 발급 못받은 경우 레디스에서 기 발급자 명단에 올라간걸 제거. 롤백
        return TryAcquireStatus.OUT_OF_STOCK;
    }

    private Long safeDecrementStock(String stockKey) {
        String script = """
                            local stock = redis.call('GET', KEYS[1])
                            if stock and tonumber(stock) > 0 then
                                return redis.call('DECR', KEYS[1])
                            else
                                return -1
                            end
                        """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        return redisTemplate.execute(redisScript, List.of(stockKey));
    }
}
