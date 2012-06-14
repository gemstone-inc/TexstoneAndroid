package com.jpn.takatsuka.texstone.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ARTICLE")
public class ArticleShort {

	@DatabaseField(id = true, columnName="ARTICLE_ID")	
	private Long articleId; 
	
	@DatabaseField(columnName="REF_CATEGORY_ID")
	private Long categoryId;
	
	@DatabaseField(columnName="TITLE")
	private String title;

	
	@DatabaseField(columnName="TITLE_FURIGANA")
	private String titleFurigana;

	
	@DatabaseField(columnName="FAVORITE")
	private boolean favorite;
	
	
	@DatabaseField(columnName="VERSION_ID")
	private Long versionId;

	
	private boolean newArrival;
	

	public Long getArticleId() {
		return articleId;
	}

	public Long getCategoryId() {
		return categoryId;
	}


	public String getTitle() {
		return title;
	}
	
	public String getTitleFurigana() {
		return titleFurigana;
	}
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public Long getVersionId() {
		return versionId;
	}
	
	public boolean isNewArrival() {
		return newArrival;
	}


	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleFurigana(String titleFurigana) {
		this.titleFurigana = titleFurigana;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	
	public void setNewArrival(boolean newArrival) {
		this.newArrival = newArrival;
	}
}
