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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.vaadin.server.startup.RouteRegistry;
import com.vaadin.ui.Component;

/**
 * Default implementation of the {@link RouteResolver} interface.
 *
 * @author Vaadin Ltd.
 */
public class DefaultRouteResolver implements RouteResolver {

    @Override
    public NavigationState resolve(ResolveRequest request) {
        String path = findPathString(request.getLocation().getSegments());
        if (path == null) {
            return null;
        }

        NavigationStateBuilder builder = new NavigationStateBuilder();
        Class<? extends Component> navigationTarget = getNavigationTarget(path);

        if (HasUrlParameter.class.isAssignableFrom(navigationTarget)) {
            List<String> pathParameters = getPathParameters(
                    request.getLocation().getPath(), path);
            if (!HasUrlParameter.verifyParameters(navigationTarget,
                    pathParameters)) {
                return null;
            }
            builder.withTarget(navigationTarget, pathParameters);
        } else {
            builder.withTarget(navigationTarget);
        }

        return builder.build();
    }

    private String findPathString(List<String> pathSegments) {
        if (pathSegments.isEmpty()) {
            return null;
        }

        List<String> paths = new ArrayList<>();
        StringBuilder pathBuilder = new StringBuilder(pathSegments.get(0));
        paths.add(pathBuilder.toString());
        for (int i = 1; i < pathSegments.size(); i++) {
            pathBuilder.append("/").append(pathSegments.get(i));
            paths.add(pathBuilder.toString());
        }
        while (!paths.isEmpty()) {
            String currentPath = paths.get(paths.size() - 1);
            Optional<?> target = RouteRegistry.getInstance()
                    .getNavigationTarget(currentPath);
            if (target.isPresent()) {
                return currentPath;
            }
            paths.remove(paths.size() - 1);
        }
        return null;
    }

    private Class<? extends Component> getNavigationTarget(String path) {
        return RouteRegistry.getInstance().getNavigationTarget(path)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No navigation target found for path '%s'.", path)));
    }

    private List<String> getPathParameters(String completePath,
            String routePath) {
        assert completePath != null;
        assert routePath != null;

        String parameterPart = completePath.replaceFirst(routePath, "");
        if (parameterPart.startsWith("/")) {
            parameterPart = parameterPart.substring(1, parameterPart.length());
        }
        if (parameterPart.endsWith("/")) {
            parameterPart = parameterPart.substring(0,
                    parameterPart.length() - 1);
        }
        if (parameterPart.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(parameterPart.split("/"));
    }
}
