package ronin.ua.balance.processor.mappers;

import java.lang.reflect.Method;
import java.util.Optional;

public abstract class Mapper<M> {
    public <T> T map(M m) {
        Class<T> tClass =  getRetType();
        return map(m, tClass);
    }

    public abstract <T> T map(M m, Class<T> t);

    protected <T> Class<T> getRetType() {
        return StackWalker.getInstance().walk(stream -> stream.skip(2).findFirst().flatMap(frame -> {
            try {
                Class<?> clazz = Class.forName(frame.getClassName());
                String methodName = frame.getMethodName();

                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(methodName)) {
                        return Optional.of((Class<T>) method.getReturnType());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        })).orElseThrow();
    }
}
