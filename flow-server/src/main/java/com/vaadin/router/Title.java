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

package com.vaadin.router;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.flow.router.View;

/**
 * Defines the HTML page title for a {@link View}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Title {

    /**
     * Gets the HTML title that should be used.
     * <p>
     * Empty string will clear any previous page title. In that case the browser
     * will decide what to show as the title, most likely the url.
     * <p>
     * You may dynamically update the title for a view by overriding the
     * {@link View#getTitle(com.vaadin.flow.router.LocationChangeEvent)
     * View.getTitle(LocationChangeEvent)} method.
     *
     * @return a page title string
     */
    String value();
}
