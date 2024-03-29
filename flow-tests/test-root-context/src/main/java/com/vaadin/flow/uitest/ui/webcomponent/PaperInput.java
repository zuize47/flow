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
package com.vaadin.flow.uitest.ui.webcomponent;

import com.vaadin.ui.common.HtmlImport;
import com.vaadin.ui.event.Synchronize;
import com.vaadin.ui.event.Tag;
import com.vaadin.ui.Component;
import com.vaadin.ui.common.PropertyDescriptor;
import com.vaadin.ui.common.PropertyDescriptors;

@Tag("paper-input")
@HtmlImport("/bower_components/paper-input/paper-input.html")
public class PaperInput extends Component {
    private static final PropertyDescriptor<String, String> valueDescriptor = PropertyDescriptors
            .propertyWithDefault("value", "");

    public PaperInput() {
        // (this public no-arg constructor is required so that Flow can
        // instantiate beans of this type
        // when they are bound to template elements via the @Id() annotation)
    }

    public PaperInput(String value) {
        setValue(value);
    }

    @Synchronize("value-changed")
    public String getValue() {
        return get(valueDescriptor);
    }

    @Synchronize("invalid-changed")
    public String getInvalid() {
        return getElement().getProperty("invalid");
    }

    public void setValue(String value) {
        set(valueDescriptor, value);
    }
}