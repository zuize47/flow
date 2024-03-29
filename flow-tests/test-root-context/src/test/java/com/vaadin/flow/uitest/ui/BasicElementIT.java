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
package com.vaadin.flow.uitest.ui;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.ui.event.Tag;

public class BasicElementIT extends AbstractBasicElementComponentIT {

    // #671, #1231
    @Test
    public void testAddRemoveComponentDuringSameRequest() {
        open();
        findElement(By.id("addremovebutton")).click();

        List<WebElement> addremovecontainerChildren = findElement(
                By.id("addremovecontainer")).findElements(By.tagName("div"));
        Assert.assertEquals(2, addremovecontainerChildren.size());
        Assert.assertEquals("to-remove",
                addremovecontainerChildren.get(0).getAttribute("id"));
        Assert.assertEquals("ok",
                addremovecontainerChildren.get(1).getAttribute("id"));
        // verify the UI still works

        Assert.assertEquals(0, getThankYouCount());

        findElement(By.tagName(Tag.INPUT)).sendKeys("abc");
        findElement(By.tagName(Tag.BUTTON)).click();

        Assert.assertEquals(1, getThankYouCount());

        String buttonText = getThankYouElements().get(0).getText();
        String expected = "Thank you for clicking \"Click me\" at \\((\\d+),(\\d+)\\)! The field value is abc";
        Assert.assertTrue(
                "Expected '" + expected + "', was '" + buttonText + "'",
                buttonText.matches(expected));

        // Clicking removes the element
        getThankYouElements().get(0).click();

        Assert.assertEquals(0, getThankYouCount());
    }
}
