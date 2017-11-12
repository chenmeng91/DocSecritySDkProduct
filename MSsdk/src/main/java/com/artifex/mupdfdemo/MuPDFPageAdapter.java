package com.artifex.mupdfdemo;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MuPDFPageAdapter extends BaseAdapter {
	private final Context mContext;
	private final MuPDFCore mCore;
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();

	public MuPDFPageAdapter(Context c, MuPDFCore core) {
		mContext = c;
		mCore = core;
	}

	public int getCount() {
		return mCore.countPages();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MuPDFPageView pageView;
		if (convertView == null) {
			pageView = new MuPDFPageView(mContext, mCore, new Point(parent.getWidth(), parent.getHeight()));
		} else {
			pageView = (MuPDFPageView) convertView;
		}

		PointF pageSize = mPageSizes.get(position);
		if (pageSize != null) {
			pageView.setPage(position, pageSize);
		} else {
			pageView.blank(position);
		AsyncTask<Void,Void,PointF> sizingTask = new AsyncTask<Void,Void,PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return mCore.getPageSize(position);
				}

				@Override
				protected void onPostExecute(PointF result){
					super.onPostExecute(result);
					mPageSizes.put(position, result);
					if (pageView.getPage() == position)
						pageView.setPage(position, result);
				}
			};

			sizingTask.execute((Void)null);
		}
		return pageView;
	}
}
