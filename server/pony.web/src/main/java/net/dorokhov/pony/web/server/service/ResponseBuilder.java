package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.InstallationDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ResponseBuilder {

	private InstallationServiceFacade installationServiceFacade;

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	public ResponseDto<Void> build() {

		ResponseDto<Void> response = new ResponseDto<>();

		response.setVersion(fetchVersion());
		response.setSuccessful(true);

		return response;
	}

	public <T> ResponseDto<T> build(T aData) {

		ResponseDto<T> response = new ResponseDto<>();

		response.setVersion(fetchVersion());
		response.setSuccessful(true);
		response.setData(aData);

		return response;
	}

	public ResponseDto<Void> build(ErrorDto aError) {

		ResponseDto<Void> response = new ResponseDto<>();

		response.setVersion(fetchVersion());
		response.setSuccessful(false);
		response.setErrors(Arrays.asList(aError));

		return response;
	}

	public ResponseDto<Void> build(List<ErrorDto> aErrors) {

		ResponseDto<Void> response = new ResponseDto<>();

		response.setVersion(fetchVersion());
		response.setSuccessful(false);
		response.setErrors(aErrors);

		return response;
	}

	private String fetchVersion() {

		InstallationDto installation = installationServiceFacade.getInstallation();

		return installation != null ? installation.getVersion() : null;
	}

}
