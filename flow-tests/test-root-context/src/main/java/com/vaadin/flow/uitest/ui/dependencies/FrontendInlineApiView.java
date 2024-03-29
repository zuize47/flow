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
package com.vaadin.flow.uitest.ui.dependencies;

import com.vaadin.ui.event.Tag;
import com.vaadin.external.jsoup.Jsoup;
import com.vaadin.flow.router.View;
import com.vaadin.ui.polymertemplate.PolymerTemplate;
import com.vaadin.flow.model.TemplateModel;
import com.vaadin.shared.ui.LoadMode;
import com.vaadin.ui.UI;

@Tag("frontend-inline-api")
public class FrontendInlineApiView extends PolymerTemplate<TemplateModel>
        implements View {

    public FrontendInlineApiView() {
        super((clazz, tag) -> Jsoup
                .parse("<dom-module id='frontend-inline-api'></dom-module>"));
        setId("template");
        UI.getCurrent().getPage().addHtmlImport(
                "frontend://components/frontend-inline-api.html",
                LoadMode.INLINE);
        UI.getCurrent().getPage().addJavaScript(
                "frontend://components/frontend-inline.js", LoadMode.INLINE);
        UI.getCurrent().getPage().addStyleSheet(
                "frontend://components/frontend-inline.css", LoadMode.INLINE);
    }
}
