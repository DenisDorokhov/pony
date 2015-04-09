package net.dorokhov.pony.web.client.control;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.gwtbootstrap3.client.ui.Anchor;

public class AnchorCell extends AbstractCell<Anchor> {

	@Override
	public void render(Cell.Context aContext, Anchor aLink, SafeHtmlBuilder aBuffer) {
		aBuffer.append(SafeHtmlUtils.fromTrustedString(aLink.toString()));
	}

}
