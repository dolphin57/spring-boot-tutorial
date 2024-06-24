package io.dolphin.uid.annotation;

import io.dolphin.uid.UIDGeneratorStarterAutoConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author dolphin
 * @date 2024年05月15日 15:43
 * @description
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(UIDGeneratorStarterAutoConfigure.class)
public @interface EnableUIDGenerator {
}
