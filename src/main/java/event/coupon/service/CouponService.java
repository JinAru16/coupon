package event.coupon.service;

import event.coupon.domain.entity.CouponStock;
import event.coupon.domain.response.CouponResponse;
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

    private final CouponRepository couponRepository;
    private final CouponStockRepository couponStockRepository;

    public CouponResponse publishCoupon(Long id) {

        CouponStock couponStock = couponStockRepository.findByCouponId(id)
                .orElseThrow(() -> new NotValidCouponException(id));

        couponStock.issueCoupon();
        couponStockRepository.save(couponStock);

        log.info("현재 남은 쿠폰수량 : {}", couponStock.getCoupon().getPlanedCount() - couponStock.getIssuedCount());
        return new CouponResponse(couponStock.getCoupon());
    }
}
