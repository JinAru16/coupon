package event.coupon.setting;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import event.coupon.domain.request.CouponRequest;
import event.coupon.domain.response.GeneratedCoupon;
import event.coupon.repository.CouponRepository;
import event.coupon.repository.CouponStockRepository;
import event.coupon.service.CouponRedisService;
import event.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InsertTestCouponData implements ApplicationRunner {

    private final CouponRepository couponRepository;
    private final CouponStockRepository stockRepository;
    private final CouponService couponService;
    private final CouponRedisService couponRedisService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb(); // 🔥 현재 선택된 Redis DB 전체 삭제
            return null;
        });
        Long plannedCount = 10L;


        // 테스트 쿠폰 생성
        new Coupon();
        CouponRequest testCoupon = CouponRequest.builder()
                .couponName("테스트쿠폰123")
                .planedCount(plannedCount)
                .discountPercent(20)
                .limitDiscountAmount(BigDecimal.valueOf(20_000))
                .build();
        GeneratedCoupon generatedCoupon = couponService.generateCoupon(testCoupon);
        System.out.println("before : " + generatedCoupon);
    }
}
