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
package com.vaadin.client.flow.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for validating custom-element name according to definition in <a href=
 * "https://www.w3.org/TR/custom-elements/#prod-potentialcustomelementname">Custom
 * element name</a>
 */
public final class CustomElementNameValidator {

    private static Set<String> reservedNames = Stream.of("annotation-xml",
            "color-profile", "font-face", "font-face-src", "font-face-uri",
            "font-face-format", "font-face-name", "missing-glyph")
            .collect(Collectors.toSet());

    // https://html.spec.whatwg.org/multipage/scripting.html#prod-potentialcustomelementname
    private static String customElementRegex = "^[a-z](?:[\\-\\.0-9_a-z\\xB7\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u037D\\u037F-\\u1FFF\\u200C\\u200D\\u203F\\u2040\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]|[\\uD800-\\uDB7F][\\uDC00-\\uDFFF])*-(?:[\\-\\.0-9_a-z\\xB7\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u037D\\u037F-\\u1FFF\\u200C\\u200D\\u203F\\u2040\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]|[\\uD800-\\uDB7F][\\uDC00-\\uDFFF])*";

    private CustomElementNameValidator() {
    }

    /**
     * Validate that given name is a valid Custom Element name.
     * 
     * @param name
     *            Name to validate
     * @return Result containing possible validation error and/or warning
     */
    public static Result validate(String name) {
        return new Result(hasError(name));
    }

    // On the client we do not test for uppercase as the tag will always be
    // in uppercase when gotten from the element
    private static String hasError(String name) {
        String result = "";
        if (name == null || name.isEmpty()) {
            return  "Missing element name.";
        } else {
            name = name.toLowerCase();
        }

        if (!name.contains("-")) {
            result = "Custom element names must contain a hyphen. Example: unicorn-cake";
        } else if (name.matches("\\d.*")) {
            result = "Custom element names must not start with a digit.";
        } else if (name.startsWith("-")) {
            result = "Custom element names must not start with a hyphen.";
        } else if (!name.matches(customElementRegex)) {
            result = "Invalid element name.";
        } else if (reservedNames.contains(name)) {
            result = "The supplied element name is reserved and can\"t be used.\nSee: https://html.spec.whatwg.org/multipage/scripting.html#valid-custom-element-name";
        }
        return result;
    }

    /**
     * Validation result class that contains information if valid and possible
     * error and/or warning message received during validation.
     */
    public static class Result {
        private String error = "";

        /**
         * Constructor with error and warning resolution.
         * 
         * @param error
         *            Error message
         */
        protected Result(String error) {
            assert error != null;

            this.error = error;
        }

        /**
         * Get the error message for this result.
         *
         * @return error message
         */
        public String getError() {
            return error;
        }

        /**
         * Get if result is valid or not.
         * 
         * @return true for valid
         */
        public boolean isValid() {
            return error.isEmpty();
        }
    }
}
