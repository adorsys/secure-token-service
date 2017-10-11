package de.adorsys.sts.keymanagement;

import de.adorsys.sts.keymanagement.config.KeyManagerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({
        KeyManagerConfiguration.class
})
public @interface EnableKeyManagement {
}
