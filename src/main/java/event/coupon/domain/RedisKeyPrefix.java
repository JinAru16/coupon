package event.coupon.domain;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum RedisKeyPrefix {
    STOCK_KEY("coupon:stock"),
    USER_KEY("coupon:issued"),
    COUPON_QUEUE("coupon:queue");
    ;

    private final String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String of(Object... args) {
        return prefix + ":" + Arrays.stream(args)
                .map(String::valueOf)
                .collect(Collectors.joining(":"));
    }
}
