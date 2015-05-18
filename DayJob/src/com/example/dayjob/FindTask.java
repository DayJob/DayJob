package com.example.dayjob;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FindTask extends Activity {

	private ArrayList<MyTask> alist;
	private ListView list;
	private TaskAdapter adapter;
	private ArrayList<String> arraylist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_task);

		alist = new ArrayList<MyTask>();

		MyAsync myAsync = new MyAsync();
		myAsync.execute("http://www.feering.zc.bz/testTaskSelect.php");

		arraylist = new ArrayList<String>();

		arraylist.add("카테고리");

		arraylist.add("거리순");

		arraylist.add("보수순");

		arraylist.add("등록순");

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

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

	}

	private class MyAsync extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... urls) {

			StringBuilder jsonHtml = new StringBuilder();

			try {

				// 연결 url 설정
				URL url = new URL(urls[0]);
				// 커넥션 객체 생성
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// 연결되었으면.
				if (conn != null) {
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					// 연결되었음 코드가 리턴되면.
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(conn.getInputStream(),
										"UTF-8"));
						for (;;) {
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
							String line = br.readLine();
							if (line == null)
								break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}
					conn.disconnect();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return jsonHtml.toString();

		}

		protected void onPostExecute(String str) {

			try {

				JSONArray ja = new JSONArray(str);

				for (int i = 0; i < ja.length(); i++) {
					JSONObject task = ja.getJSONObject(i);

					alist.add(new MyTask(task.getString("pay"), task
							.getString("description"), task
							.getString("location"), task.getString("time"),
							task.getString("phone"), task.getString("category")));
					adapter = new TaskAdapter(FindTask.this, alist,
							R.layout.task);
					list = (ListView) findViewById(R.id.listView1);
					list.setAdapter(adapter);

				}

			} catch (JSONException e) {
				e.printStackTrace();

				Toast.makeText(FindTask.this,
						"데이터를 받아올 수 없습니다. 인터넷 연결 상태를 확인하세요.", Toast.LENGTH_LONG)
						.show();

			}

		}
	}

	class TaskAdapter extends BaseAdapter {
		Context context; // 아답터를 어디에 붙일건가..
		LayoutInflater inflater; // Dog.xml 뷰를 확장하기 위한 것
		ArrayList<MyTask> alist;
		int layout;

		// 생성자
		public TaskAdapter(Context context, ArrayList<MyTask> alist, int layout) {
			super();
			this.context = context;
			this.alist = alist;
			this.layout = layout;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return alist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return alist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(layout, parent, false);
			}
			// ImageView img = (ImageView)
			// convertView.findViewById(R.id.imageView1);
			// img.setImageResource(alist.get(position).category);
			TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
			tv1.setText("보수 : " + alist.get(position).pay);
			TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
			tv2.setText("설명 : " + alist.get(position).description);
			TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);
			tv3.setText("시간 : " + alist.get(position).time);
			TextView tv4 = (TextView) convertView.findViewById(R.id.textView4);
			tv4.setText("연락처 : " + alist.get(position).phone);

			final double[] latlng = getLatlng(alist.get(position).location);

			Button btn = (Button) convertView.findViewById(R.id.button1);

			btn.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.button1:

						Intent findTaskMap = new Intent(FindTask.this,
								FindTaskMap.class);
						findTaskMap.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						findTaskMap.putExtra("lat", latlng[0]);
						findTaskMap.putExtra("lng", latlng[1]);
						startActivity(findTaskMap);

						break;
					}
				}

			});

			return convertView;
		}

		public double[] getLatlng(String str) {

			String[] temp = new String(str).split(",");

			double[] latlng = new double[temp.length];

			int counter = 0;

			for (String s : temp) {
				latlng[counter] = Double.parseDouble(s);
				counter++;
			}

			return latlng;
		}
	}

}

class MyTask {
	String pay;
	String description;
	String location;
	String time;
	String phone;
	String category;

	public MyTask(String pay, String description, String location, String time,
			String phone, String category) {
		super();
		this.pay = pay;
		this.description = description;
		this.location = location;
		this.time = time;
		this.phone = phone;
		this.category = category;
	}

}
