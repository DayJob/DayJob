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

import android.app.DialogFragment;
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
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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
	private Intent intent;

	private static Map<String, Integer> iconItemMarker = new HashMap<String, Integer>() {
		{
			put("수리", R.drawable.fix_small);
			put("청소", R.drawable.clean_small);
			put("노가다", R.drawable.fix_small);
			put("단순노동", R.drawable.bike_small);
			put("전단지", R.drawable.poster_small);
			put("과외", R.drawable.write_small);
			put("배달", R.drawable.bike_small);
			put("전화업무", R.drawable.call_small);
		}
	};

	private static Map<String, Integer> iconItemContent = new HashMap<String, Integer>() {
		{

			put("수리", R.drawable.fix);
			put("청소", R.drawable.clean);
			put("노가다", R.drawable.fix);
			put("단순노동", R.drawable.bike);
			put("전단지", R.drawable.poster);
			put("과외", R.drawable.write);
			put("배달", R.drawable.bike);
			put("전화업무", R.drawable.call);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_task_map);

		// 객체 선언부
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mProvider = mLocMan.getBestProvider(new Criteria(), true);
		location = mLocMan.getLastKnownLocation(mProvider);
		mCoder = new Geocoder(this);
		intent = getIntent();

		tv = (TextView) findViewById(R.id.textView2);
		sv = (SearchView) findViewById(R.id.searchView1);

		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(
						intent.getDoubleExtra("lat", location.getLatitude()),
						intent.getDoubleExtra("lng", location.getLongitude())),
				15));

		sv.setIconifiedByDefault(true);
		sv.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "주소로검색"
				+ "</font>"));
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {

				try {
					addr = mCoder.getFromLocationName(query, 5);
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(addr.get(0).getLatitude(), addr.get(0)
									.getLongitude()), 15));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					Toast.makeText(FindTaskMap.this,
							"연결 대기시간 초과, 인터넷 접속상태를 확인하세요", Toast.LENGTH_LONG)
							.show();
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		try {

			addr = mCoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 5);
			tv.setText("현재위치 : " + addr.get(0).getAddressLine(0));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			Toast.makeText(this, "연결 대기시간 초과, 인터넷 접속상태를 확인하세요",
					Toast.LENGTH_LONG).show();
			tv.setText("현재위치 : 알수없음");
		}

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

						img.setImageResource(iconItemContent.get(marker
								.getTitle()));
						tv1.setText(marker.getTitle());
						tv2.setText(marker.getSnippet());

						return v;
					}
				});

		mClusterManager
				.setOnClusterClickListener(new OnClusterClickListener<TaskMarker>() {

					@Override
					public boolean onClusterClick(Cluster<TaskMarker> cluster) {
						// FragmentManager manager =
						// getSupportFragmentManager();
						// MyDialogFragment dialog = new MyDialogFragment();
						//
						// dialog.show(manager, "dialog");

						// map.animateCamera(CameraUpdateFactory.zoomIn());
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(cluster.getPosition().latitude,
										cluster.getPosition().longitude), map
										.getCameraPosition().zoom + 2), 500,
								null);

						return true;
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
								+ task.getString("phone"),
						iconItemMarker.get(task.getString("category")));

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

		private static final int MIN_CLUSTER_SIZE = 4;

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
			markerOptions.icon(BitmapDescriptorFactory.fromResource(item
					.getIcon()));
		}

		@Override
		protected void onClusterItemRendered(TaskMarker clusterItem,
				Marker marker) {
			super.onClusterItemRendered(clusterItem, marker);
			// here you have access to the marker itself
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster<TaskMarker> cluster) {
			// start clustering if at least 5 items overlap
			return cluster.getSize() > MIN_CLUSTER_SIZE;
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

	class MyDialogFragment extends android.support.v4.app.DialogFragment
			implements OnItemClickListener {

		private String[] listitems = { "item01", "item02", "item03", "item04" };
		private ListView mylist;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.dialog_fragment, null, false);
			mylist = (ListView) view.findViewById(R.id.list);

			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			return view;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_list_item_1,
					listitems);

			mylist.setAdapter(adapter);

			mylist.setOnItemClickListener(this);

		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			dismiss();
			Toast.makeText(getActivity(), listitems[position],
					Toast.LENGTH_SHORT).show();

		}

	}

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent findTask = new Intent(this, FindTask.class);
			findTask.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(findTask);
			break;
		}
	}
}
