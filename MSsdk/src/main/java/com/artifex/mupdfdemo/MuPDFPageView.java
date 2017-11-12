package com.artifex.mupdfdemo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;

public class MuPDFPageView extends PageView {
	private final MuPDFCore mCore;
	public MuPDFPageView(Context c, MuPDFCore core, Point parentSize) {
		super(c, parentSize);
		mCore = core;
	}
	@Override
	protected Bitmap drawPage(int sizeX, int sizeY,
							  int patchX, int patchY, int patchWidth, int patchHeight) {
		return mCore.drawPage(mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
	}


	@Override
	public void setPage(final int page, PointF size) {
		super.setPage(page, size);
	}

	@Override
	public void releaseResources(){
		super.releaseResources();
	}
}
