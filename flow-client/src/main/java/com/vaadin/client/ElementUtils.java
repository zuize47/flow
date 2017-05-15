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
package com.vaadin.client;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.client.flow.ExecuteJavaScriptProcessor;
import com.vaadin.client.flow.StateNode;
import com.vaadin.client.flow.collection.JsArray;
import com.vaadin.client.flow.collection.JsCollections;
import com.vaadin.client.flow.collection.JsMap;
import com.vaadin.client.flow.dom.DomApi;
import com.vaadin.client.flow.nodefeature.NodeList;
import com.vaadin.client.flow.nodefeature.NodeMap;
import com.vaadin.client.flow.util.CustomElementNameValidator;
import com.vaadin.flow.shared.NodeFeatures;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.json.Json;
import elemental.json.JsonObject;

/**
 * Utility class which handles javascript execution context (see
 * ExecuteJavaScriptProcessor#getContextExecutionObject()).
 * 
 * @see ExecuteJavaScriptProcessor
 * 
 * @author Vaadin Ltd
 *
 */
public class ElementUtils {

    /**
     * Calculate the data required for server side callback to attach existing
     * element and send it to the server.
     * 
     * @param parent
     *            the parent node whose child is requested to attach
     * @param previousSibling
     *            previous sibling element
     * @param tagName
     *            the tag name of the element requested to attach
     * @param id
     *            the identifier of the server side node which is requested to
     *            be a counterpart of the client side element
     */
    public static void attachExistingElement(StateNode parent,
            Element previousSibling, String tagName, int id) {
        Element existingElement = null;
        JsArray<Node> childNodes = DomApi.wrap(parent.getDomNode())
                .getChildNodes();
        JsMap<Node, Integer> indices = JsCollections.map();
        boolean afterSibling = previousSibling == null;
        int elementIndex = -1;
        for (int i = 0; i < childNodes.length(); i++) {
            Node node = childNodes.get(i);
            indices.set(node, i);
            if (node.equals(previousSibling)) {
                afterSibling = true;
            }
            if (afterSibling && hasTag(node, tagName)) {
                existingElement = (Element) node;
                elementIndex = i;
                break;
            }
        }

        if (existingElement == null) {
            // report an error
            parent.getTree().sendExistingElementAttachToServer(parent, id, -1,
                    tagName, -1);
        } else {
            NodeList list = parent.getList(NodeFeatures.ELEMENT_CHILDREN);
            Integer existingId = null;
            int childIndex = 0;
            for (int i = 0; i < list.length(); i++) {
                StateNode stateNode = (StateNode) list.get(i);
                Node domNode = stateNode.getDomNode();
                Integer index = indices.get(domNode);
                if (index != null && index < elementIndex) {
                    childIndex++;
                }

                if (domNode.equals(existingElement)) {
                    existingId = stateNode.getId();
                    break;
                }
            }

            existingId = getExistingIdOrUpdate(parent, id, existingElement,
                    existingId);

            parent.getTree().sendExistingElementAttachToServer(parent, id,
                    existingId, existingElement.getTagName(), childIndex);
        }
    }

    private static boolean hasTag(Node node, String tag) {
        return node instanceof Element
                && tag.equalsIgnoreCase(((Element) node).getTagName());
    }

