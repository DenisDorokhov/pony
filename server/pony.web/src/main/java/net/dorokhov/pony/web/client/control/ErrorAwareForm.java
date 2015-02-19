package net.dorokhov.pony.web.client.control;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.message.Messages;
import net.dorokhov.pony.web.client.util.ErrorUtils;
import net.dorokhov.pony.web.shared.ErrorDto;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.html.Strong;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

import java.util.*;

public class ErrorAwareForm extends Form {

	private List<ErrorDto> errors;

	public List<ErrorDto> getErrors() {

		if (errors == null) {
			errors = new ArrayList<>();
		}

		return errors;
	}

	public void setErrors(List<ErrorDto> aErrors) {

		errors = aErrors;

		updateErrors();
	}

	public void updateErrors() {

		Controls controls = new Controls();

		findAndResetControls(controls, this);

		for (ErrorDto error : getErrors()) {

			FieldAwareFormGroup formGroup = controls.getFormGroups().get(error.getField());

			if (formGroup != null) {

				formGroup.setValidationState(ValidationState.ERROR);

				HelpBlock helpBlock = controls.getHelpBlocks().get(error.getField());

				if (helpBlock == null) {

					helpBlock = new HelpBlock();

					formGroup.add(helpBlock);
				}

				helpBlock.setText(ErrorUtils.formatError(error));

			} else {

				Alert alert = controls.getAlert();

				if (alert == null) {

					alert = new Alert();

					insert(alert, Element.as(getElement()), 0, true);
				}

				alert.setVisible(true);
				alert.setType(AlertType.DANGER);

				if (alert.getWidgetCount() == 0) {
					alert.add(new Strong(Messages.INSTANCE.errorsHeader()));
					alert.add(new UnorderedList());
				}

				((UnorderedList)alert.getWidget(1)).add(new ListItem(ErrorUtils.formatError(error)));
			}
		}
	}

	private void findAndResetControls(Controls aControls, ComplexPanel aPanel) {

		for (int i = 0; i < aPanel.getWidgetCount(); i++) {

			Widget widget = aPanel.getWidget(i);

			if (widget instanceof FieldAwareFormGroup) {

				FieldAwareFormGroup formGroup = (FieldAwareFormGroup) widget;

				if (formGroup.getFieldName() != null) {

					formGroup.setValidationState(ValidationState.NONE);

					aControls.getFormGroups().put(formGroup.getFieldName(), formGroup);

					HelpBlock helpBlock = findHelpBlock(formGroup);

					if (helpBlock != null) {

						helpBlock.setText("");

						aControls.getHelpBlocks().put(formGroup.getFieldName(), helpBlock);
					}
				}

			} else if (widget instanceof Alert) {

				Alert alert = (Alert)widget;

				alert.setVisible(false);
				alert.clear();

				aControls.setAlert(alert);

			} else if (widget instanceof ComplexPanel) {
				findAndResetControls(aControls, (ComplexPanel) widget);
			}
		}
	}

	private HelpBlock findHelpBlock(FieldAwareFormGroup aFormGroup) {

		for (int i = 0; i < aFormGroup.getWidgetCount(); i++) {

			Widget widget = aFormGroup.getWidget(i);

			if (widget instanceof HelpBlock) {
				return (HelpBlock)widget;
			}
		}

		return null;
	}

	private class Controls {

		private Alert alert;

		private Map<String, FieldAwareFormGroup> formGroups;

		private Map<String, HelpBlock> helpBlocks;

		public Alert getAlert() {
			return alert;
		}

		public void setAlert(Alert aAlert) {
			alert = aAlert;
		}

		public Map<String, FieldAwareFormGroup> getFormGroups() {

			if (formGroups == null) {
				formGroups = new HashMap<>();
			}

			return formGroups;
		}

		public void setFormGroups(Map<String, FieldAwareFormGroup> aFormGroups) {
			formGroups = aFormGroups;
		}

		public Map<String, HelpBlock> getHelpBlocks() {

			if (helpBlocks == null) {
				helpBlocks = new HashMap<>();
			}

			return helpBlocks;
		}

		public void setHelpBlocks(Map<String, HelpBlock> aHelpBlocks) {
			helpBlocks = aHelpBlocks;
		}

	}

}
