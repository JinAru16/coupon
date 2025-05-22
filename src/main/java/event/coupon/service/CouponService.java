package event.coupon.service;

import event.coupon.domain.TryAcquireStatus;
import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import event.coupon.domain.request.CouponRequest;
import event.coupon.domain.response.CouponResponse;
import event.coupon.domain.response.GeneratedCoupon;
import event.coupon.exception.ExceededCouponException;
import event.coupon.exception.NotValidCouponException;
import event.coupon.repository.CouponRepository;
import event.coupon.repository.CouponStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CouponService {

    private final CouponRedisService couponRedisService;
    private final CouponRepository couponRepository;
    private final CouponStockRepository couponStockRepository;

    /* 사용자가 쿠폰을 발급받음*/
    public CouponResponse issueCoupon(Long id, Long userId) {

        TryAcquireStatus tryAcquireStatus = couponRedisService.tryAcquire(id, userId);
        System.out.println("try : " + tryAcquireStatus);

        if (tryAcquireStatus == TryAcquireStatus.REMAIN) {
            CouponStock couponStock = couponStockRepository.findByCouponId(id)
                    .orElseThrow(() -> new NotValidCouponException(id));

            couponStock.issueCoupon();
            couponStockRepository.save(couponStock);

            log.info("현재 남은 쿠폰수량 : {}", couponStock.getCoupon().getPlanedCount() - couponStock.getIssuedCount());
            return new CouponResponse(couponStock.getCoupon());
        } else{
            throw new ExceededCouponException();
        }

    }

    /* 관리자가 쿠폰 최초 생성.*/
    public GeneratedCoupon generateCoupon(CouponRequest couponRequest){

        Coupon coupon = couponRepository.save(new Coupon(couponRequest));
        CouponStock couponStock = new CouponStock(coupon, 0L, 0L);
        CouponStock stock = couponStockRepository.save(couponStock);

        couponRedisService.generateCoupon(coupon.getId(), coupon.getPlanedCount());
        return new GeneratedCoupon(coupon, stock);
    }
}