    /**
     * Find element for given id and collect data required for server side
     * callback to attach existing element and send it to the server.
     *
     * @param parent
     *            the parent node containing the shadow root containing the
     *            element requested to attach
     * @param tagName
     *            the tag name of the element requested to attach
     * @param serverSideId
     *            the identifier of the server side node which is requested to
     *            be a counterpart of the client side element
     * @param id
     *            the id attribute of the element to wire to
     */
    public static void attachExistingElementById(StateNode parent,
            String tagName, int serverSideId, String id) {
        Element existingElement = getDomElementById(
                (Element) parent.getDomNode(), id);

        if (existingElement != null && hasTag(existingElement, tagName)) {
            NodeMap map = parent.getMap(NodeFeatures.SHADOW_ROOT_DATA);
            StateNode shadowRootNode = (StateNode) map
                    .getProperty(NodeFeatures.SHADOW_ROOT).getValue();
            NodeList list = shadowRootNode
                    .getList(NodeFeatures.ELEMENT_CHILDREN);
            Integer existingId = null;

            for (int i = 0; i < list.length(); i++) {
                StateNode stateNode = (StateNode) list.get(i);
                Node domNode = stateNode.getDomNode();

                if (domNode.equals(existingElement)) {
                    existingId = stateNode.getId();
                    break;
                }
            }

            existingId = getExistingIdOrUpdate(shadowRootNode, serverSideId,
                    existingElement, existingId);

            // Return this as attach to parent which will delegate it to the
            // underlying shadowRoot as a virtual child.
            parent.getTree().sendExistingElementWithIdAttachToServer(parent,
                    serverSideId, existingId, existingElement.getTagName(), id);
        } else {
            parent.getTree().sendExistingElementWithIdAttachToServer(parent,
                    serverSideId, -1, tagName, id);
        }
    }

    private static native Element getDomElementById(Element shadowRootParent,
            String id) /*-{
        if (shadowRootParent.shadowRoot) {
            return shadowRootParent.shadowRoot.getElementById(id);
        }
        return null;
    }-*/;

    private static Integer getExistingIdOrUpdate(StateNode parent,
            int serverSideId, Element existingElement, Integer existingId) {
        if (existingId == null) {
            ExistingElementMap map = parent.getTree().getRegistry()
                    .getExistingElementMap();
            Integer fromMap = map.getId(existingElement);
            if (fromMap == null) {
                map.add(serverSideId, existingElement);
                return serverSideId;
            }
            return fromMap;
        }
        return existingId;
    }

    private static native Element getShadowRoot(Element shadowRootParent) /*-{
        if (shadowRootParent.shadowRoot) {
            return shadowRootParent.shadowRoot;
        }
        return null;
    }-*/;

    public static void checkForCustomElements(StateNode parent) {
        if(parent.getDomNode() == null) {
            return;
        }
        Element shadowRoot = getShadowRoot((Element) parent.getDomNode());

        List<Element> nodes = walkNodes((Element) shadowRoot, false);
        List<Element> unlinkedElements = new ArrayList<>();

        NodeMap map = parent.getMap(NodeFeatures.SHADOW_ROOT_DATA);
        StateNode shadowRootNode = (StateNode) map
                .getProperty(NodeFeatures.SHADOW_ROOT).getValue();

        if (shadowRootNode == null) {
            return;
        }

        for (Element node : nodes) {
            String name = node.getTagName();
            if (CustomElementNameValidator.validate(name).isValid()
                    && !ElementUtils.elementIdBound(node, shadowRootNode)) {
                unlinkedElements.add(node);
            }
        }

        for (Element e : unlinkedElements) {
            Console.log(e);
            if (!e.hasAttribute("id")) {
                e.setAttribute("id", guidGenerator());
            }
            JsonObject object = Json.createObject();
            object.put("tag", e.getTagName());
            object.put("id", e.getAttribute("id"));

            parent.getTree().sendEventToServer(parent, "bind-custom-element",
                    object);
        }
    }

    private static native String guidGenerator() /*-{
        var S4 = function() {
            return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
        };
        return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    }-*/;

    private static List<Element> walkNodes(Element node, boolean add) {
        List<Element> nodes = new ArrayList<>(0);
        if (add) {
            nodes.add(node);
        }

        elemental.dom.NodeList childNodes = node.getChildNodes();
        if (childNodes.length() > 0) {
            for (int i = 0; i < childNodes.length(); i++) {
                nodes.addAll(walkNodes((Element) childNodes.item(i), true));
            }
        }
        return nodes;
    }

    private static boolean elementIdBound(Element element, StateNode parent) {
        ExistingElementMap map = parent.getTree().getRegistry()
                .getExistingElementMap();
        return map.getId(element) != null;
    }
}
