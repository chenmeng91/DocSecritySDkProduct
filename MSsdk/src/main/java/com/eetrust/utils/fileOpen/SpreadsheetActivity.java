/*
 * Copyright (C) Unpublished by Olivephone Co.Ltd. All rights reserved.
 * Olivephone Co.Ltd, Confidential and Proprietary.
 * Unless otherwise explicitly stated in writing, this software may not be used by or disclosed to any third party.
 * This software is subject to copyright protection under the laws of the People's Republic of China and other countries.
 * Unless otherwise explicitly stated, this software is only provided by Olivephone Co.Ltd "AS IS".
 */
package com.eetrust.utils.fileOpen;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.eetrust.utils.OpenFile;
import com.olivephone.sdk.InternalCopyListener;
import com.olivephone.sdk.PageViewController;
import com.olivephone.sdk.PageViewController.PageChangedListener;
import com.olivephone.sdk.WorkBookViewController;

import java.io.File;
import java.util.List;

import docsecuritysdk.eetrust.com.mysdk.R;

/**
 * @author SiJyun
 * 
 */
public class SpreadsheetActivity extends BaseDocumentActivity {
	private LinearLayout sheetBarContainer;
	private FrameLayout sheetContainer;

	@Override
	protected void onCreate(File file) {
		super.onCreate(file);
	}

	@Override
	protected void initDocument(File file) {
		super.initDocument(file);
		((WorkBookViewController) this.docViewController).setInternalCopyListener(new InternalCopyListener() {
			@Override
			public void onCopy(String copyText) {
				Toast.makeText(SpreadsheetActivity.this, copyText, Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	protected void initViews() {
		super.initViews();
		LayoutInflater inflater = LayoutInflater.from(this);
		View excelMainLayout = inflater.inflate(R.layout.excel_content, null);
		this.contentContainer.addView(excelMainLayout);
		this.sheetBarContainer = (LinearLayout) excelMainLayout.findViewById(R.id.sheetbar_container);
		this.sheetContainer = (FrameLayout) excelMainLayout.findViewById(R.id.sheet_container);

		this.findViewById(R.id.control_page_panel).setVisibility(View.VISIBLE);
		final PageViewController page = (PageViewController) SpreadsheetActivity.this.docViewController;
		page.setPageChangedListener(new PageChangedListener() {
			@Override
			public void onPageChanged(int pageNumber) throws Exception {
				SpreadsheetActivity.this.updatePageInfo();
			}
		});
		this.findViewById(R.id.control_goto_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				page.nextPage();
			}
		});
		this.findViewById(R.id.control_goto_prev).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				page.prevPage();
			}
		});
		this.findViewById(R.id.control_page_input_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int pageNumber = Integer.parseInt(((EditText) SpreadsheetActivity.this.findViewById(R.id.control_page_input)).getText().toString());
				boolean result = page.gotoPage(pageNumber);
				Toast.makeText(SpreadsheetActivity.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	protected void bindViewToContainer() {
		this.sheetContainer.addView(this.docView.asView());
	}

	@Override
	protected void onDocumentLoaded() {
		super.onDocumentLoaded();
		List<String> sheetsNames = ((WorkBookViewController) this.docViewController).getSheetsName();
		if (sheetsNames != null) {
			final int sheetsCount = sheetsNames.size();
			for (int i = 0; i < sheetsCount; i++) {
				String sheetName = sheetsNames.get(i);
				this.addSheet(i, sheetName);
			}
		}
		this.updatePageInfo();
	}

	private void updatePageInfo() {
		final PageViewController page = (PageViewController) SpreadsheetActivity.this.docViewController;
		int currentPage = page.getCurrentPage();
		int index = currentPage - 1;
		((TextView) SpreadsheetActivity.this.findViewById(R.id.control_page_info)).setText(currentPage + "/" + page.getPageCount());
		for (int i = 0; i < SpreadsheetActivity.this.sheetBarContainer.getChildCount(); i++) {
			View child = SpreadsheetActivity.this.sheetBarContainer.getChildAt(i);
			if (index == i) {
				child.setSelected(true);
			} else {
				child.setSelected(false);
			}
		}
	}

	@Override
	protected String copyTextInternal() {
		return ((WorkBookViewController) this.docViewController).getSelectionText();
	}

	private void addSheet(int sheetIndex, String sheetName) {
		TextView sheetBar = new TextView(this);
		sheetBar.setWidth(100);
		sheetBar.setBackgroundResource(R.drawable.excel_sheet_bar_bg);
		final int index = sheetIndex;
		sheetBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((WorkBookViewController) SpreadsheetActivity.this.docViewController).changeSheet(index);
			}
		});
		sheetBar.setText(sheetName);
		sheetBar.setTextColor(Color.BLACK);
		sheetBar.setTextSize(18);
		sheetBar.setGravity(Gravity.CENTER);
		sheetBar.setPadding(10, 0, 15, 0);
		sheetBar.setSingleLine();
		sheetBar.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		sheetBar.setSelected(index == 0);
		this.sheetBarContainer.addView(sheetBar);
	}

}
