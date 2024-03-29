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
package com.vaadin.flow.demo.views;

import com.vaadin.flow.demo.ComponentDemo;
import com.vaadin.ui.html.Div;
import com.vaadin.generated.vaadin.text.field.GeneratedVaadinTextField;
import com.vaadin.ui.textfield.TextField;

/**
 * View for {@link GeneratedVaadinTextField} demo.
 */
@ComponentDemo(name = "Text Field", href = "vaadin-text-field")
public class TextFieldView extends DemoView {
    @Override
    void initView() {
        Div message = new Div();

        // begin-source-example
        // source-example-heading: Basic text field
        TextField textField = new TextField();
        textField.setLabel("Text field label");
        textField.setPlaceholder("placeholder text");
        textField.addValueChangeListener(event -> message.setText(
                String.format("Text field value changed from '%s' to '%s'",
                        event.getOldValue(), event.getValue())));
        // end-source-example

        textField.setId("text-field-with-value-change-listener");
        message.setId("text-field-value");

        addCard("Basic text field", textField, message);
    }
}
