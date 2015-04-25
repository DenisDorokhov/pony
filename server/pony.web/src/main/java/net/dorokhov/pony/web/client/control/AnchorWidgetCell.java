package net.dorokhov.pony.web.client.control;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

public class AnchorWidgetCell extends WidgetCell {

	public AnchorWidgetCell() {
		super(CLICK);
	}

	@Override
	public void onBrowserEvent(Context aContext, Element aParent, Widget aValue, NativeEvent aEvent, ValueUpdater<Widget> aValueUpdater) {

		super.onBrowserEvent(aContext, aParent, aValue, aEvent, aValueUpdater);

		if (Element.is(aEvent.getEventTarget())) {
			if (Element.as(aEvent.getEventTarget()).getTagName().equals("A")) {
				onEnterKeyDown(aContext, aParent, aValue, aEvent, aValueUpdater);
			}
		}
	}

	@Override
	protected void onEnterKeyDown(Context aContext, Element aParent, Widget aValue, NativeEvent aEvent, ValueUpdater<Widget> aValueUpdater) {
		if (aValueUpdater != null) {
			aValueUpdater.update(aValue);
		}
	}

}
