package com.vaadin.flow.uitest.ui.dependencies;

import com.vaadin.ui.common.HtmlImport;
import com.vaadin.ui.common.JavaScript;

@HtmlImport("replaceme://something-that-doesnt-exist.html")
@JavaScript("/com/vaadin/flow/uitest/ui/dependencies/eager.js")
@HtmlImport("/com/vaadin/flow/uitest/ui/dependencies/eager.html")
public class DependencyFilterUI extends DependenciesLoadingBaseUI {
}
