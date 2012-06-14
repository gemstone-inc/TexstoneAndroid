package com.jpn.takatsuka.texstone.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="ARTICLE")
public class Article extends ArticleShort{

	@DatabaseField(columnName="BODY")
	private String body;
	
	@DatabaseField(columnName="RATING")
	private Integer rating;
	
	
	


	public String getBody() {
		return body;
	}


	public Integer getRating() {
		return rating;
	}








	public void setBody(String body) {
		this.body = body;
	}



	public void setRating(Integer rating) {
		this.rating = rating;
	}


	
}
