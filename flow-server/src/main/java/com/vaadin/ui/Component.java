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
package com.vaadin.ui;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementUtil;
import com.vaadin.flow.dom.ShadowRoot;
import com.vaadin.shared.Registration;
import com.vaadin.ui.common.AttachEvent;
import com.vaadin.ui.common.AttachNotifier;
import com.vaadin.ui.common.DetachEvent;
import com.vaadin.ui.common.DetachNotifier;
import com.vaadin.ui.common.HasElement;
import com.vaadin.ui.common.PropertyDescriptor;
import com.vaadin.ui.common.PropertyDescriptors;
import com.vaadin.ui.event.ComponentEvent;
import com.vaadin.ui.event.ComponentEventBus;
import com.vaadin.ui.event.ComponentEventListener;
import com.vaadin.ui.event.ComponentEventNotifier;
import com.vaadin.ui.event.Tag;
import com.vaadin.util.AnnotationReader;

/**
 * A Component is a higher level abstraction of an {@link Element} or a
 * hierarchy of {@link Element}s.
 * <p>
 * A component must have exactly one root element which is created based on the
 * {@link Tag} annotation of the sub class (or in special cases set using the
 * constructor {@link #Component(Element)} or using
 * {@link #setElement(Component, Element)} before the element is attached to a
 * parent). The root element cannot be changed once it has been set.
 *
 * @author Vaadin Ltd
 */
