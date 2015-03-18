package net.dorokhov.pony.web.client.control;

import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

public class ScrollFlowPanel extends FlowPanel implements HasScrollHandlers {

	public HandlerRegistration addScrollHandler(ScrollHandler aHandler) {

		sinkEvents(Event.ONSCROLL);

		return addHandler(aHandler, ScrollEvent.getType());
	}

}
