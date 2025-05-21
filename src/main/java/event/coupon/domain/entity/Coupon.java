package event.coupon.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String couponName;

    private Long planedCount; // 발행될 전체 쿠폰수량.

    private int discountPercent;

    private BigDecimal limitDiscountAmount;

    @Builder
    public Coupon(String couponName, Long planedCount, int discountPercent, BigDecimal limitDiscountAmount) {
        this.couponName = couponName;
        this.planedCount = planedCount;
        this.discountPercent = discountPercent;
        this.limitDiscountAmount = limitDiscountAmount;
    }
}
