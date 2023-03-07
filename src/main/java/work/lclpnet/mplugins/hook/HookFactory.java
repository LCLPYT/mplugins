package work.lclpnet.mplugins.hook;

import java.util.function.Function;

public class HookFactory {

    public static <T> Hook<T> createArrayBacked(Class<T> type, Function<T[], T> invokerBuilder) {
        return new ArrayBackedHook<>(type, invokerBuilder);
    }
}
