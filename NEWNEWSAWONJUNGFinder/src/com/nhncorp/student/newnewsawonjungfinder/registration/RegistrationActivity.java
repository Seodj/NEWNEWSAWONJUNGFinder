package com.nhncorp.student.newnewsawonjungfinder.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.nhncorp.student.newnewsawonjungfinder.MainActivity;
import com.nhncorp.student.newnewsawonjungfinder.R;
import com.nhncorp.student.newnewsawonjungfinder.R.drawable;
import com.nhncorp.student.newnewsawonjungfinder.database.DbGetSet;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

public class RegistrationActivity extends Activity {

	private CentralManager centralManager;
	private ImageButton cardNamingBtn;

	private boolean registrationState = false;

	private Peripheral peripheralValue;

	private DbGetSet dbGetSet;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				centralManager.stopScanning();
				RegistrationActivity.this.finish();
			default:
			}
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);
		init();
	}

	private void init() {
		getView();
		setCentralManager(this.getApplicationContext());
		dbGetSet = new DbGetSet(this);
		addListener();

	}

	private void addListener() {
		cardNamingBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dbGetSet.setRegistration(peripheralValue.getBDAddress());
				Intent intent = new Intent(RegistrationActivity.this,
						MainActivity.class);
				if (registrationState == true) {
					registrationState = false;
					startActivity(intent);
					RegistrationActivity.this.finish();
				}
			}
		});

	}

	private void getView() {
		cardNamingBtn = (ImageButton) findViewById(R.id.cardNamingBtn);
	}

	private void setCentralManager(Context context) {
		centralManager = CentralManager.getInstance();
		centralManager.init(context);
		centralManager.setPeripheralScanListener(new PeripheralScanListener() {
			@Override
			public void onPeripheralScan(Central central,
					final Peripheral peripheral) {

				System.out.println("onPeripheralScan() : peripheral : "
						+ peripheral); // /////////////////////////////////////////////////////

				runOnUiThread(new Runnable() {
					public void run() {
						if (peripheral.isIBeacon())
							setView(peripheral);
					}
				});

			}

		});
		centralManager.startScanning();
	}

	private void setView(Peripheral peripheral) {
		if (peripheral.getDistance() < 0.04 && registrationState == false) {
			cardNamingBtn.setImageResource(drawable.registration2);
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(500);
			peripheralValue = peripheral;
			registrationState = true;

		} else if (registrationState == true) {
			centralManager.stopScanning();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cardNamingBtn.setImageResource(drawable.registration_btn);
		}

	}

}