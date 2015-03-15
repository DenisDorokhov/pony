package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.Modal;

import javax.inject.Inject;

public class ScanningView extends ModalViewWithUiHandlers<ScanningUiHandlers> implements ScanningPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, ScanningView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final NumberFormat PROGRESS_FORMAT = NumberFormat.getPercentFormat();

	@UiField
	Modal scanningView;

	@Inject
	public ScanningView(EventBus aEventBus) {

		super(aEventBus);

		initWidget(uiBinder.createAndBindUi(this));

		scanningView.addHiddenHandler(new ModalHiddenHandler() {
			@Override
			public void onHidden(ModalHiddenEvent evt) {
				ScanningView.this.hide();
			}
		});
	}

}
