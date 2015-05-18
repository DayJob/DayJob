package com.example.dayjob;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddTask extends Activity {

	String pay, description, location, time, phone, category;
	private ArrayList<String> arraylist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);

		Intent intent = getIntent();

		EditText et = (EditText) findViewById(R.id.editText3);

		location = intent.getStringExtra("latitude") + ", "
				+ intent.getStringExtra("longitude");

		arraylist = new ArrayList<String>();

		arraylist.add("수리");

		arraylist.add("청소");

		arraylist.add("노가다");

		arraylist.add("단순노동");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,

		android.R.layout.simple_spinner_dropdown_item, arraylist);

		// 스피너 속성

		Spinner sp = (Spinner) this.findViewById(R.id.spinner1);

		sp.setPrompt("골라봐"); // 스피너 제목

		sp.setAdapter(adapter);

		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				category = arraylist.get(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Toast.makeText(this, "취소했습니다.", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.button2:

			// 사용자가 입력한 내용을 전역변수에 저장한다
			pay = ((EditText) (findViewById(R.id.editText1))).getText()
					.toString();
			description = ((EditText) (findViewById(R.id.editText2))).getText()
					.toString();
			// location = ((EditText) (findViewById(R.id.editText3))).getText()
			// .toString();
			time = ((EditText) (findViewById(R.id.editText4))).getText()
					.toString();
			phone = ((EditText) (findViewById(R.id.editText5))).getText()
					.toString();
//			category = ((EditText) (findViewById(R.id.spinner1))).getText()
//					.toString();

			if (pay.equals("") || description.equals("") || location.equals("")
					|| time.equals("") || phone.equals("")
					|| category.equals("")) {

				Toast.makeText(this, "양식을 모두 채워주세요", Toast.LENGTH_SHORT).show();

				break;

			} else {

				new SendPost().execute(); // 서버와 자료 주고받기

				Toast.makeText(this, "등록했습니다. 찾아보기버튼을 눌러서 확인해보세요.",
						Toast.LENGTH_SHORT).show();
				((EditText) (findViewById(R.id.editText1))).setText(pay);

				finish();
				break;

			}

		}

	}

	private class SendPost extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {

			String content = executeClient();
			return content;
		}

		protected void onPostExecute(String result) {

		}

		// 실제 전송하는 부분
		public String executeClient() {
			try {

				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("pay", pay));

				params.add(new BasicNameValuePair("description", description));

				params.add(new BasicNameValuePair("location", location));

				params.add(new BasicNameValuePair("time", time));

				params.add(new BasicNameValuePair("phone", phone));

				params.add(new BasicNameValuePair("category", category));

				HttpClient client = new DefaultHttpClient();

				HttpPost post = new HttpPost(
						"http://www.feering.zc.bz/testTaskInsert.php");

				// 추가하고 싶으면 add를 써서 추가하면되고

				// BasicNameValuePair은 key와 value로 되어 있어서 바로 위처럼 사용해주면된다.
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);

				post.setEntity(ent);

				HttpResponse responsePOST = client.execute(post);

				HttpEntity resEntity = responsePOST.getEntity();

				// POST로 보낸 값들은 요청하여 확인작업

				if (resEntity != null) {

					Log.i("RESPONSE", EntityUtils.toString(resEntity));

					// 보낸 값들을 로그를 찍어서 확인해준다.

				}

			} catch (UnsupportedEncodingException e) {

				// TODO Auto-generated catch block

				e.printStackTrace();

			} catch (IOException e) {

				// TODO Auto-generated catch block

				e.printStackTrace();

			}

			return null;

		}

	}
}
