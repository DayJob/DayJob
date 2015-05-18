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

		arraylist.add("����");

		arraylist.add("û��");

		arraylist.add("�밡��");

		arraylist.add("�ܼ��뵿");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,

		android.R.layout.simple_spinner_dropdown_item, arraylist);

		// ���ǳ� �Ӽ�

		Spinner sp = (Spinner) this.findViewById(R.id.spinner1);

		sp.setPrompt("����"); // ���ǳ� ����

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
			Toast.makeText(this, "����߽��ϴ�.", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.button2:

			// ����ڰ� �Է��� ������ ���������� �����Ѵ�
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

				Toast.makeText(this, "����� ��� ä���ּ���", Toast.LENGTH_SHORT).show();

				break;

			} else {

				new SendPost().execute(); // ������ �ڷ� �ְ�ޱ�

				Toast.makeText(this, "����߽��ϴ�. ã�ƺ����ư�� ������ Ȯ���غ�����.",
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

		// ���� �����ϴ� �κ�
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

				// �߰��ϰ� ������ add�� �Ἥ �߰��ϸ�ǰ�

				// BasicNameValuePair�� key�� value�� �Ǿ� �־ �ٷ� ��ó�� ������ָ�ȴ�.
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);

				post.setEntity(ent);

				HttpResponse responsePOST = client.execute(post);

				HttpEntity resEntity = responsePOST.getEntity();

				// POST�� ���� ������ ��û�Ͽ� Ȯ���۾�

				if (resEntity != null) {

					Log.i("RESPONSE", EntityUtils.toString(resEntity));

					// ���� ������ �α׸� �� Ȯ�����ش�.

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
