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
package com.vaadin.server;

import java.io.Serializable;
import java.util.List;

import com.vaadin.ui.common.HtmlImport;
import com.vaadin.ui.common.JavaScript;
import com.vaadin.ui.common.StyleSheet;
import com.vaadin.shared.ui.Dependency;

/**
 * Filter for dependencies loaded using {@link StyleSheet @StyleSheet},
 * {@link JavaScript @JavaScript} and {@link HtmlImport @HtmlImport}.
 *
 * @see ServiceInitEvent#addDependencyFilter(DependencyFilter)
 */
@FunctionalInterface
public interface DependencyFilter extends Serializable {

    /**
     * Filters the list of dependencies and returns a (possibly) updated
     * version.
     * <p>
     * Called whenever dependencies are about to be sent to the client side for
     * loading.
     *
     * @param dependencies
     *            the collected dependencies, possibly already modified by other
     *            filters
     * @param filterContext
     *            context information, e.g about the target UI
     * @return a list of dependencies to load
     */
    List<Dependency> filter(List<Dependency> dependencies,
            FilterContext filterContext);

    /**
     * Provides context information for the dependency filter operation.
     */
    public static class FilterContext implements Serializable {

        private VaadinSession session;

        /**
         * Creates a new context for the given session.
         *
         * @param session
         *            the session which is loading dependencies
         */
        public FilterContext(VaadinSession session) {
            this.session = session;
        }

        /**
         * Gets the related Vaadin session.
         *
         * @return the Vaadin session
         */
        public VaadinSession getSession() {
            return session;
        }

        /**
         * Gets the related Vaadin service.
         *
         * @return the Vaadin service
         */
        public VaadinService getService() {
            return session.getService();
        }
    }

}
