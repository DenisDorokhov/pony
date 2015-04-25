package net.dorokhov.pony.web.client.control;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;

import java.util.Set;

public class WidgetCell extends AbstractCell<Widget> {

	public WidgetCell(String... aConsumedEvents) {
		super(aConsumedEvents);
	}

	public WidgetCell(Set<String> aConsumedEvents) {
		super(aConsumedEvents);
	}

	@Override
	public void render(Cell.Context aContext, Widget aWidget, SafeHtmlBuilder aBuffer) {
		aBuffer.append(SafeHtmlUtils.fromTrustedString(aWidget.toString()));
	}

}
