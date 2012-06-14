package com.jpn.takatsuka.texstone.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jpn.takatsuka.texstone.Config;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "mainichiKasan.db";
	
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;


	// the DAO object we use to access the Article table
	private Dao<Article, Long> articleDao = null;
	
	// the DAO object we use to access the Article table
	private Dao<ArticleShort, Long> articleShortDao = null;

	// the DAO object we use to access the Category table
	private Dao<Category, Long> categoryDao = null;
	
	// the DAO object we use to access the Registry table
	private Dao<Registry, String> registryDao = null;

	
	static final String TAG = Config.makeLogTag(DatabaseHelper.class);

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Article.class);
			TableUtils.createTable(connectionSource, Category.class);
			TableUtils.createTable(connectionSource, Registry.class);

		} catch (SQLException e) {
			if(Config.DEBUG){
				Log.e(TAG, "Can't create database", e);		
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		Log.i(TAG, "No upgrade needed");
	}



	
	/**
	 * Returns the Database Access Object (DAO) for our Article class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Article, Long> getArticleDao() throws SQLException {
		if (articleDao == null) {
			articleDao = getDao(Article.class);
		}
		return articleDao;
	}
	
	
	/**
	 * Returns the Database Access Object (DAO) for our Article class. It will create it or just give the cached
	 * value.
	 */
	public Dao<ArticleShort, Long> getArticleShortDao() throws SQLException {
		if (articleShortDao == null) {
			articleShortDao = getDao(ArticleShort.class);
		}
		return articleShortDao;
	}
	
	
	/**
	 * Returns the Database Access Object (DAO) for our Category class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Category, Long> getCategoryDao() throws SQLException {
		if (categoryDao == null) {
			categoryDao = getDao(Category.class);
		}
		return categoryDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our Registry class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Registry, String> getRegistryDao() throws SQLException {
		if (registryDao == null) {
			registryDao = getDao(Registry.class);
		}
		return registryDao;
	}
	



	
	

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		articleDao = null;
		categoryDao = null;
		registryDao = null;
		articleShortDao = null;
	}
}