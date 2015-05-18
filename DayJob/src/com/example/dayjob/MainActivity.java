package com.example.dayjob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void mOnClick(View v) {
		switch (v.getId()) {

		case R.id.button1:
			Intent addTaskMap = new Intent(this, AddTaskMap.class);
			addTaskMap.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(addTaskMap);
			break;

		case R.id.button2:
			Intent findTaskMap = new Intent(this, FindTaskMap.class);
			findTaskMap.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(findTaskMap);
			break;

		case R.id.button3:
			Intent findTask = new Intent(this, FindTask.class);
			findTask.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(findTask);
			break;
		}
	}
}
