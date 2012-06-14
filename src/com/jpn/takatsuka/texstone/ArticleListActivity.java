package com.jpn.takatsuka.texstone;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import jp.Adlantis.Android.AdlantisView;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jpn.takatsuka.texstone.R;
import com.jpn.takatsuka.texstone.data.ArticleShort;
import com.jpn.takatsuka.texstone.data.HttpUtil;
import com.jpn.takatsuka.texstone.data.OrmDBAdapter;
import com.jpn.takatsuka.texstone.data.Registry;
import com.jpn.takatsuka.texstone.data.UpdateDataPack;


public class ArticleListActivity extends ListActivity implements AdWhirlInterface{

	
	private OrmDBAdapter ormdb;
	
	private ArticleAdapter articleAdapter;
	
	private final String TAG = Config.makeLogTag(ArticleListActivity.class);
	
	private TextView emptyListView;
	
	private View loadingView;
	
	private View updateButton;
	
	private View updateProgressBar;
	
	private long categoryId;
	
	private List<ArticleShort> articleList;
	
	private ListView mListView;
	
	private AdWhirlLayout adWhirlLayout;
	
	private AdView imobileAd;
	
	private AdlantisView adlantisView;
	
	int prevIndex;
	int prevTop;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.article_list);
    	
    	adWhirlLayout = (AdWhirlLayout)findViewById(R.id.adWhirlLayout);
    	adWhirlLayout.setAdWhirlInterface(this);
    	imobileAd = jp.co.imobile.android.AdView.createForAdWhirl(this, 19038, 33512);
    	adlantisView = new AdlantisView(this);
    	
		updateProgressBar = findViewById(R.id.updateProgressBar);
		updateButton = findViewById(R.id.updateButton);
		
		updateProgressBar.setVisibility(View.INVISIBLE);
		updateButton.setVisibility(View.INVISIBLE);

    	ormdb = OrmDBAdapter.getSharedInstance();
    	categoryId = getIntent().getLongExtra("CATEGORY_ID", 1);
    	
    	loadingView = findViewById(R.id.loadingView);
    	loadingView.setVisibility(View.VISIBLE);
    	
    	mListView = getListView();
    	mListView.setVisibility(View.INVISIBLE);
    	
    	emptyListView = (TextView)findViewById(R.id.emptyListView);

		if(categoryId == Config.CATEGORY_ID_FAVORITE){
			emptyListView.setText(R.string.favorite_empty);
		}
		else if(categoryId == Config.CATEGORY_ID_NEW){
			emptyListView.setText(R.string.new_arrival_empty);
		}

    	new DataLoadTask().execute();
	}
	
	
	public void backButtonClicked(View v){
    	finish();
	}
	
	
    public void onListItemClick(ListView parent, View v,
        	int position, long id) {
    	
    		Intent nextIntent = new Intent(this, ArticleActivity.class);
    		
    		long[] articleIdArray = new long[articleList.size()];
    		int count = 0;
    		for (ArticleShort article : articleList) {
				articleIdArray[count++] = article.getArticleId();
			}
    		
    		nextIntent.putExtra("ARTICLE_ID_ARRAY", articleIdArray);
    		nextIntent.putExtra("ARTICLE_ID_POSITION", position);
    		


    		prevIndex = mListView.getFirstVisiblePosition();
    		View v1 = mListView.getChildAt(0);
    		prevTop = (v1 == null) ? 0 : v1.getTop();
    		
    		
    		startActivityForResult(nextIntent, 1);
    }
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(data != null){
        	loadingView.setVisibility(View.VISIBLE);
        	mListView.setVisibility(View.INVISIBLE);
    		new DataLoadTask().execute();
    	}
    }
    
    
	private class FavoriteButtonCheckedChangeListener implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			ArticleShort article = (ArticleShort)buttonView.getTag();
			article.setFavorite(isChecked);
			try {
				ormdb.updateArticle(article);
				Toast.makeText(ArticleListActivity.this, isChecked?R.string.favorite_added:R.string.favorite_removed, Toast.LENGTH_SHORT).show();
			} catch (SQLException e) {
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
		
	}
    
    
	
	private static class ArticleAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		
		private List<ArticleShort> articleList;
		
		private FavoriteButtonCheckedChangeListener mFavoriteButtonCheckedChangeListener;
		
		public ArticleAdapter(Context context, List<ArticleShort> articleList, FavoriteButtonCheckedChangeListener favoriteButtonCheckedChangeListener) {
			mInflater = LayoutInflater.from(context);
			this.articleList = articleList;
			this.mFavoriteButtonCheckedChangeListener = favoriteButtonCheckedChangeListener;
        }
		
		public void setArticleList(List<ArticleShort> newArticleList){
			this.articleList = newArticleList;
		}
		

		@Override
		public int getCount() {
			return articleList.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.article_list_item, null);
				holder = new ViewHolder();
				holder.text = (TextView)convertView.findViewById(R.id.articleTitle);
				holder.favoriteButton = (ToggleButton)convertView.findViewById(R.id.favoriteButton);
				holder.newArrivalIcon = (ImageView)convertView.findViewById(R.id.newArrivalIcon);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			ArticleShort article = articleList.get(position);
			holder.text.setText(article.getTitle());
			
			holder.favoriteButton.setOnCheckedChangeListener(null);
			holder.favoriteButton.setChecked(article.isFavorite());
			holder.favoriteButton.setOnCheckedChangeListener(mFavoriteButtonCheckedChangeListener);
			holder.favoriteButton.setTag(article);
			
			holder.newArrivalIcon.setVisibility(article.isNewArrival()?View.VISIBLE:View.INVISIBLE);
			
			
			return convertView;
		}
		
		class ViewHolder {
			TextView text;
			ToggleButton favoriteButton;
			ImageView newArrivalIcon;
        }

		@Override
		public Object getItem(int position) {
			return articleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
	}
	
	
	private class DataLoadTask extends AsyncTask<Void, Void, List<ArticleShort>> {
		private boolean failed;
		

		@Override
		protected List<ArticleShort> doInBackground(Void... r) {
			List<ArticleShort> resultList = null;
			try {
				if(categoryId == Config.CATEGORY_ID_FAVORITE){
					resultList = ormdb.getFavoriteList();
				}
				else if(categoryId == Config.CATEGORY_ID_NEW){
					resultList = ormdb.getNewArrivalList();
				}
				else {
					resultList = ormdb.getArticleShortList(categoryId);					
				}
			} catch (SQLException e) {
				failed = true;
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			return resultList;
		}
		
		@Override
		protected void onPostExecute(final List<ArticleShort> resultList) {
			loadingView.setVisibility(View.GONE);
			if(!failed){
				ArticleListActivity.this.articleList = resultList;
				
				if(articleAdapter == null){
					articleAdapter = new ArticleAdapter(ArticleListActivity.this, resultList, new FavoriteButtonCheckedChangeListener());
					mListView.setVisibility(View.VISIBLE);
					mListView.setAdapter(articleAdapter);
					if(resultList.isEmpty()){
						emptyListView.setVisibility(View.VISIBLE);
					}
					if(categoryId == Config.CATEGORY_ID_NEW){
						updateButton.setVisibility(View.VISIBLE);
					}
				}
				else {
					articleAdapter.setArticleList(resultList);
		    		articleAdapter.notifyDataSetChanged();
		    		mListView.post(new Runnable() {
						public void run() {
							mListView.setSelectionFromTop(prevIndex, prevTop);
				        	loadingView.setVisibility(View.INVISIBLE);
				        	mListView.setVisibility(View.VISIBLE);
							updateButton.setVisibility(View.VISIBLE);
							
							if(resultList.isEmpty()){
								emptyListView.setVisibility(View.VISIBLE);
							}
							
							if(categoryId == Config.CATEGORY_ID_NEW){
								updateButton.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			}
		}
		
	}
	
	public void updateButtonClicked(View v){
		updateProgressBar.setVisibility(View.VISIBLE);
		updateButton.setVisibility(View.INVISIBLE);
		new DataUpdateTask().execute();
	}
	
	
	
	private class DataUpdateTask extends AsyncTask<Void, Void, List<ArticleShort>> {
		private boolean failed;
		
		private int nextCheckTimeDelayMinute;
		
		private boolean alreadyLatestVersion;
		
		@Override
		protected void onPreExecute() {
			nextCheckTimeDelayMinute = getResources().getInteger(R.integer.next_data_update_check_time_minute);
		}
		

		@Override
		protected List<ArticleShort> doInBackground(Void... r) {
			List<ArticleShort> resultList = null;
			
			try {
				Registry currentDataVersion = ormdb.getDeviceCurrentDataVersion();
				String dataUpdateUrl = getString(R.string.data_update_URL, currentDataVersion.getValue()+"");
				String updateJson = HttpUtil.executeHttpGet(dataUpdateUrl, this);
				if(isCancelled()){
					failed = true;
					return null;
				}
				Gson gs= new GsonBuilder().create();
				UpdateDataPack dataPack = gs.fromJson(updateJson, UpdateDataPack.class);
				
				long nextCheckTime = new Date().getTime() + nextCheckTimeDelayMinute*60*1000;
				
				if(dataPack != null){
					ormdb.updateData(dataPack, false, nextCheckTime);
					if(categoryId == Config.CATEGORY_ID_FAVORITE){
						resultList = ormdb.getFavoriteList();
					}
					else if(categoryId == Config.CATEGORY_ID_NEW){
						resultList = ormdb.getNewArrivalList();
					}
					else {
						resultList = ormdb.getArticleShortList(categoryId);					
					}
					return resultList;
				}
				else {
					alreadyLatestVersion = true;
					ormdb.updateNextServerCheckTime(nextCheckTime);
					return null;
				}
				
			} catch (Exception e) {
				failed = true;
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
				return null;
			}
		}
		
		
		@Override
		protected void onPostExecute(final List<ArticleShort> resultList) {
			if(failed){
				updateProgressBar.setVisibility(View.INVISIBLE);
				updateButton.setVisibility(View.VISIBLE);
				return;
			}
			
			if(alreadyLatestVersion){
				updateProgressBar.setVisibility(View.INVISIBLE);
				updateButton.setVisibility(View.VISIBLE);
				Toast.makeText(ArticleListActivity.this, "新着情報はありません。", Toast.LENGTH_SHORT).show();
			}
			else {
				if(resultList != null){
					if(!resultList.isEmpty()){
						articleAdapter.setArticleList(resultList);
			    		articleAdapter.notifyDataSetChanged();
			    		mListView.post(new Runnable() {
							@Override
							public void run() {
								emptyListView.setVisibility(View.INVISIBLE);
								updateProgressBar.setVisibility(View.INVISIBLE);
								updateButton.setVisibility(View.VISIBLE);
							}
						});
					}
					else {
						emptyListView.setVisibility(View.VISIBLE);
						updateProgressBar.setVisibility(View.INVISIBLE);
						updateButton.setVisibility(View.VISIBLE);
					}
				}
			}
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
						ArticleListActivity.this.adWhirlLayout, sender));
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
				ArticleListActivity.this.adWhirlLayout, adlantisView));
		// 必要な時間処理をAdWhirl側で待機
		adWhirlLayout.rotateThreadedDelayed();
	}
	
	
	
}
