package com.example.surroundcrazycat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class PlayActivity extends Activity {

	PlayGround playground;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		playground = new PlayGround(this);
		// ����ʾ����ı�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(playground);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// playground.stopTimer();
	}

}
