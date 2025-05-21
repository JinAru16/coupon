package event.coupon.service;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import event.coupon.exception.ExceededCouponException;
import event.coupon.exception.NotValidCouponException;
import event.coupon.repository.CouponRepository;
import event.coupon.repository.CouponStockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


@Profile("test")
@SpringBootTest
@Transactional
class CouponServiceTest {

    @Autowired
    CouponRepository repository;

    @Autowired
    CouponStockRepository stockRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    Long couponId = 1L;
    String redisKey = "coupon:stock:" + couponId;


    //@BeforeEach
    void setup() {
        // 테스트 쿠폰 생성
        Coupon testCoupon = new Coupon().builder()
                .couponName("테스트쿠폰")
                .planedCount(50L)
                .discountPercent(20)
                .limitDiscountAmount(BigDecimal.valueOf(20_000))
                .build();
        repository.save(testCoupon);

        //테스트 쿠폰은 토탈 50장만 발행한다.
        CouponStock couponStock = new CouponStock(testCoupon, 50L, 0L, 0L);
        stockRepository.save(couponStock);

        // 레디스에 올라간 쿠폰 수량은 초기화한다.
        redisTemplate.delete(redisKey);
        System.out.println("레디스 키 초기화" + redisTemplate.opsForValue().get(redisKey));
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
        Coupon coupon = repository.findByCouponName("테스트쿠폰");
        CouponStock stock = stockRepository.findByCouponId(coupon.getId()).orElseThrow(() -> new NotValidCouponException(coupon.getId()));

        //then
        Long totalCount = stock.getCoupon().getPlanedCount();
        Assertions.assertThat(totalCount).isEqualTo(50L);
    }

    @Test
    @DisplayName("쿠폰을 한장 발급받으면 쿠폰 재고 수량은 49장이 된다.")
    void issueCoupon(){
        //given
        Coupon coupon = repository.findByCouponName("테스트쿠폰");
        CouponStock stock = stockRepository.findByCouponId(coupon.getId()).orElseThrow(() -> new NotValidCouponException(coupon.getId()));
        //when
        stock.issueCoupon();
        stockRepository.save(stock);

        //then
        CouponStock remain = stockRepository.findByCouponId(coupon.getId()).orElseThrow(() -> new NotValidCouponException(coupon.getId()));
        Assertions.assertThat(remain.getCoupon().getPlanedCount() - remain.getIssuedCount()).isEqualTo(49L);

    }

    @Test
    @DisplayName("쿠폰 발급수량을 초과하면 ExceededCouponException이 발생한다.")
    void overIssuedCouponException(){
        //given
        Coupon coupon = repository.findByCouponName("테스트쿠폰");
        CouponStock stock = stockRepository.findByCouponId(coupon.getId()).orElseThrow(() -> new NotValidCouponException(coupon.getId()));

        //when
        for(int i = 0; i < coupon.getPlanedCount(); i++){
            stock.issueCoupon();
        }

        //then
        Assertions.assertThatThrownBy(() -> stock.issueCoupon())
                .isInstanceOf(ExceededCouponException.class);

    }

    @Test
    @DisplayName("쿠폰 재고를 레디스에 등록")
    void setCouponStock(){

        // 캐시 초기화 된건지 확인해보자.
        redisTemplate.delete(redisKey);
        System.out.println("rediskey: "+redisKey+ ":"+ redisTemplate.opsForValue().get(redisKey));

        //given
        redisTemplate.opsForValue().set(redisKey, String.valueOf(10), Duration.ofHours(1));


        //when

        String redisStock = redisTemplate.opsForValue().get(redisKey);
        System.out.println("redisStock : " + redisStock);

        //then
        Assertions.assertThat(redisStock).isEqualTo("10");

    }
}