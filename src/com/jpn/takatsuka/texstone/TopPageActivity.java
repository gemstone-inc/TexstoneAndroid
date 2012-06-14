package com.jpn.takatsuka.texstone;

import java.util.Date;
import java.util.List;

import jp.Adlantis.Android.AdlantisView;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.jpn.takatsuka.texstone.R;
import com.jpn.takatsuka.texstone.data.Category;
import com.jpn.takatsuka.texstone.data.DatabaseHelper;
import com.jpn.takatsuka.texstone.data.FileReadUtil;
import com.jpn.takatsuka.texstone.data.HttpUtil;
import com.jpn.takatsuka.texstone.data.NetworkUtil;
import com.jpn.takatsuka.texstone.data.OrmDBAdapter;
import com.jpn.takatsuka.texstone.data.Registry;
import com.jpn.takatsuka.texstone.data.UpdateDataPack;


public class TopPageActivity extends OrmLiteBaseActivity<DatabaseHelper> implements AdapterView.OnItemClickListener, AdWhirlInterface{

	
	private OrmDBAdapter ormdb;


	private final String TAG = Config.makeLogTag(TopPageActivity.class);
	
	private View loadingView;
	
	private List<Category> categoryList;
	
	private View categoryPanelUpper;
	
	private View categoryPanelLower;
	
	private AdWhirlLayout adWhirlLayout;
	
	private AdView imobileAd;
	
	private AdlantisView adlantisView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.category_list);
    	
    	adWhirlLayout = (AdWhirlLayout)findViewById(R.id.adWhirlLayout);
    	adWhirlLayout.setAdWhirlInterface(this);
    	imobileAd = AdView.createForAdWhirl(this, 19038, 33512);
    	adlantisView = new AdlantisView(this);
    	
    	
    	loadingView = findViewById(R.id.loadingView);
    	loadingView.setVisibility(View.VISIBLE);
    	
    	categoryPanelUpper = findViewById(R.id.categoryPanelUpper);
    	categoryPanelUpper.setVisibility(View.INVISIBLE);
    	

    	categoryPanelLower = findViewById(R.id.categoryPanelLower);
    	categoryPanelLower.setVisibility(View.INVISIBLE);

    	
    	new DataLoadTask().execute();
	}
	
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Category ctg = categoryList.get(position);
		startArticleListActivity(ctg.getCategoryId());
	}
	
	public void categoryButtonClicked(View btn) {
		Long categoryId = Long.valueOf((String)btn.getTag());
		startArticleListActivity(categoryId);
	}
	
	
	public void newArrivalButtonClicked(View v){
		startArticleListActivity(Config.CATEGORY_ID_NEW);
	}
	
	public void favoriteButtonClicked(View v){
		startArticleListActivity(Config.CATEGORY_ID_FAVORITE);
	}

	private void startArticleListActivity(long categoryID){
		Intent nextIntent = new Intent(this, ArticleListActivity.class);
		nextIntent.putExtra("CATEGORY_ID", categoryID);
		startActivity(nextIntent);
	}
	
	
	


	
	
	private class DataLoadTask extends AsyncTask<Void, Void, Void> {
		private boolean failed;
		
		private int nextCheckTimeDelayMinute;
		
		@Override
		protected void onPreExecute() {
			nextCheckTimeDelayMinute = getResources().getInteger(R.integer.initial_data_update_check_time_minute);
			super.onPreExecute();
			
		}

		@Override
		protected Void doInBackground(Void... r) {
			try {
				ormdb = OrmDBAdapter.prepare(getHelper());
				Registry currentVersion = ormdb.getDeviceCurrentDataVersion(); 
				if(currentVersion == null){
					String initialJson = FileReadUtil.readRawTxt(getApplicationContext(), R.raw.texstone);
					Gson gs= new GsonBuilder().create();
					UpdateDataPack dataPack = gs.fromJson(initialJson, UpdateDataPack.class);
					long nextCheckTime = new Date().getTime() + nextCheckTimeDelayMinute*60*1000;
					ormdb.updateData(dataPack, true, nextCheckTime);
					categoryList = ormdb.getCategoryList();
				}
				else {
					categoryList = ormdb.getCategoryList();
					long now = new Date().getTime();
					long scheduledCheckTimeMs = ormdb.getNextServerCheckTime().getValue();
					if(now > scheduledCheckTimeMs && NetworkUtil.isOnline(getApplicationContext())){
						new DataUpdateTask().execute();
					}
				}
				
				
				
			} catch (Exception e) {
				failed = true;
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			loadingView.setVisibility(View.GONE);
			if(!failed){
		    	categoryPanelUpper.setVisibility(View.VISIBLE);
		    	categoryPanelLower.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	
	private class DataUpdateTask extends AsyncTask<Void, Void, Void> {
		private boolean failed;
		
		private int nextCheckTimeDelayMinute;
		
		@Override
		protected void onPreExecute() {
			nextCheckTimeDelayMinute = getResources().getInteger(R.integer.next_data_update_check_time_minute);
			super.onPreExecute();
		}
		

		@Override
		protected Void doInBackground(Void... r) {
			try {
				Registry currentDataVersion = ormdb.getDeviceCurrentDataVersion();
				String dataUpdateUrl = getString(R.string.data_update_URL, currentDataVersion.getValue()+"");
				String updateJson = HttpUtil.executeHttpGet(dataUpdateUrl, this);
				Gson gs= new GsonBuilder().create();
				UpdateDataPack dataPack = gs.fromJson(updateJson, UpdateDataPack.class);
				
				long nextCheckTime = new Date().getTime() + nextCheckTimeDelayMinute*60*1000;
				
				if(dataPack != null){
					ormdb.updateData(dataPack, false, nextCheckTime);
				}
				else {
					ormdb.updateNextServerCheckTime(nextCheckTime);
				}
				
				
				
			} catch (Exception e) {
				failed = true;
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			return null;
		}
		
	}


	@Override
	public void adWhirlGeneric() {
		// TODO Auto-generated method stub
	}
	
	public void handleimobile() {
		//AdViewの取得結果監視イベント
		imobileAd.setOnRequestListener(new AdViewRequestListener() {
			@Override
			public void onCompleted(AdRequestResult result, AdView sender) {
				// AdWhirlに次の広告要求
				adWhirlLayout.adWhirlManager.resetRollover();
				// 取得できたAdViewを枠に適用
				adWhirlLayout.handler.post(new ViewAdRunnable(
						TopPageActivity.this.adWhirlLayout, sender));
				// 必要な時間処理をAdWhirl側で待機
				adWhirlLayout.rotateThreadedDelayed();
			}

			@Override
			public void onFailed(AdRequestResult result, AdView sender) {
				// AdWhirlに次の広告要求
				adWhirlLayout.rollover();
			}
		});
		// 広告取得開始
		imobileAd.start();
	}
	

	public void handleAdlantis() {
		// AdWhirlに次の広告要求
		adWhirlLayout.adWhirlManager.resetRollover();
		// 取得できたAdViewを枠に適用
		adWhirlLayout.handler.post(new ViewAdRunnable(
				TopPageActivity.this.adWhirlLayout, adlantisView));
		// 必要な時間処理をAdWhirl側で待機
		adWhirlLayout.rotateThreadedDelayed();
	}
	
	
	
	
}
