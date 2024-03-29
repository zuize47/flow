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
package com.vaadin.shared.ui;

import java.io.Serializable;

import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * Represents an html import, stylesheet or JavaScript to include on the page.
 *
 * @author Vaadin Ltd
 */
public class Dependency implements Serializable {
    public static final String KEY_URL = "url";
    public static final String KEY_TYPE = "type";
    public static final String KEY_LOAD_MODE = "mode";
    public static final String KEY_CONTENTS = "contents";

    /**
     * The type of a dependency.
     */
    public enum Type {
        STYLESHEET, JAVASCRIPT, HTML_IMPORT
    }

    private final Type type;
    private final String url;
    private final LoadMode loadMode;

    /**
     * Creates a new dependency of the given type, to be loaded from the given
     * URL.
     * <p>
     * The URL is passed through the translation mechanism before loading, so
     * custom protocols, specified at
     * {@link com.vaadin.shared.VaadinUriResolver} can be used.
     *
     * @param type
     *            the type of the dependency, not {@code null}
     * @param url
     *            the URL to load the dependency from, not {@code null}
     * @param loadMode
     *            determines dependency load mode, refer to {@link LoadMode} for
     *            details
     */
    public Dependency(Type type, String url, LoadMode loadMode) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        assert type != null;

        this.type = type;
        this.url = url;
        this.loadMode = loadMode;
    }

    /**
     * Gets the untranslated URL for the dependency.
     *
     * @return the URL for the dependency
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the type of the dependency.
     *
     * @return the type of the dependency
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets load mode that will be used for dependency loading. Refer to
     * {@link LoadMode} for details.
     *
     * @return the load mode that will be used during dependency loading
     */
    public LoadMode getLoadMode() {
        return loadMode;
    }

    /**
     * Converts the object into json representation.
     *
     * @return json representation of the object
     */
    public JsonObject toJson() {
        JsonObject jsonObject = Json.createObject();
        jsonObject.put(KEY_URL, url);
        jsonObject.put(KEY_TYPE, type.name());
        jsonObject.put(KEY_LOAD_MODE, loadMode.name());
        return jsonObject;
    }
}
