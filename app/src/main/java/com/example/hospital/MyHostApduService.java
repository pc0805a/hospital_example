package com.example.hospital;

import android.database.Cursor;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class MyHostApduService extends HostApduService {
	
	private MemberInfo meberInfo;
	private String memberID;

	private void getMemberInfo() {

		meberInfo = new  MemberInfo(this);
		meberInfo.open();

		Cursor cursor = meberInfo.getAll();
		int row_num = cursor.getCount();
		if (row_num != 0) {
			cursor.moveToFirst();
			memberID = cursor.getString(1);
		}
	}

	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
		getMemberInfo();
		if (selectAidApdu(apdu)) {
			Log.i("HCEDEMO", "Application selected");
			return getWelcomeMessage();
		}
		else {
			Log.i("HCEDEMO", "Received: " + new String(apdu));
			return getNextMessage();
		}
	}

	private byte[] getWelcomeMessage() {
		return "This is my android HCE Demo".getBytes();
	}

	private byte[] getNextMessage() {
		return ("Message from android: " + memberID).getBytes();
	}

	private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}


}