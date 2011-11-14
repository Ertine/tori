package org.vaadin.tori.component.breadcrumbs;

import java.util.List;

import org.vaadin.hene.splitbutton.SplitButton;
import org.vaadin.hene.splitbutton.SplitButton.SplitButtonPopupVisibilityEvent;
import org.vaadin.tori.ToriApplication;
import org.vaadin.tori.data.entity.DiscussionThread;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.ListSelect;

@SuppressWarnings("serial")
class ThreadCrumb extends CustomComponent {
    public interface ThreadSelectionListener {
        void selectThread(DiscussionThread selectedThread);
    }

    private final ThreadSelectionListener listener;
    private final SplitButton crumb;

    public ThreadCrumb(final DiscussionThread thread,
            final ThreadSelectionListener listener) {
        this.listener = listener;

        if (thread == null) {
            throw new RuntimeException("Trying to render the thread part of "
                    + "the breadcrumbs, but the given thread was null");
        }

        setStyleName(Breadcrumbs.STYLE_CRUMB);
        addStyleName(Breadcrumbs.STYLE_THREAD);
        addStyleName(Breadcrumbs.STYLE_UNCLICKABLE);

        crumb = new SplitButton(thread.getTopic());
        crumb.addPopupVisibilityListener(new SplitButton.SplitButtonPopupVisibilityListener() {
            @Override
            public void splitButtonPopupVisibilityChange(
                    final SplitButtonPopupVisibilityEvent event) {
                event.getSplitButton().setComponent(getThreadPopup(thread));
            }
        });

        setCompositionRoot(crumb);
    }

    private Component getThreadPopup(final DiscussionThread thread) {
        final ListSelect root = new ListSelect();
        root.setImmediate(true);
        root.setNullSelectionAllowed(false);

        final List<DiscussionThread> threads = ToriApplication.getCurrent()
                .getDataSource().getThreads(thread.getCategory());

        for (final DiscussionThread t : threads) {
            root.addItem(t);

            String topic = t.getTopic();
            if (t.equals(thread)) {
                topic = "> " + topic;
            }

            root.setItemCaption(t, topic);
        }

        root.setValue(thread);
        root.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                if (listener != null) {
                    final DiscussionThread value = (DiscussionThread) event
                            .getProperty().getValue();
                    listener.selectThread(value);
                }
                crumb.setPopupVisible(false);
            }
        });

        return root;
    }
}