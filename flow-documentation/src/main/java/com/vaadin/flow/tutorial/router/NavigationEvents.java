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
package com.vaadin.flow.tutorial.router;

import com.vaadin.ui.common.HtmlImport;
import com.vaadin.router.Route;
import com.vaadin.ui.event.Tag;
import com.vaadin.ui.html.Anchor;
import com.vaadin.ui.html.Div;
import com.vaadin.router.event.AfterNavigationEvent;
import com.vaadin.router.event.AfterNavigationListener;
import com.vaadin.router.event.BeforeNavigationEvent;
import com.vaadin.router.event.BeforeNavigationListener;
import com.vaadin.ui.polymertemplate.PolymerTemplate;
import com.vaadin.flow.model.TemplateModel;
import com.vaadin.flow.tutorial.annotations.CodeFor;

@CodeFor("routing/tutorial-routing-lifecycle.asciidoc")
public class NavigationEvents {

    @Route("")
    @Tag("main-layout")
    @HtmlImport("frontend://com/example//MainLayout.html")
    public class MainLayout extends PolymerTemplate<TemplateModel> {

        public MainLayout() {
            SideElement side = new SideElement();
            getElement().appendChild(side.getElement());
        }
    }

    public class SideElement extends Div implements BeforeNavigationListener {
        @Override
        public void beforeNavigation(BeforeNavigationEvent event) {
            // Handle for instance before navigation clean up
        }
    }

    @Route("no-items")
    public class NoItemsView extends Div {
        public NoItemsView() {
            setText("No items found.");
        }
    }

    @Route("blog")
    public class BlogList extends Div implements BeforeNavigationListener {
        @Override
        public void beforeNavigation(BeforeNavigationEvent event) {
            // implementation omitted
            Object record = getItem();

            if (record == null) {
                event.rerouteTo(NoItemsView.class);
            }
        }

        private Object getItem() {
            // no-op implementation
            return null;
        }
    }

    public class SideMenu extends Div implements AfterNavigationListener {
        Anchor blog = new Anchor("blog", "Blog");

        @Override
        public void afterNavigation(AfterNavigationEvent event) {
            boolean active = event.getLocation().getFirstSegment()
                    .equals(blog.getHref());
            blog.getElement().getClassList().set("≥active", active);
        }
    }
}
