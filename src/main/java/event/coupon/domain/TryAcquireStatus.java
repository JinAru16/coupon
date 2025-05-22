package event.coupon.domain;

public enum TryAcquireStatus {
    NOT_ISSUED(),
    ISSUED(),
    REMAIN(),
    OUT_OF_STOCK();
}
