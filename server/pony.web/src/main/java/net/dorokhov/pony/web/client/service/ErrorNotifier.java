package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.ErrorDto;

import java.util.List;

public interface ErrorNotifier {

	public void notifyOfErrors(List<ErrorDto> aErrors);

}
