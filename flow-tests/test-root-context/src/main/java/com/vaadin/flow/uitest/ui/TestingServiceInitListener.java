/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.flow.uitest.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServiceInitListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.Dependency;
import com.vaadin.shared.ui.LoadMode;

public class TestingServiceInitListener implements VaadinServiceInitListener {

    private static AtomicInteger initCount = new AtomicInteger();
    private static AtomicInteger requestCount = new AtomicInteger();

    @Override
    public void serviceInit(ServiceInitEvent event) {
        initCount.incrementAndGet();

        event.addRequestHandler(new RequestHandler() {
            @Override
            public boolean handleRequest(VaadinSession session,
                    VaadinRequest request, VaadinResponse response)
                    throws IOException {
                requestCount.incrementAndGet();
                return false;
            }
        });

        event.addDependencyFilter((dependencies, context) -> {
            if (dependencies.stream().anyMatch(dependency -> dependency.getUrl()
                    .startsWith("replaceme://"))) {
                List<Dependency> newList = new ArrayList<>();
                newList.add(new Dependency(Dependency.Type.HTML_IMPORT,
                        "/com/vaadin/flow/uitest/ui/dependencies/filtered.html",
                        LoadMode.EAGER));
                dependencies.stream()
                        .filter(dependency -> !dependency.getUrl()
                                .startsWith("replaceme://"))
                        .forEach(newList::add);
                dependencies = newList;
            }
            return dependencies;
        });
    }

    public static int getInitCount() {
        return initCount.get();
    }

    public static int getRequestCount() {
        return requestCount.get();
    }

}
