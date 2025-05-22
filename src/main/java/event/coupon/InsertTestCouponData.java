package event.coupon;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import event.coupon.repository.CouponRepository;
import event.coupon.repository.CouponStockRepository;
import event.coupon.service.CouponRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InsertTestCouponData implements ApplicationRunner {

    private final CouponRepository couponRepository;
    private final CouponStockRepository stockRepository;
    private final CouponRedisService couponRedisService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Long plannedCount = 10L;


        // 테스트 쿠폰 생성
        new Coupon();
        Coupon testCoupon = Coupon.builder()
                .couponName("테스트쿠폰123")
                .planedCount(plannedCount)
                .discountPercent(3)
                .limitDiscountAmount(BigDecimal.valueOf(20_000))
                .build();
        Coupon save = couponRepository.save(testCoupon);
        Boolean delete = redisTemplate.delete("coupon:stock:" + save.getId());
        Boolean delete2 = redisTemplate.delete("coupon:issued:" + save.getId());
        System.out.println("deleted : "+ delete);


        //테스트 쿠폰은 토탈 50장만 발행한다.
        CouponStock couponStock = new CouponStock(testCoupon, 0L, 0L);
        stockRepository.save(couponStock);
        couponRedisService.generateCoupon(testCoupon.getId(), plannedCount);
    }
}
