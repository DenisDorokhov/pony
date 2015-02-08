package net.dorokhov.pony.web.client.control;

import org.gwtbootstrap3.client.ui.FormGroup;

public class FieldAwareFormGroup extends FormGroup {

	private String fieldName;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String aFieldName) {
		fieldName = aFieldName;
	}
}
