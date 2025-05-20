package event.coupon.domain.entity;

import jakarta.persistence.*;

@Entity
public class CouponStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @OneToOne
    Coupon coupon;

    private Long totalCount;
    private Long issuedCount;
    private Long usedCount;

//    @Version // 낙관적 락 걸 때 사용. 본 쿠폰이벤트는 비관적 락 사용 예정.
//    private Long version;
}
