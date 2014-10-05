package net.dorokhov.pony.core.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ChecksumServiceImpl implements ChecksumService {

	@Override
	public String calculateChecksum(File aFile) throws IOException {
		return DigestUtils.md5Hex(new FileInputStream(aFile));
	}

	@Override
	public String calculateChecksum(byte[] aData) {
		return DigestUtils.md5Hex(aData);
	}

}
