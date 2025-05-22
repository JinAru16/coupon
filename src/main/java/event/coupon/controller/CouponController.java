package event.coupon.controller;

import event.coupon.domain.request.CouponRequest;
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

    @PostMapping("/coupon")
    public void generateCoupon(@RequestBody CouponRequest couponRequest){
        couponService.generateCoupon(couponRequest);

    }

    @PostMapping("/coupon/{id}")
    public ResponseEntity<?> issueCoupon(@PathVariable Long id, @RequestParam("id") Long userId) {

        CouponResponse couponResponse = couponService.issueCoupon(id, userId);

        return ResponseEntity
                .ok()
                .body(couponResponse);
    }

    @PutMapping("/coupon/{id}")
    public void restockCoupon(@PathVariable Long id, @RequestParam("plannedCount") Long plannedCount){
        couponService.restockCoupon(id, plannedCount);
    }
}
