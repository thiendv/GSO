/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.supported;

import jp.co.ricoh.ssdk.sample.function.common.Conversions;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Capability;

import java.util.List;

public final class MailAddressManualDestinationSettingSupported {
	
	private final MaxLengthSupported supportedMailAddressLength;
	private final List<String> supportedMailToCcBcc;

	static MailAddressManualDestinationSettingSupported getInstance(Capability.MailAddressInfoCapability capability) {
		if (capability == null) {
			return null;
		}
		return new MailAddressManualDestinationSettingSupported(capability);
	}
	
	private MailAddressManualDestinationSettingSupported(Capability.MailAddressInfoCapability capability) {
		supportedMailAddressLength = MaxLengthSupported.getMaxLengthSupported(capability.getMailAddressLength());
		supportedMailToCcBcc = Conversions.getList(capability.getMailToCcBccList());
	}
	
	public MaxLengthSupported getSupportedMailAddressLength() {
		return supportedMailAddressLength;
	}
	
	public List<String> getSupportedMailToCcBcc() {
		return supportedMailToCcBcc;
	}
	
}