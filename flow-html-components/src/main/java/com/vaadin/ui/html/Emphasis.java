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
package com.vaadin.ui.html;

import com.vaadin.ui.event.Tag;
import com.vaadin.ui.common.HtmlContainer;
import com.vaadin.ui.event.ClickNotifier;
import com.vaadin.ui.Component;

/**
 * Component representing a <code>&lt;em&gt;</code> element.
 *
 * @author Vaadin Ltd
 */
@Tag(Tag.EM)
public class Emphasis extends HtmlContainer implements ClickNotifier {

    /**
     * Creates a new empty emphasis.
     */
    public Emphasis() {
        super();
    }

    /**
     * Creates a new emphasis with the given child components.
     *
     * @param components
     *            the child components
     */
    public Emphasis(Component... components) {
        super(components);
    }

    /**
     * Creates a new emphasis with the given text.
     *
     * @param text
     *            the text
     */
    public Emphasis(String text) {
        super();
        setText(text);
    }
}
