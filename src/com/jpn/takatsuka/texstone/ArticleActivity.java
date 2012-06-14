package com.jpn.takatsuka.texstone;

import java.sql.SQLException;

import jp.Adlantis.Android.AdlantisView;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlLayout.ViewAdRunnable;
import com.jpn.takatsuka.texstone.R;
import com.jpn.takatsuka.texstone.data.Article;
import com.jpn.takatsuka.texstone.data.OrmDBAdapter;


public class ArticleActivity extends Activity implements AdWhirlInterface{

	
	private OrmDBAdapter ormdb;

	private final String TAG = Config.makeLogTag(ArticleActivity.class);
	
	private AdWhirlLayout adWhirlLayout;
	
	private AdView imobileAd;
	
	private AdlantisView adlantisView;
	
	private View loadingView;
	
	private Article mCurrentArticle;
	
	private TextView articleTitleView;
	
	private TextView articleBodyView;
	
	private ToggleButton favoriteButton;
	
	private View previousButton;
	
	private View nextButton;
	
	private View newArrivalIcon;
	
	private View articleTitleLayout;
	
	private FavoriteButtonCheckedChangeListener mFavoriteButtonCheckedChangeListener;
	
	private boolean favoriteStateChanged;
	
	
	private long[] mArticleIdArray;
	
	private int mCurrentArticleIdPosition = -1;
	
	public void backButtonClicked(View v){
    	if(favoriteStateChanged){
    		Intent returnIntent = new Intent();
    		setResult(1, returnIntent);
    	}
    	finish();
	}
	
	public void nextButtonClicked(View v){
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);
		new DataLoadTask().execute(mCurrentArticleIdPosition + 1);
	}
	
	public void previousButtonClicked(View v){
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);
		new DataLoadTask().execute(mCurrentArticleIdPosition - 1);
	}
	
	
	private void adjustNavigateButtons(){
		previousButton.setEnabled(mCurrentArticleIdPosition != 0);
		nextButton.setEnabled(mCurrentArticleIdPosition != (mArticleIdArray.length - 1));
		previousButton.setVisibility(previousButton.isEnabled()?View.VISIBLE:View.INVISIBLE);
		nextButton.setVisibility(nextButton.isEnabled()?View.VISIBLE:View.INVISIBLE);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.article);
    	
    	adWhirlLayout = (AdWhirlLayout)findViewById(R.id.adWhirlLayout);
    	adWhirlLayout.setAdWhirlInterface(this);
    	imobileAd = jp.co.imobile.android.AdView.createForAdWhirl(this, 19038, 33512);
    	adlantisView = new AdlantisView(this);
    	
    	
    	newArrivalIcon = findViewById(R.id.newArrivalIcon);
    	
    	ormdb = OrmDBAdapter.getSharedInstance();
    	
    	loadingView = findViewById(R.id.loadingView);
    	loadingView.setVisibility(View.VISIBLE);

    	articleTitleView = (TextView)findViewById(R.id.articleTitle);
    	articleTitleView.setText("");

    	
    	articleBodyView = (TextView)findViewById(R.id.articleBody);
    	articleBodyView.setMovementMethod(new ScrollingMovementMethod());
    	articleBodyView.setText("");
    	
    	articleTitleLayout = findViewById(R.id.articleTitleLayout);
    	articleTitleLayout.setVisibility(View.INVISIBLE);
    	
    	favoriteButton = (ToggleButton)findViewById(R.id.favoriteButton);
    	mFavoriteButtonCheckedChangeListener = new FavoriteButtonCheckedChangeListener();
    	
    	nextButton = findViewById(R.id.nextButton);
    	previousButton = findViewById(R.id.previousButton);
    	
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);

    	mArticleIdArray = getIntent().getLongArrayExtra("ARTICLE_ID_ARRAY");
    	new DataLoadTask().execute(getIntent().getIntExtra("ARTICLE_ID_POSITION", 0));
	}
	
	
	private class FavoriteButtonCheckedChangeListener implements ToggleButton.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			favoriteStateChanged = true;
			mCurrentArticle.setFavorite(isChecked);
			try {
				ormdb.updateArticle(mCurrentArticle);
				Toast.makeText(ArticleActivity.this, isChecked?R.string.favorite_added:R.string.favorite_removed, Toast.LENGTH_SHORT).show();
			} catch (SQLException e) {
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
		
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	if(!favoriteStateChanged){
		    	return super.onKeyDown(keyCode, event);	    		
	    	}
	    	else {
	    		Intent returnIntent = new Intent();
	    		setResult(1, returnIntent);
	            finish();
	    		return true;
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}

	
	
	private class DataLoadTask extends AsyncTask<Integer, Void, Article> {
		private boolean failed;
		private int articleIdPosition;

		@Override
		protected Article doInBackground(Integer... arg) {
			try {
				articleIdPosition = arg[0];
				long articleId = mArticleIdArray[articleIdPosition];
				Article result = ormdb.getArticle(articleId);
				return result;
			} catch (SQLException e) {
				failed = true;
				if(Config.DEBUG){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Article result) {
			loadingView.setVisibility(View.GONE);
			if(!failed){
				mCurrentArticleIdPosition = articleIdPosition;
				mCurrentArticle = result;
				adjustNavigateButtons();
				
				articleTitleLayout.setVisibility(View.VISIBLE);
		    	articleTitleView.setText(mCurrentArticle.getTitle());
		    	articleBodyView.scrollTo(0, 0);
		    	articleBodyView.setText(mCurrentArticle.getBody().replaceAll("\r", ""));
		    	
				favoriteButton.setOnCheckedChangeListener(null);
				favoriteButton.setChecked(mCurrentArticle.isFavorite());
				favoriteButton.setOnCheckedChangeListener(mFavoriteButtonCheckedChangeListener);

				newArrivalIcon.setVisibility(mCurrentArticle.isNewArrival() ? View.VISIBLE : View.INVISIBLE);
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
						ArticleActivity.this.adWhirlLayout, sender));
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
				ArticleActivity.this.adWhirlLayout, adlantisView));
		// 必要な時間処理をAdWhirl側で待機
		adWhirlLayout.rotateThreadedDelayed();


	}

	
	
	
}
