package event.coupon.repository;

import event.coupon.domain.entity.CouponStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {

}
