package event.coupon.domain.entity;

import event.coupon.exception.ExceededCouponException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class CouponStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @OneToOne
    Coupon coupon;

    private Long issuedCount; // 발행된 쿠폰수량
    private Long usedCount;  // 사용한 쿠폰 수량.

    public void issueCoupon(){
        if(issuedCount < coupon.getPlanedCount()){
            this.issuedCount += 1;

        } else{
            throw new ExceededCouponException(coupon.getPlanedCount().toString());
        }
    }

    public CouponStock(Coupon coupon, Long issuedCount, Long usedCount) {
        this.coupon = coupon;
        this.issuedCount = issuedCount;
        this.usedCount = usedCount;
    }


    //    @Version // 낙관적 락 걸 때 사용. 본 쿠폰이벤트는 비관적 락 사용 예정.
//    private Long version;
}
