package com.jpn.takatsuka.texstone.data;

import java.util.List;

public class UpdateDataPack {
	
	private Long serverDataVersionId;
	
	private List<Category> categoryList;
	
	private List<Article> articleList;


	public Long getServerDataVersionId() {
		return serverDataVersionId;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public List<Article> getArticleList() {
		return articleList;
	}

	
	public void setServerDataVersionId(Long serverDataVersionId) {
		this.serverDataVersionId = serverDataVersionId;
	}


	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}
	
	
}
