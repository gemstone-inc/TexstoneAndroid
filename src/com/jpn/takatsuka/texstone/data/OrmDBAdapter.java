package com.jpn.takatsuka.texstone.data;


import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

public class OrmDBAdapter {
	
	private static OrmDBAdapter dbInstance;
	
	public static OrmDBAdapter getSharedInstance(){
		return dbInstance;
	}
	
	private DatabaseHelper mHelper;
	public void setHelper(DatabaseHelper helper) {
		this.mHelper = helper;
	}
	
	public static OrmDBAdapter prepare(DatabaseHelper helper) throws SQLException{
		dbInstance = new OrmDBAdapter();
		dbInstance.setHelper(helper);
		dbInstance.setCategoryDao(helper.getCategoryDao());
		dbInstance.setArticleDao(helper.getArticleDao());
		dbInstance.setArticleShortDao(helper.getArticleShortDao());
		dbInstance.setRegistryDao(helper.getRegistryDao());
		return dbInstance;
	}
	

	private Dao<Category, Long> categoryDao;
	private Dao<Article, Long> articleDao;
	private Dao<ArticleShort, Long> articleShortDao;
	private Dao<Registry, String> registryDao;
	
	private OrmDBAdapter(){
	}
	
	private void setCategoryDao(Dao<Category, Long> categoryDao) {
		this.categoryDao = categoryDao;
	}

	private void setArticleDao(Dao<Article, Long> articleDao) {
		this.articleDao = articleDao;
	}
	private void setRegistryDao(Dao<Registry, String> registryDao) {
		this.registryDao = registryDao;
	}
	
	public void setArticleShortDao(Dao<ArticleShort, Long> articleShortDao) {
		this.articleShortDao = articleShortDao;
	}


	
	private synchronized void detectNewArrivalList(List<ArticleShort> articleList) throws SQLException {
    	getDeviceCurrentDataVersion();
    	getDeviceInstallDataVersion();
    	
    	for (ArticleShort article : articleList) {
			article.setNewArrival(article.getVersionId().longValue() > deviceInstallDataVersion.value && 
									article.getVersionId().longValue() == deviceCurrentDataVersion.value);
		}
	}

	
	
    public synchronized List<ArticleShort> getArticleShortList(long categoryId) throws SQLException {
    	List<ArticleShort> articleList = 
	    	 articleShortDao.queryBuilder().orderBy("TITLE_FURIGANA", true)
			.where()
			.eq("REF_CATEGORY_ID", categoryId)
			.query();
    	
    	
    	detectNewArrivalList(articleList);
    	return articleList;
    }
    
    public synchronized List<ArticleShort> getFavoriteList() throws SQLException {
    	List<ArticleShort> articleList = 
	    	 articleShortDao.queryBuilder().orderBy("TITLE_FURIGANA", true)
			.where()
			.eq("FAVORITE", true)
			.query();
    	detectNewArrivalList(articleList);
    	return articleList;
    }
    
    
    public synchronized List<ArticleShort> getNewArrivalList() throws SQLException {
    	getDeviceCurrentDataVersion();
    	getDeviceInstallDataVersion();
    	
    	List<ArticleShort> articleList =  articleShortDao.queryBuilder().orderBy("TITLE_FURIGANA", true)
		.where()
		.eq("VERSION_ID", deviceCurrentDataVersion.value)
		.and()
		.gt("VERSION_ID", deviceInstallDataVersion.value)
		.query();
    	
    	for (ArticleShort article : articleList) {
			article.setNewArrival(true);
		}
    	
    	return articleList;
    }


    public synchronized List<Category> getCategoryList() throws SQLException {
    	return categoryDao.queryForAll();
    }

    public synchronized void createArticle(Article article) throws SQLException {
    	articleDao.create(article);
    }
    
    public synchronized void updateArticle(Article article) throws SQLException {
    	articleDao.update(article);
    }
    
    public synchronized void updateArticle(ArticleShort article) throws SQLException {
    	articleShortDao.update(article);
    }
    
    public synchronized Article getArticle(long articleId) throws SQLException {
    	Article article = articleDao.queryForId(articleId);
    	article.setNewArrival(article.getVersionId().longValue() > deviceInstallDataVersion.value &&
    			article.getVersionId().longValue() == deviceCurrentDataVersion.value);
    	return article;
    }
    
    
    public synchronized void updateData(UpdateDataPack dataPack, boolean initial, Long nextServerCheckTime) throws SQLException {
		if(initial){
			deviceInstallDataVersion = new Registry(Registry.INSTALL_VERSION, dataPack.getServerDataVersionId());
    		registryDao.create(deviceInstallDataVersion);
    		
    		deviceCurrentDataVersion = new Registry(Registry.CURRENT_VERSION, dataPack.getServerDataVersionId());
    		registryDao.create(deviceCurrentDataVersion);
    		
    		nextServerCheck = new Registry(Registry.NEXT_SERVER_CHECK, nextServerCheckTime);
    		registryDao.create(nextServerCheck);
		}
		else {
			deviceCurrentDataVersion.setValue(dataPack.getServerDataVersionId());
			registryDao.update(deviceCurrentDataVersion);
			
			nextServerCheck.setValue(nextServerCheckTime);
			registryDao.update(nextServerCheck);
		}
		
		
    	
    	for (Category ctry : dataPack.getCategoryList()) {
			categoryDao.create(ctry);
		}
    	
    	for (Article article : dataPack.getArticleList()) {
    		article.setVersionId(dataPack.getServerDataVersionId());
			articleDao.create(article);
		}
    	
    }
    
    private Registry deviceCurrentDataVersion;
    
    private Registry deviceInstallDataVersion;
    
    private Registry nextServerCheck;
    
    public synchronized Registry getDeviceCurrentDataVersion() throws SQLException {
    	if(deviceCurrentDataVersion == null){
    		deviceCurrentDataVersion = registryDao.queryForId(Registry.CURRENT_VERSION);
    	}
    	return deviceCurrentDataVersion;
    }
    
    public synchronized Registry getDeviceInstallDataVersion() throws SQLException {
    	if(deviceInstallDataVersion == null){
    		deviceInstallDataVersion = registryDao.queryForId(Registry.INSTALL_VERSION);
    	}
    	return deviceInstallDataVersion;
    }
    
    public synchronized Registry getNextServerCheckTime() throws SQLException {
    	if(nextServerCheck == null){
    		nextServerCheck = registryDao.queryForId(Registry.NEXT_SERVER_CHECK);
    	}
    	return nextServerCheck;
    }
    
    public synchronized void updateNextServerCheckTime(Long nextServerCheckTime) throws SQLException {
		nextServerCheck.setValue(nextServerCheckTime);
		registryDao.update(nextServerCheck);
    }
    

    
    

}