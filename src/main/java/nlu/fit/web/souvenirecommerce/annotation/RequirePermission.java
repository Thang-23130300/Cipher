package nlu.fit.web.souvenirecommerce.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required permissions for a servlet method.
 * Can be used on doGet/doPost methods to require specific permissions.
 *
 * Example:
 * @RequirePermission(resource = "product", action = "create")
 * protected void doPost(...) { ... }
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * The resource name (e.g., "product", "category", "order")
     */
    String resource() default "";

    /**
     * The action name (e.g., "read", "create", "update", "delete")
     */
    String action() default "read";

    /**
     * Description of what permission is required (for logging/debugging)
     */
    String description() default "";
}
