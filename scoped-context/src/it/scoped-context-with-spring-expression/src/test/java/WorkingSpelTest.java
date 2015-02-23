import static org.junit.Assert.assertThat;

import com.ledoyen.context.ScopedContext;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Optional;

/**
 * @Author L.LEDOYEN.
 */
public class WorkingSpelTest {

    @Test
    public void spel_expressions_are_resolved() {
        ScopedContext context = ScopedContext.create().put("key", "value").putExpression("exp1", "'value1'").putExpression("exp2", "#key");

        assertThat(context.get("exp1"), CoreMatchers.equalTo(Optional.of("value1")));
        assertThat(context.get("exp2"), CoreMatchers.equalTo(Optional.of("value")));
    }
}
