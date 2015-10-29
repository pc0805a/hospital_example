package com.example.hospital;

import android.database.Cursor;
import android.graphics.Color;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyHostApduService extends HostApduService {

    private static final String TAG = MyHostApduService.class.getSimpleName();
	
	private MemberInfo meberInfo;
	private MemberDayList memberDayList;
	private String memberID;
	private String imei;

	private boolean reportToastControl=false;
	private Toast reportToast;

	private String division;
	private String doctor;
	private String year	;
	private String month;
	private String day;
	private String time;
	private int num;

	private void getMemberInfo() {

		meberInfo = new MemberInfo(this);
		meberInfo.open();

		Cursor cursor = meberInfo.getAll();
		int row_num = cursor.getCount();
		if (row_num != 0) {
			cursor.moveToFirst();
			memberID = cursor.getString(1);
			imei = cursor.getString(2);
		}
	}
	private void getSelectedDayList()
	{
		memberDayList = new MemberDayList(this);
		memberDayList.open();

		Cursor cursor = memberDayList.getSpinnerSelected();
		int row_num = cursor.getCount();
		if (row_num != 0) {
			cursor.moveToFirst();
			division = cursor.getString(1);
			doctor = cursor.getString(2);
			year = cursor.getString(3);
			month = cursor.getString(4);
			day = cursor.getString(5);
			time = cursor.getString(6);
			num= cursor.getInt(7);
		}
	}

	@Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
		getMemberInfo();
        getSelectedDayList();
		if (selectAidApdu(apdu)) {
			Log.i("HCEDEMO", "Application selected");
			return getWelcomeMessage();
		}
		else {
			Log.i("HCEDEMO", "Received: " + new String(apdu));
			String[] tempMessage = new String(apdu).split(":");
			switch(Integer.parseInt(tempMessage[1]))
			{
				case 1:
					showReportToast("已報到");
					break;
			}
			return getNextMessage();
		}
	}

	private byte[] getWelcomeMessage() {
		return "This is my android HCE Demo".getBytes();
	}

	private byte[] getNextMessage() {
		return ("Message from android:" + memberID + ":" +
                                            division + ":" +
                                            doctor + ":" +
                                            year + ":" +
                                            month + ":" +
                                            day + ":" +
                                            time + ":" +
                                            num + ":" +
											imei).getBytes();
    }

	private boolean selectAidApdu(byte[] apdu) {
		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}

	public void showReportToast(String text){
		if(reportToast == null) {
			reportToast = Toast.makeText(MyHostApduService.this, text, Toast.LENGTH_LONG);
		} else {
			reportToast.setText(text);
			reportToast.setDuration(Toast.LENGTH_LONG);
		}
		reportToast.show();
	}
}

