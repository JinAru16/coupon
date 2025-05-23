package event.coupon.service;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import event.coupon.domain.request.CouponRequest;
import event.coupon.domain.response.GeneratedCoupon;
import event.coupon.exception.ExceededCouponException;
import event.coupon.exception.NotValidCouponException;
import event.coupon.repository.CouponRepository;
import event.coupon.repository.CouponStockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

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

    @PersistenceContext
    EntityManager em;

    long couponId = 1L;
    String redisKey = "coupon:stock:" + couponId;
    @Autowired
    private CouponService couponService;


   // @BeforeEach
    void setup() {
        // 레디스에 올라간 쿠폰 수량은 초기화한다.
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb(); // 🔥 현재 선택된 Redis DB 전체 삭제
            return null;
        });
        // 테스트 쿠폰 생성
        CouponRequest testCoupon = CouponRequest.builder()
                .couponName("테스트쿠폰")
                .planedCount(10L)
                .discountPercent(20)
                .limitDiscountAmount(BigDecimal.valueOf(20_000))
                .build();
        GeneratedCoupon generatedCoupon = couponService.generateCoupon(testCoupon);
        System.out.println("before : " + generatedCoupon);
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
        assertThat(couponName).isEqualTo("테스트쿠폰");

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
        assertThat(totalCount).isEqualTo(50L);
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
        assertThat(remain.getCoupon().getPlanedCount() - remain.getIssuedCount()).isEqualTo(49L);

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
        assertThatThrownBy(() -> stock.issueCoupon())
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
        assertThat(redisStock).isEqualTo("10");

    }

    @Test
    @DisplayName("쿠폰을 생성하는데 성공한다.")
    void generateCoupon() {
        //given
        CouponRequest request = CouponRequest.builder()
                .couponName("생성된쿠폰")
                .planedCount(10L)
                .discountPercent(20)
                .limitDiscountAmount(BigDecimal.valueOf(20_000))
                .build();
        //when
        GeneratedCoupon generatedCoupon = couponService.generateCoupon(request);
        String redisStock = redisTemplate.opsForValue().get("coupon:stock:" + generatedCoupon.getCouponId());

        //then
        System.out.println(generatedCoupon);
        assertThat(generatedCoupon.getCouponName()).isEqualTo(request.getCouponName());
        assertThat(generatedCoupon.getPlanedCount()).isEqualTo(request.getPlanedCount());

        // 레디스 검증.
        assertThat(redisStock).isEqualTo(request.getPlanedCount().toString());
    }

    @Test
    @DisplayName("여러명의 각기 다른 사람이 쿠폰을 발급받는데 성공함.")
    void issueCouponTest(){
        //given
        //when
        for(int i=0; i<10; i++){
            System.out.println("userid : "+ i);
            couponService.issueCoupon(1L, (long) i);
        }
        //then
        assertThatThrownBy(() ->  couponService.issueCoupon(1L, 11L)).isInstanceOf(ExceededCouponException.class);
    }



    @Test
    @DisplayName("다중 쓰레드를 이용하여 쿠폰발급 동시성 테스트.")
    void multiThreadCouponTest() throws InterruptedException {
        //given
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(32);

        //when
        for (long userId = 1; userId <= 32; userId++) {
            final long uid = userId;
            executor.submit(() -> {
                try {
                    couponService.issueCoupon(couponId, uid);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(); // 모든 스레드 종료 대기

        //then

        // Redis 대기열에 들어간 유저 수 == 발급된 수
//        Long issuedCount = redisTemplate.opsForList().size("coupon:queue:" + couponId);
//        assertThat(issuedCount).isEqualTo(10);

        // DB 상태도 확인
        //em.clear();
        CouponStock stock = stockRepository.findByCouponId(couponId).orElseThrow();
        System.out.println(stock);
        assertThat(stock.getIssuedCount()).isEqualTo(10);

    }
}