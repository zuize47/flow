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
package com.vaadin.server.startup;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.router.ParentLayout;
import com.vaadin.router.Route;
import com.vaadin.server.InvalidRouteConfigurationException;
import com.vaadin.server.InvalidRouteLayoutConfigurationException;
import com.vaadin.ui.Component;

/**
 * Servlet initializer for collecting all available {@link Route}s on startup.
 */
@HandlesTypes(Route.class)
public class RouteRegistryInitializer implements ServletContainerInitializer {

    @SuppressWarnings("unchecked")
    @Override
    public void onStartup(Set<Class<?>> classSet, ServletContext servletContext)
            throws ServletException {
        try {
            if (classSet == null) {
                RouteRegistry.getInstance()
                        .setNavigationTargets(Collections.emptySet());
                return;
            }
            Set<Class<? extends Component>> routes = classSet.stream()
                    .filter(RouteRegistryInitializer::isApplicableClass)
                    .map(clazz -> (Class<? extends Component>) clazz)
                    .peek(this::checkForConflictingAnnotations)
                    .collect(Collectors.toSet());

            RouteRegistry.getInstance().setNavigationTargets(routes);
        } catch (InvalidRouteConfigurationException irce) {
            throw new ServletException(
                    "Exception while registering Routes on servlet startup",
                    irce);
        }
    }

    private void checkForConflictingAnnotations(Class<?> route) {
        if (route.isAnnotationPresent(ParentLayout.class)) {
            throw new InvalidRouteLayoutConfigurationException(route
                    .getCanonicalName()
                    + " contains both @Route and @ParentLayout annotation. Only use @Route with Route.layout.");
        }
    }

    private static boolean isApplicableClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Route.class)
                && Component.class.isAssignableFrom(clazz);
    }
}
