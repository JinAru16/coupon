package event.coupon;

import event.coupon.exception.ExceededCouponException;
import event.coupon.exception.NotValidCouponException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "event.coupon.controller")
public class CouponExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotValidCouponException.class)
    public ErrorResponse invalidRequestHandler(NotValidCouponException e) {

        return ErrorResponse.builder()
                .code("406")
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(ExceededCouponException.class)
    public ErrorResponse exceedCouponHandler(ExceededCouponException e) {

        return ErrorResponse.builder()
                .code("406")
                .message(e.getMessage())
                .build();
    }
}
