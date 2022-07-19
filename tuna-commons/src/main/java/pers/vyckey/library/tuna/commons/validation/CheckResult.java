package pers.vyckey.library.tuna.commons.validation;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.helpers.MessageFormatter;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * description is here
 *
 * @author vyckey
 * 2022/7/19 10:53
 */
@Getter
@ToString
public class CheckResult {
    public static final CheckResult SUCCESS = new CheckResult(true, null);
    private final boolean success;
    private final String message;

    protected CheckResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public <T> CheckResult andCheck(T target, Checker<T> checker) {
        return success ? checker.check(target) : this;
    }

    public <T> CheckResult andCheck(T target, Predicate<T> predicate, String message, Object... args) {
        return success ? Checker.of(predicate, message, args).check(target) : this;
    }

    public <T> CheckResult andCheckAll(Iterable<T> target, Checker<T> checker) {
        CheckResult result = this;
        Iterator<T> iterator = target.iterator();
        while (result.success && iterator.hasNext()) {
            result = checker.check(iterator.next());
        }
        return result;
    }

    public <T> CheckResult andCheckAll(Iterable<T> target, Predicate<T> predicate, String message, Object... args) {
        return andCheckAll(target, Checker.of(predicate, message, args));
    }

    private static String formatMessage(String message, Object... args) {
        return args.length == 0 ? message : MessageFormatter.arrayFormat(message, args).getMessage();
    }

    public static CheckResult success(String message, Object... args) {
        return new CheckResult(true, formatMessage(message, args));
    }

    public static CheckResult fail(String message, Object... args) {
        return new CheckResult(false, formatMessage(message, args));
    }
}
