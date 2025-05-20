package event.coupon.repository;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {

    @Query("select cs from CouponStock cs JOIN FETCH cs.coupon where cs.coupon.couponName =: name")
    CouponStock findByCouponName(String name);
}
