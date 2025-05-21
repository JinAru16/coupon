package event.coupon.controller;

import event.coupon.domain.response.CouponResponse;
import event.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;
    @PostMapping("/{id}/issue")
    public ResponseEntity<?> publishCoupon(@PathVariable Long id) {

        CouponResponse couponResponse = couponService.publishCoupon(id);

        return ResponseEntity
                .ok()
                .body(couponResponse);
    }
}
