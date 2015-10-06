package com.example.hospital;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.grundid.hcedemo.R;


public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private Button m_btn_1;
	private Button m_btn_2;
	private Button m_btn_3;

	private void initViews() {
		m_btn_1 = (Button)findViewById(R.id.main_button_1);
		m_btn_2 = (Button)findViewById(R.id.main_button_2);
		m_btn_3 = (Button)findViewById(R.id.main_button_3);
	}

	private void setListeners() {
		m_btn_2.setOnClickListener(m_btn_2_activity);
	}

	private View.OnClickListener m_btn_2_activity = new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this,  NFCReportActivity.class);
			startActivity(intent);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
		setListeners();
	}

}
