package com.example.dayjob;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class TaskMarker implements ClusterItem {
	private final LatLng mPosition;
	private final String mTitle;
	private final String mSnippet;
	private final int icon;
	private final int small_Icon;

	public TaskMarker(double lat, double lng, String mTitle, String mSnippet,
			int icon, int small_Icon) {
		this.mPosition = new LatLng(lat, lng);
		this.mTitle = mTitle;
		this.mSnippet = mSnippet;
		this.icon = icon;
		this.small_Icon = small_Icon;

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
		return icon;
	}

	public int getSmallIcon() {
		return small_Icon;
	}

}