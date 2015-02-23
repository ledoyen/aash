import static org.junit.Assert.*;

import com.ledoyen.context.ScopedContext;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Optional;

/**
 * @Author L.LEDOYEN
 */
public class BasicStorageWorkingButExpressionsTest {

    @Test
    public void put_and_get_working() {
        ScopedContext root = ScopedContext.create().put("key", "value");
        ScopedContext child = root.createChild().put("key", "overrided");

        assertThat(root.get("key"), CoreMatchers.equalTo(Optional.of("value")));
        assertThat(child.get("key"), CoreMatchers.equalTo(Optional.of("overrided")));
    }

    @Test(expected = IllegalStateException.class)
    public void put_expression_raise_exception() {
        ScopedContext.create().putExpression("key", "'value'");
    }
}
