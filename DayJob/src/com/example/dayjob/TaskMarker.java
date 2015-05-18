package com.example.dayjob;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TaskMarker implements ClusterItem {
	private final LatLng mPosition;
	private final String mTitle;
	private final String mSnippet;
	private final int mIcon;

	public TaskMarker(double lat, double lng, String mTitle, String mSnippet,
			int mIcon) {
		mPosition = new LatLng(lat, lng);
		this.mTitle = mTitle;
		this.mSnippet = mSnippet;
		this.mIcon = mIcon;
	}

	@Override
	public LatLng getPosition() {
		return mPosition;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getSnippet() {
		return mSnippet;
	}

	public int getIcon() {
		return mIcon;
	}

}