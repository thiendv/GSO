package com.gso.hogoapi.model;

import java.io.Serializable;
import java.util.List;

public class TransferData implements Serializable{

	private List<AddressBookItem> list;

	public List<AddressBookItem> getList() {
		return list;
	}

	public void setList(List<AddressBookItem> list) {
		this.list = list;
	}

}
