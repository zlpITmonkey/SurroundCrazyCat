package com.example.surroundcrazycat;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;

public class CrazyCatActivity extends Activity 
{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����ʾ����ı�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.funny_crazycat);
	}

	public void onClick(View view) {
		Intent intent = new Intent(CrazyCatActivity.this, PlayActivity.class);
		startActivity(intent);
	}

}