public abstract class Component implements HasElement, Serializable,
        ComponentEventNotifier, AttachNotifier, DetachNotifier {

    /**
     * Encapsulates data required for mapping a new component instance to an
     * existing element.
     */
    static class MapToExistingElement {
        Element element = null;
        private boolean mapElementToComponent = false;

        public MapToExistingElement(Element element,
                boolean mapElementToComponent) {
            this.element = element;
            this.mapElementToComponent = mapElementToComponent;
        }

    }

    private static final PropertyDescriptor<String, Optional<String>> idDescriptor = PropertyDescriptors
            .optionalAttributeWithDefault("id", "");

    /**
     * Contains information about the element which should be used the next time
     * a component class is instantiated.
     */
    static ThreadLocal<MapToExistingElement> elementToMapTo = new ThreadLocal<>();

    private Element element;

    private ComponentEventBus eventBus = null;

    /**
     * Creates a component instance with an element created based on the
     * {@link Tag} annotation of the sub class.
     * <p>
     * If this is invoked through {@link #from(Element, Class)} or
     * {@link Element#as(Class)}, uses the element defined in those methods
     * instead of creating a new element.
     */
    protected Component() {
        Optional<String> tagNameAnnotation = AnnotationReader
                .getAnnotationFor(getClass(), Tag.class).map(Tag::value);
        if (!tagNameAnnotation.isPresent()) {
            throw new IllegalStateException(getClass().getSimpleName()
                    + " (or a super class) must be annotated with @"
                    + Tag.class.getName()
                    + " if the default constructor is used.");
        }

        String tagName = tagNameAnnotation.get();
        if (tagName.isEmpty()) {
            throw new IllegalStateException("@" + Tag.class.getSimpleName()
                    + " value cannot be empty.");
        }

        if (elementToMapTo.get() != null) {
            mapToElement(tagName);
        } else {
            Element e = new Element(tagName, false);
            setElement(this, e);
        }
    }

    /**
     * Creates a component instance based on the given element.
     * <p>
     * For nearly all cases you want to pass an element reference but it is
     * possible to pass {@code null} to this method. If you pass {@code null}
     * you must ensure that the element is initialized using
     * {@link #setElement(Component, Element)} before {@link #getElement()} is
     * used.
     *
     * @param element
     *            the root element for the component
     */
    protected Component(Element element) {
        if (elementToMapTo.get() != null) {
            mapToElement(element == null ? null : element.getTag());
        } else if (element != null) {
            setElement(this, element, true);
        }
    }

    /**
     * Configures synchronized properties based on given annotations.
     */
    private void configureSynchronizedProperties() {
        ComponentUtil.getSynchronizedProperties(getClass())
                .forEach(getElement()::addSynchronizedProperty);
        ComponentUtil.getSynchronizedPropertyEvents(getClass())
                .forEach(getElement()::addSynchronizedPropertyEvent);
    }

    private void mapToElement(String tagName) {
        MapToExistingElement wrapData = elementToMapTo.get();
        assert wrapData != null;

        // Clear to be sure that the element is only used for one component
        elementToMapTo.remove();

        // Sanity check: validate that tag name matches
        String elementTag = wrapData.element.getTag();
        if (tagName != null && !tagName.equalsIgnoreCase(elementTag)) {
            throw new IllegalArgumentException("A component specified to use a "
                    + tagName + " element cannot use an element with tag name "
                    + elementTag);
        }
        setElement(this, wrapData.element, wrapData.mapElementToComponent);
    }

    /**
     * Gets the root element of this component.
     * <p>
     * Each component must have exactly one root element. When the component is
     * attached to a parent component, this element is attached to the parent
     * component's element hierarchy.
     *
     * @return the root element of this component
     */
    @Override
    public Element getElement() {
        assert element != null : "getElement() must not be called before the element has been set";
        return element;
    }

    /**
     * Initializes the root element of a component.
     * <p>
     * Each component must have a root element and it must be set before the
     * component is attached to a parent. The root element of a component cannot
     * be changed once it has been set.
     * <p>
     * Typically you do not want to call this method but define the element
     * through {@link #Component(Element)} instead.
     *
     * @param element
     *            the root element of the component
     * @param mapElementToComponent
     *            <code>true</code> to map the element to the component in
     *            addition to mapping the component to the element,
     *            <code>false</code> to only map the component to the element
     */
    private static void setElement(Component component, Element element,
            boolean mapElementToComponent) {
        if (component.element != null) {
            throw new IllegalStateException("Element has already been set");
        }
        if (element == null) {
            throw new IllegalArgumentException("Element must not be null");
        }
        component.element = element;
        if (mapElementToComponent) {
            ElementUtil.setComponent(element, component);
            component.configureSynchronizedProperties();
        }
    }

    /**
     * Initializes the root element of a component.
     * <p>
     * Each component must have a root element and it must be set before the
     * component is attached to a parent. The root element of a component cannot
     * be changed once it has been set.
     * <p>
     * Typically you do not want to call this method but define the element
     * through {@link #Component(Element)} instead.
     *
     * @param component
     *            the component to set the root element to
     * @param element
     *            the root element of the component
     */
    protected static void setElement(Component component, Element element) {
        setElement(component, element, true);
    }

    /**
     * Gets the parent component of this component.
     * <p>
     * A component can only have one parent.
     *
     * @return an optional parent component, or an empty optional if the
     *         component is not attached to a parent
     */
    public Optional<Component> getParent() {

        // If "this" is a component inside a Composite, iterate from the
        // Composite downwards
        Optional<Component> mappedComponent = ElementUtil
                .getComponent(getElement());
        if (!mappedComponent.isPresent()) {
            throw new IllegalStateException(
                    "You cannot use getParent() on a wrapped component. Use Component.wrapAndMap to include the component in the hierarchy");
        }
        if (isInsideComposite(mappedComponent)) {
            Component parent = ComponentUtil.getParentUsingComposite(
                    (Composite<?>) mappedComponent.get(), this);
            return Optional.of(parent);
        }

        // Find the parent component based on the first parent element which is
        // mapped to a component
        return ComponentUtil.findParentComponent(getElement().getParent());
    }

    private boolean isInsideComposite(Optional<Component> mappedComponent) {
        if (!mappedComponent.isPresent()) {
            return false;
        }

        Component component = mappedComponent.get();
        return component instanceof Composite && component != this;
    }

    /**
     * Gets the child components of this component.
     * <p>
     * The default implementation finds child components by traversing each
     * child {@link Element} tree.
     *
     * @return the child components of this component
     */
    public Stream<Component> getChildren() {
        // This should not ever be called for a Composite as it will return
        // wrong results
        assert !(this instanceof Composite);

        if (!ElementUtil.getComponent(getElement()).isPresent()) {
            throw new IllegalStateException(
                    "You cannot use getChildren() on a wrapped component. Use Component.wrapAndMap to include the component in the hierarchy");
        }

        Builder<Component> childComponents = Stream.builder();
        getElement().getChildren().forEach(childElement -> {
            ComponentUtil.findComponents(childElement, component -> {
                childComponents.add(component);
            });
        });
        return childComponents.build();
    }

    /**
     * Gets the event bus for this component.
     * <p>
     * This method will create the event bus if it has not yet been created.
     *
     * @return the event bus for this component
     */
    protected ComponentEventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new ComponentEventBus(this);
        }
        return eventBus;
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {

        return getEventBus().addListener(eventType, listener);
    }

    /**
     * Checks if there is at least one listener registered for the given event
     * type for this component.
     *
     * @param eventType
     *            the component event type
     * @return <code>true</code> if at least one listener is registered,
     *         <code>false</code> otherwise
     */
    @SuppressWarnings("rawtypes")
    protected boolean hasListener(Class<? extends ComponentEvent> eventType) {
        return eventBus != null && eventBus.hasListener(eventType);
    }

    /**
     * Dispatches the event to all listeners registered for the event type.
     *
     * @param componentEvent
     *            the event to fire
     */
    protected void fireEvent(ComponentEvent<?> componentEvent) {
        if (hasListener(componentEvent.getClass())) {
            getEventBus().fireEvent(componentEvent);
        }
    }

    /**
     * Gets the UI this component is attached to.
     *
     * @return an optional UI component, or an empty optional if this component
     *         is not attached to a UI
     */
    public Optional<UI> getUI() {
        if (getParent().isPresent()) {
            return getParent().flatMap(Component::getUI);
        } else if (getElement().getParentNode() instanceof ShadowRoot) {
            Optional<Component> parent = ComponentUtil.findParentComponent(
                    ((ShadowRoot) getElement().getParentNode()).getHost());
            return parent.flatMap(Component::getUI);
        }
        return Optional.empty();
    }

    /**
     * Sets the id of the root element of this component. The id is used with
     * various APIs to identify the element, and it should be unique on the
     * page.
     *
     * @param id
     *            the id to set, or <code>null</code> to remove any previously
     *            set id
     */
    public void setId(String id) {
        set(idDescriptor, id);
    }

    /**
     * Gets the id of the root element of this component.
     *
     * @see #setId(String)
     *
     * @return the id, or <code>""</code> if no id has been set
     */
    public Optional<String> getId() {
        return get(idDescriptor);
    }

    /**
     * Called when the component is attached to a UI.
     * <p>
     * The default implementation does nothing.
     * <p>
     * This method is invoked before the {@link AttachEvent} is fired for the
     * component.
     *
     * @param attachEvent
     *            the attach event
     */
    protected void onAttach(AttachEvent attachEvent) {
        // NOOP by default
    }

    /**
     * Called when the component is detached from a UI.
     * <p>
     * The default implementation does nothing.
     * <p>
     * This method is invoked before the {@link DetachEvent} is fired for the
     * component.
     *
     * @param detachEvent
     *            the detach event
     */
    protected void onDetach(DetachEvent detachEvent) {
        // NOOP by default
    }

    /**
     * Sets the value of the given component property.
     *
     * @see PropertyDescriptor
     *
     * @param <T>
     *            type of the value to set
     * @param descriptor
     *            the descriptor for the property to set, not <code>null</code>
     * @param value
     *            the new property value to set
     */
    protected <T> void set(PropertyDescriptor<T, ?> descriptor, T value) {
        assert descriptor != null;

        descriptor.set(this, value);
    }

    /**
     * Gets the value of the given component property.
     *
     * @see PropertyDescriptor
     *
     * @param <T>
     *            type of the value to get
     * @param descriptor
     *            the descriptor for the property to set, not <code>null</code>
     * @return the property value
     */
    protected <T> T get(PropertyDescriptor<?, T> descriptor) {
        assert descriptor != null;

        return descriptor.get(this);
    }

    /**
     * Creates a new component instance using the given element.
     * <p>
     * You can use this method when you have an element instance and want to use
     * it through the API of a {@link Component} class.
     * <p>
     * This method attaches the component instance to the element so that
     * {@link Element#getComponent()} returns the component instance. This means
     * that {@link #getParent()}, {@link #getChildren()} and other methods which
     * rely on {@link Element} -&gt; {@link Component} mappings will work
     * correctly.
     * <p>
     * Note that only one {@link Component} can be mapped to any given
     * {@link Element}.
     *
     * @see Element#as(Class)
     *
     * @param <T>
     *            the component type to create
     * @param element
     *            the element to wrap
     * @param componentType
     *            the component type
     * @return the component instance connected to the given element
     */
    public static <T extends Component> T from(Element element,
            Class<T> componentType) {
        return ComponentUtil.componentFromElement(element, componentType, true);
    }

}
