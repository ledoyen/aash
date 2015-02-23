package samples;

import com.ledoyen.context.ScopedContext;

/**
 * @author L.LEDOYEN
 */
public class ContextUse {

    public static void main(String[] args) {
        ScopedContext root = ScopedContext.create();

        root.put("env.url", "http://localhost:8080");
        root.put("env.active", "true");
    }
}
