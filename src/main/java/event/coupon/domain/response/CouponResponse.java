package event.coupon.domain.response;

import event.coupon.domain.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
public class CouponResponse {

    private String couponName;

    private int discountPercent;

    private BigDecimal limitDiscountAmount;

    public CouponResponse(Coupon coupon){
        this.couponName = coupon.getCouponName();
        this.discountPercent = coupon.getDiscountPercent();
        this.limitDiscountAmount = coupon.getLimitDiscountAmount();
    }
}
