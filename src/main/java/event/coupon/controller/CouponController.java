package event.coupon.controller;

import event.coupon.domain.request.CouponRequest;
import event.coupon.domain.response.CouponResponse;
import event.coupon.domain.response.GeneratedCoupon;
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
    public void generateCoupon(CouponRequest couponRequest){
        GeneratedCoupon generatedCoupon = couponService.generateCoupon(couponRequest);

    }

    @PostMapping("coupon/{id}")
    public ResponseEntity<?> issueCoupon(@PathVariable Long id) {

        CouponResponse couponResponse = couponService.issueCoupon(id);

        return ResponseEntity
                .ok()
                .body(couponResponse);
    }
}
