package event.coupon.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    String couponName;

    Long discountPercent;

    BigDecimal limitDiscountAmount;

    public Coupon(String couponName, long discountPercent, BigDecimal limitDiscountAmount) {
        this.couponName = couponName;
        this.discountPercent = discountPercent;
        this.limitDiscountAmount = limitDiscountAmount;
    }
}
