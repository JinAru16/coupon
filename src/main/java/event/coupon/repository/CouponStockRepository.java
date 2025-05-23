package event.coupon.repository;

import event.coupon.domain.entity.Coupon;
import event.coupon.domain.entity.CouponStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {

    @Query("select cs from CouponStock cs JOIN FETCH cs.coupon where cs.coupon.couponName =:couponName")
    Optional<CouponStock> findByCouponName(@Param("couponName") String name);

    @Query("select cs from CouponStock cs JOIN FETCH cs.coupon where cs.id =:id")
    Optional<CouponStock> findByCouponId(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cs from CouponStock cs join fetch cs.coupon where cs.id = :id")
    Optional<CouponStock> findByCouponIdForUpdate(@Param("id") Long id);
}
