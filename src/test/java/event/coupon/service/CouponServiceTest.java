package event.coupon.service;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import event.coupon.repository.CouponRepository;
import event.coupon.repository.CouponStockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


@Profile("test")
@SpringBootTest
@Transactional
class CouponServiceTest {

    @Autowired
    CouponRepository repository;

    @Autowired
    CouponStockRepository stockRepository;

    @BeforeEach
    void setup(){
        // 테스트 쿠폰 생성
        Coupon testCoupon = new Coupon("테스트쿠폰", 20L, BigDecimal.valueOf(20_000));
        repository.save(testCoupon);

        //테스트 쿠폰은 토탈 50장만 발행한다.
        new CouponStock(testCoupon, 50L, 0L, 0L);
        stockRepository.save(new CouponStock());
    }

    @Test
    @DisplayName("테스트쿠폰을 조회하는데 성공한다.")
    void getTestCoupon(){
        //given
        Coupon coupon = repository.findByCouponName("테스트쿠폰");

        //when

        //then
        String couponName = coupon.getCouponName();
        System.out.println("couponName :" + couponName);
        Assertions.assertThat(couponName).isEqualTo("테스트쿠폰");

    }

    @Test
    @DisplayName("테스트 쿠폰의 발급 가능 수량은 50개다.")
    void testCouponStock(){
        //given

        //when
        stockRepository.findByCouponName("테스트쿠폰");

        //then

    }

}