/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.annotations.InternalContainerAnnotationForSS;
import com.vaadin.shared.ui.LoadMode;
import com.vaadin.ui.Component;

/**
 * Annotation for defining style sheet dependencies on a {@link Component}
 * class. For adding multiple style sheets for a single component, you can use
 * this annotation multiple times.
 * <p>
 * It is guaranteed that dependencies will be loaded only once.
 * <p>
 * NOTE: while this annotation is not inherited using the
 * {@link Inherited @Inherited} annotation, the annotations of the possible
 * parent components or implemented interfaces are read when sending the
 * dependencies to the browser.
 *
 * @author Vaadin Ltd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(InternalContainerAnnotationForSS.class)
public @interface StyleSheet {

    /**
     * Style sheet file URL to load before using the annotated {@link Component}
     * in the browser.
     * <p>
     * Relative URLs are interpreted as relative to the service (servlet) path.
     * You can prefix the URL with {@literal context://} to make it relative to
     * the context path or use an absolute URL to refer to files outside the
     * service (servlet) path.
     *
     * @return a style sheet file URL
     */
    String value();

    /**
     * Determines the dependency load mode. Refer to {@link LoadMode} for the
     * details.
     *
     * @return load mode for the dependency
     */
    LoadMode loadMode() default LoadMode.EAGER;
}
