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
package com.vaadin.flow.dom.impl;

import java.io.Serializable;
import java.util.stream.Stream;

import com.vaadin.flow.StateNode;
import com.vaadin.flow.nodefeature.ClientDelegateHandlers;
import com.vaadin.flow.nodefeature.ComponentMapping;
import com.vaadin.flow.nodefeature.ElementAttributeMap;
import com.vaadin.flow.nodefeature.ElementChildrenList;
import com.vaadin.flow.nodefeature.ElementClassList;
import com.vaadin.flow.nodefeature.ElementData;
import com.vaadin.flow.nodefeature.ElementListenerMap;
import com.vaadin.flow.nodefeature.ElementPropertyMap;
import com.vaadin.flow.nodefeature.ElementStylePropertyMap;
import com.vaadin.flow.nodefeature.NodeFeature;
import com.vaadin.flow.nodefeature.ParentGeneratorHolder;
import com.vaadin.flow.nodefeature.SynchronizedPropertiesList;
import com.vaadin.flow.nodefeature.SynchronizedPropertyEventsList;

/**
 * Implementation which stores data for basic elements, i.e. elements which are
 * not bound to any template and have no special functionality.
 * <p>
 * This should be considered a low level class focusing on performance and
 * leaving most sanity checks to the caller.
 * <p>
 * The data is stored directly in the state node but this should be considered
 * an implementation detail which can change.
 *
 * @author Vaadin Ltd
 */
public class BasicElementStateProvider extends AbstractElementStateProvider {

    private static BasicElementStateProvider instance = new BasicElementStateProvider();

    @SuppressWarnings("unchecked")
    private static Class<? extends NodeFeature>[] features = new Class[] {
            ElementData.class, ElementAttributeMap.class,
            ElementChildrenList.class, ElementPropertyMap.class,
            ElementListenerMap.class, ElementClassList.class,
            ElementStylePropertyMap.class, SynchronizedPropertiesList.class,
            SynchronizedPropertyEventsList.class, ComponentMapping.class,
            ParentGeneratorHolder.class, ClientDelegateHandlers.class };

    private BasicElementStateProvider() {
        // Not meant to be sub classed and only once instance should ever exist
    }

    /**
     * Gets the one and only instance.
     *
     * @return the instance to use for all basic elements
     */
    public static BasicElementStateProvider get() {
        return instance;
    }

    @Override
    public Object getProperty(StateNode node, String name) {
        assert node != null;
        assert name != null;

        return getPropertyFeature(node).getProperty(name);
    }

    @Override
    public void setProperty(StateNode node, String name, Serializable value,
            boolean emitChange) {
        assert node != null;
        assert name != null;

        getPropertyFeature(node).setProperty(name, value, emitChange);
    }

    @Override
    public void removeProperty(StateNode node, String name) {
        assert node != null;
        assert name != null;

        getPropertyFeature(node).removeProperty(name);
    }

    @Override
    public boolean hasProperty(StateNode node, String name) {
        assert node != null;
        assert name != null;

        return getPropertyFeature(node).hasProperty(name);
    }

    @Override
    public Stream<String> getPropertyNames(StateNode node) {
        assert node != null;

        return getPropertyFeature(node).getPropertyNames();
    }

    @Override
    protected Class<? extends NodeFeature>[] getProviderFeatures() {
        return features;
    }

    /**
     * Gets the property feature for the given node and asserts it is non-null.
     *
     * @param node
     *            the node
     * @return the property feature
     */
    private static ElementPropertyMap getPropertyFeature(StateNode node) {
        return node.getFeature(ElementPropertyMap.class);
    }
}
