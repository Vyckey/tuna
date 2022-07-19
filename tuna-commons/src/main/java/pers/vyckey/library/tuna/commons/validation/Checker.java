package pers.vyckey.library.tuna.commons.validation;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * description is here
 *
 * @author vyckey
 * 2022/7/19 10:52
 */
@FunctionalInterface
public interface Checker<T> {
    CheckResult check(T target);

    default Checker<T> andCheck(Checker<? super T> other) {
        Objects.requireNonNull(other);
        return (T target) -> {
            CheckResult result = check(target);
            if (result.isSuccess()) {
                result = other.check(target);
            }
            return result;
        };
    }

    default Checker<T> orCheck(Checker<? super T> other) {
        Objects.requireNonNull(other);
        return (T target) -> {
            CheckResult result = check(target);
            if (!result.isSuccess()) {
                result = other.check(target);
            }
            return result;
        };
    }

    default Checker<T> negCheck(String message) {
        return (T target) -> {
            CheckResult result = check(target);
            return result.isSuccess() ? CheckResult.fail(message) : result;
        };
    }

    static <T> Checker<T> of(Predicate<T> predicate, String message, Object... args) {
        return target -> predicate.test(target) ? CheckResult.SUCCESS : CheckResult.fail(message, args);
    }

    static Checker<?> of(Supplier<CheckResult> supplier) {
        return target -> supplier.get();
    }
}
