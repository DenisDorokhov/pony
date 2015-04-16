package net.dorokhov.pony.web.client.control;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;

public class FieldAwareFormGroup extends FormGroup {

	private String fieldName;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String aFieldName) {
		fieldName = aFieldName;
	}

	public void insert(Widget w, int beforeIndex) {
		insert(w, Element.as(getElement()), beforeIndex, true);
	}

}
