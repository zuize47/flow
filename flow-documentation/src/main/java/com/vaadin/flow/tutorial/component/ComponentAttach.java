package com.vaadin.flow.tutorial.component;

import java.util.EventObject;
import java.util.function.Consumer;

import com.vaadin.ui.event.Tag;
import com.vaadin.flow.tutorial.annotations.CodeFor;
import com.vaadin.ui.common.AttachEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.common.DetachEvent;

@CodeFor("creating-components/tutorial-component-with-dependencies.asciidoc")
public class ComponentAttach {

    interface I18N {
        static String getTranslation(String localeName, String key,
                String... params) {
            return "";
        }
    }

    interface ShopEventBus {
        void register(Consumer<EventObject> eventHandler);

        void unregister(Consumer<EventObject> eventHandler);
    }

    @Tag("div")
    public class UserNameLabel extends Component {

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            // user name can be stored to session after login
            //@formatter:off - custom line wrapping
            String userName = (String) attachEvent.getSession().getAttribute("username");
            getElement().setText("Hello " + userName + ", welcome back!");
            //@formatter:on
        }
    }

    @Tag("div")
    public class ShoppingCartSummaryLabel extends Component {

        private final Consumer<EventObject> eventHandler = this::onCartSummaryUpdate;

        @Override
        protected void onAttach(AttachEvent attachEvent) {
            //@formatter:off - custom line wrapping
            ShopEventBus eventBus = attachEvent.getSession().getAttribute(ShopEventBus.class);
            //@formatter:on
            // registering to event bus for updates from other components
            eventBus.register(eventHandler);
        }

        @Override
        protected void onDetach(DetachEvent detachEvent) {
            // @formatter:off
            ShopEventBus eventBus = detachEvent.getSession().getAttribute(ShopEventBus.class);
            // @formatter:on
            // after detaching don't need any updates
            eventBus.unregister(eventHandler);
        }

        private void onCartSummaryUpdate(EventObject event) {
            // update cart summary ...
        }
    }
}
