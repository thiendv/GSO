/*
 *  Copyright (C) 2013 RICOH Co.,LTD.
 *  All rights reserved.
 */
package jp.co.ricoh.ssdk.sample.function.scan.supported;

import jp.co.ricoh.ssdk.sample.function.common.Conversions;
import jp.co.ricoh.ssdk.sample.wrapper.rws.service.scanner.Capability;

public final class MaxBroadcastNumberSupported {
	
	private final int supportedMails;
	private final int supportedManualMails;
	private final int supportedFolders;
	private final int supportedManualFolders;
	private final int supportedTotals;
	private final int supportedManualTotals;
	
	public static MaxBroadcastNumberSupported getInstance(Capability.MaxBroadcastNumberCapability capability) {
		if (capability == null) {
			return null;
		}
		return new MaxBroadcastNumberSupported(capability);
	}
	
	MaxBroadcastNumberSupported(Capability.MaxBroadcastNumberCapability capability) {
		supportedMails			= Conversions.getIntValue(capability.getMail(), 0);
		supportedManualMails	= Conversions.getIntValue(capability.getManualMail(), 0);
		supportedFolders		= Conversions.getIntValue(capability.getFolder(), 0);
		supportedManualFolders	= Conversions.getIntValue(capability.getManualFolder(), 0);
		supportedTotals			= Conversions.getIntValue(capability.getTotal(), 0);
		supportedManualTotals	= Conversions.getIntValue(capability.getManualTotal(), 0);
	}
	
	public int getSupportedMails() {
		return supportedMails;
	}
	
	public int getSupportedManualMails() {
		return supportedManualMails;
	}
	
	public int getSupportedFolders() {
		return supportedFolders;
	}
	
	public int getSupportedManualFolders() {
		return supportedManualFolders;
	}
	
	public int getSupportedTotals() {
		return supportedTotals;
	}
	
	public int getSupportedManualTotals() {
		return supportedManualTotals;
	}
	
}
