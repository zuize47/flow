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
package com.vaadin.flow.uitest.ui.template;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.common.HtmlImport;
import com.vaadin.ui.event.Tag;
import com.vaadin.ui.html.NativeButton;
import com.vaadin.ui.polymertemplate.PolymerTemplate;
import com.vaadin.flow.model.TemplateModel;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class BasicTypeInListUI extends UI {

    @Tag("basic-type-list")
    @HtmlImport("/com/vaadin/flow/uitest/ui/template/BasicTypeList.html")
    public static class BasicTypeList extends PolymerTemplate<ItemsModel> {

        BasicTypeList() {
            getModel().setItems(Arrays.asList("foo", "bar"));
        }

        @Override
        protected ItemsModel getModel() {
            return super.getModel();
        }
    }

    public interface ItemsModel extends TemplateModel {
        void setItems(List<String> items);

        List<String> getItems();
    }

    @Override
    protected void init(VaadinRequest request) {
        BasicTypeList list = new BasicTypeList();
        list.setId("template");
        add(list);
        NativeButton add = new NativeButton("Add an item",
                event -> list.getModel().getItems().add("newItem"));
        add.setId("add");
        add(add);
        NativeButton remove = new NativeButton("Remove the first item",
                event -> list.getModel().getItems().remove(0));
        remove.setId("remove");
        add(remove);
    }
}
