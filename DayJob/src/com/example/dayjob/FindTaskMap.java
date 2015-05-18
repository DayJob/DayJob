package com.example.dayjob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class FindTaskMap extends FragmentActivity {

	GoogleMap map;
	Geocoder mCoder;
	private LocationManager mLocMan;
	private String mProvider;
	private JSONArray ja;
	private ClusterManager<TaskMarker> mClusterManager;
	private Location location;
	private List<Address> addr;
	private TextView tv;
	private SearchView sv;

	private static Map<String, Integer> iconItem = new HashMap<String, Integer>() {
		{
			put("수리", R.drawable.repair);
			put("청소", R.drawable.clean);
			put("노가다", R.drawable.labor);
			put("단순노동", R.drawable.repair);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_task_map);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mProvider = mLocMan.getBestProvider(new Criteria(), true);

		location = mLocMan.getLastKnownLocation(mProvider);

		mCoder = new Geocoder(this);

		try {
			addr = mCoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tv = (TextView) findViewById(R.id.textView2);

		tv.setText("현재위치 : " + addr.get(0).getAddressLine(0));

		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);

		Intent intent = getIntent();

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(
						intent.getDoubleExtra("lat", location.getLatitude()),
						intent.getDoubleExtra("lng", location.getLongitude())),
				15));

		sv = (SearchView) findViewById(R.id.searchView1);
		sv.setQueryHint("주소로 검색");
		sv.setIconifiedByDefault(true);
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {

				try {
					addr = mCoder.getFromLocationName(query, 5);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						addr.get(0).getLatitude(), addr.get(0).getLongitude()),
						15));

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		MyAsync myAsync = new MyAsync();
		myAsync.execute("http://www.feering.zc.bz/testTaskSelect.php");

	}

	private void setUpClusterer() {

		// Initialize the manager with the context and the map.
		// (Activity extends context, so we can pass 'this' in the constructor.)
		mClusterManager = new ClusterManager<TaskMarker>(this, map);

		// Point the map's listeners at the listeners implemented by the cluster
		// manager.
		map.setOnCameraChangeListener(mClusterManager);
		map.setOnMarkerClickListener(mClusterManager);

		map.setInfoWindowAdapter(mClusterManager.getMarkerManager());

		// For cluster marker
		mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(
				new InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public View getInfoContents(Marker arg0) {
						// TODO Auto-generated method stub
						return null;
					}
				});

		// For normal marker
		mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
				new InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public View getInfoContents(Marker marker) {
						View v = getLayoutInflater().inflate(
								R.layout.info_window, null);

						ImageView img = (ImageView) v
								.findViewById(R.id.imageView1);
						TextView tv1 = (TextView) v
								.findViewById(R.id.textView1);
						TextView tv2 = (TextView) v
								.findViewById(R.id.textView2);

						img.setImageResource(iconItem.get(marker.getTitle()));
						tv1.setText(marker.getTitle());
						tv2.setText(marker.getSnippet());

						return v;
					}
				});

		mClusterManager
				.setOnClusterClickListener(new OnClusterClickListener<TaskMarker>() {

					@Override
					public boolean onClusterClick(Cluster<TaskMarker> cluster) {
						// TODO Auto-generated method stub
						return false;
					}
				});

		mClusterManager
				.setOnClusterItemClickListener(new OnClusterItemClickListener<TaskMarker>() {

					@Override
					public boolean onClusterItemClick(TaskMarker marker) {
						// AlertDialog alert = new AlertDialog.Builder(
						// FindTaskMap.this)
						// .setTitle(marker.getTitle())
						// .setMessage(marker.getSnippet())
						// .setPositiveButton("신청",
						// new DialogInterface.OnClickListener() {
						//
						// @Override
						// public void onClick(
						// DialogInterface dialog,
						// int which) {
						// dialog.dismiss();
						//
						// }
						// })
						// .setNegativeButton("취소",
						// new DialogInterface.OnClickListener() {
						//
						// @Override
						// public void onClick(
						// DialogInterface dialog,
						// int which) {
						// dialog.dismiss();
						//
						// }
						// }).show();
						return false;
					}
				});

		// Add cluster items (markers) to the cluster manager.
		addItems();

	}

	private void addItems() {

		try {

			for (int i = 0; i < ja.length(); i++) {
				JSONObject task = ja.getJSONObject(i);

				double[] latlng = getLatlng(task.getString("location"));

				TaskMarker item = new TaskMarker(latlng[0], latlng[1],
						task.getString("category"), "보수: "
								+ task.getString("pay") + " \n설명 : "
								+ task.getString("description") + " \n시간 : "
								+ task.getString("time") + " \n연락처 : "
								+ task.getString("phone"), iconItem.get(task
								.getString("category")));

				mClusterManager.addItem(item);

				mClusterManager.setRenderer(new MyClusterRenderer(this, map,
						mClusterManager));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MyClusterRenderer extends DefaultClusterRenderer<TaskMarker> {

		public MyClusterRenderer(Context context, GoogleMap map,
				ClusterManager<TaskMarker> clusterManager) {
			super(context, map, clusterManager);
		}

		@Override
		protected void onBeforeClusterItemRendered(TaskMarker item,
				MarkerOptions markerOptions) {
			super.onBeforeClusterItemRendered(item, markerOptions);

			markerOptions.title(item.getTitle());
			markerOptions.snippet(item.getSnippet());
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(item.getIcon()));
		}

		@Override
		protected void onClusterItemRendered(TaskMarker clusterItem,
				Marker marker) {
			super.onClusterItemRendered(clusterItem, marker);
			// here you have access to the marker itself
		}
	}

	class MyAsync extends AsyncTask<String, Integer, String> {

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
					} else {
						Toast.makeText(FindTaskMap.this,
								"서버 상태가 불안정합니다. 잠시후 다시 시도해보세요.",
								Toast.LENGTH_SHORT).show();
					}
					conn.disconnect();
				} else {

					Toast.makeText(FindTaskMap.this,
							"네트워크 상태가 불안정합니다. 잠시후 다시 시도해보세요.",
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jsonHtml.toString();

		}

		protected void onPostExecute(String str) {

			try {

				ja = new JSONArray(str);

				// for (int i = 0; i < ja.length(); i++) {
				// JSONObject task = ja.getJSONObject(i);
				//
				// String[] temp = new String(task.getString("location"))
				// .split(",");
				//
				// double[] latlng = new double[temp.length];
				// int counter = 0;
				//
				// for (String s : temp) {
				// latlng[counter] = Double.parseDouble(s);
				// counter++;
				// }
				//
				// map.addMarker(new MarkerOptions()
				// .position(new LatLng(latlng[0], latlng[1]))
				// .title(task.getString("category"))
				// .snippet(
				// "보수: " + task.getString("pay") + " \n설명 : "
				// + task.getString("description")
				// + " \n시간 : "
				// + task.getString("time")
				// + " \n연락처 : "
				// + task.getString("phone"))
				// .icon(BitmapDescriptorFactory
				// .fromResource(R.drawable.repair)));
				//
				// }

				setUpClusterer();

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
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
