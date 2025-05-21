package event.coupon.exception;

public class NotValidCouponException extends RuntimeException{

    public NotValidCouponException(Long message) {
        super("CouponId : "+  message + "\n 해당 쿠폰은 유효한 쿠폰이 아닙니다.");
    }
}
