package event.coupon.domain.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
public class CouponRequest {
    private String couponName;

    private Long planedCount; // 발행될 전체 쿠폰수량.

    private int discountPercent;

    private BigDecimal limitDiscountAmount;

    @Builder
    public CouponRequest(String couponName, Long planedCount, int discountPercent, BigDecimal limitDiscountAmount) {
        this.couponName = couponName;
        this.planedCount = planedCount;
        this.discountPercent = discountPercent;
        this.limitDiscountAmount = limitDiscountAmount;
    }
}
