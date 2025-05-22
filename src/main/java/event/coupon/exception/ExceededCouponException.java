package event.coupon.exception;

public class ExceededCouponException extends RuntimeException{
    public ExceededCouponException(String message) {
        super("쿠폰 발행량이 계획된 수량인 " + message +"개를 초과되었습니다.");
    }

    public ExceededCouponException() {
        super("쿠폰이 모두 소진되었습니다.");
    }
}
