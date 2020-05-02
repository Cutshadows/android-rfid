package com.appc72_uhf.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.appc72_uhf.app.adapter.ViewPagerAdapter;
import com.appc72_uhf.app.fragment.KeyDwonFragment;
import com.appc72_uhf.app.tools.UIHelper;
import com.appc72_uhf.app.widget.NoScrollViewPager;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

//import java.io.Reader;

/**
 * Created by Administrator on 2015-03-10.
 */
public class BaseTabFragmentActivity extends FragmentActivity {

	private final int offscreenPage = 2;

	protected ActionBar mActionBar;


	protected NoScrollViewPager mViewPager;
	protected ViewPagerAdapter mViewPagerAdapter;


	protected List<KeyDwonFragment> lstFrg = new ArrayList<KeyDwonFragment>();
	protected List<String> lstTitles = new ArrayList<String>();

	//public Reader mReader;
	public RFIDWithUHF mReader;
	private int index = 0;

	private ActionBar.Tab tab_inventory, tab_kill, tab_lock, tab_set ;
	private DisplayMetrics metrics;
	private AlertDialog dialog;
	private long[] timeArr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void initUHF() {


		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		try {
			mReader = RFIDWithUHF.getInstance();
		} catch (Exception ex) {

			toastMessage(ex.getMessage());

			return;
		}

		if (mReader != null) {
			new InitTask().execute();
		}
	}

	protected void initViewPageData() {

	}

	/**
	 * ����ActionBar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
//		return super.onCreateOptionsMenu(menu);
	}

	protected void initViewPager() {

		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), lstFrg, lstTitles);

		mViewPager = (NoScrollViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setOffscreenPageLimit(offscreenPage);
	}

	protected void initTabs() {
		for (int i = 0; i < mViewPagerAdapter.getCount() - 4; ++i) {
			mActionBar.addTab(mActionBar.newTab()
					.setText(mViewPagerAdapter.getPageTitle(i)).setTabListener(mTabListener));
		}
		tab_inventory = mActionBar.newTab().setText(mViewPagerAdapter.getPageTitle(1)).setTabListener(mTabListener);
		tab_kill = mActionBar.newTab().setText(mViewPagerAdapter.getPageTitle(2)).setTabListener(mTabListener);
		tab_lock = mActionBar.newTab().setText(mViewPagerAdapter.getPageTitle(3)).setTabListener(mTabListener);
		tab_set = mActionBar.newTab().setText(mViewPagerAdapter.getPageTitle(4)).setTabListener(mTabListener);

		//��Ӳ˵�
//        mActionBar.addTab(mActionBar.newTab().setText(getString(R.string.myMenu)).setTabListener(mTabListener));
	}


	protected ActionBar.TabListener mTabListener = new ActionBar.TabListener() {

		@Override
		public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
			if (mActionBar.getTabCount() > 1 && tab.getPosition() != 1) {
				mActionBar.removeTabAt(1);
			}
			if (tab.getPosition() == 1) {
				mViewPager.setCurrentItem(index, false);
			} else {
				mViewPager.setCurrentItem(tab.getPosition());
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

		}
	};

	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		//
		if (mActionBar.getSelectedTab().getText().equals(item.getTitle())) {
			return true;
		}
		if (mActionBar.getTabCount() > 1
				&& item.getItemId() != android.R.id.home && item.getItemId() != R.id.UHF_ver) {
			mActionBar.removeTabAt(1);
		}
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_inventory:
				index = 1;
				mActionBar.addTab(tab_inventory, true);
				break;
			case R.id.action_kill:
				index = 2;
				mActionBar.addTab(tab_kill, true);
				break;
			case R.id.action_lock:
				index = 3;
				mActionBar.addTab(tab_lock, true);
				break;
			case R.id.action_set:
				index = 4;
				mActionBar.addTab(tab_set, true);
				break;
			case R.id.UHF_ver:
				getUHFVersion();
				break;
			default:
				break;
		}

		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 139 ||keyCode == 280) {

			if (event.getRepeatCount() == 0) {

				if (mViewPager != null) {

					KeyDwonFragment sf = (KeyDwonFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
					sf.myOnKeyDwon();

				}
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void gotoActivity(Intent it) {
		startActivity(it);
	}

	public void gotoActivity(Class<? extends BaseTabFragmentActivity> clz) {
		Intent it = new Intent(this, clz);
		gotoActivity(it);
	}

	public void toastMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void toastMessage(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @author liuruifeng
	 */
	public class InitTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog mypDialog;

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return mReader.init();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mypDialog.cancel();

			if (!result) {
				Toast.makeText(BaseTabFragmentActivity.this, "init fail",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mypDialog = new ProgressDialog(BaseTabFragmentActivity.this);
			mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mypDialog.setMessage("init...");
			mypDialog.setCanceledOnTouchOutside(false);
			mypDialog.show();
		}
	}

	@Override
	protected void onDestroy() {

		if (mReader != null) {
			mReader.free();
		}
		super.onDestroy();
	}

	/**
	 * @param str
	 * @return
	 */
	public boolean vailHexInput(String str) {

		if (str == null || str.length() == 0) {
			return false;
		}

		if (str.length() % 2 == 0) {
			return StringUtility.isHexNumberRex(str);
		}

		return false;
	}

	public void getUHFVersion() {


		if(mReader!=null) {

			String rfidVer = mReader.getHardwareType();

			UIHelper.alert(this, R.string.action_uhf_ver,
					rfidVer, R.drawable.webtext);
		}
	}

}
