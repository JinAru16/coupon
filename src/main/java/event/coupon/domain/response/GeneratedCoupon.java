package event.coupon.domain.response;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class GeneratedCoupon {

    private Long couponId;

    private String couponName;

    private Long planedCount; // 발행될 전체 쿠폰수량.

    private int discountPercent;

    private BigDecimal limitDiscountAmount;

    private Long issuedCount; // 발행된 쿠폰수량
    private Long usedCount;  // 사용한 쿠폰 수량.

    public GeneratedCoupon(Coupon coupon, CouponStock couponStock){
        this.couponId = coupon.getId();
        this.couponName = coupon.getCouponName();
        this.planedCount = coupon.getPlanedCount();
        this.discountPercent = coupon.getDiscountPercent();
        this.limitDiscountAmount = coupon.getLimitDiscountAmount();
        this.issuedCount = couponStock.getIssuedCount();
        this.usedCount = couponStock.getUsedCount();
    }
}
