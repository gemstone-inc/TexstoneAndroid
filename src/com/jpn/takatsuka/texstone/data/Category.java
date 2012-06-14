package com.jpn.takatsuka.texstone.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;





@DatabaseTable(tableName="CATEGORY")
public class Category {

	@DatabaseField(id = true, columnName="CATEGORY_ID")
	private Long categoryId;
	
	@DatabaseField(columnName="CATEGORY_NAME")
	private String categoryName;
	
	@DatabaseField(columnName="IMAGE_HTTP")
	private String imageHttp;
	
	@DatabaseField(columnName="IMAGE_FILE_SIZE")
	private Long imageFileSize;
	
	
	@DatabaseField(columnName="IMAGE_OVER_HTTP")
	private String imageOverHttp;
	
	@DatabaseField(columnName="IMAGE_OVER_FILE_SIZE")
	private Long imageOverFileSize;

	
	@DatabaseField(columnName="VERSION_ID")
	private Long versionId;

	public Long getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getImageHttp() {
		return imageHttp;
	}
	
	public String getDeviceImageFilename(){
		return imageHttp.substring(imageHttp.lastIndexOf("/")+1);
	}
	

	public Long getImageOverFileSize() {
		return imageOverFileSize;
	}
	
	
	public String getImageOverHttp() {
		return imageOverHttp;
	}
	
	public String getDeviceImageOverFilename(){
		return imageOverHttp.substring(imageOverHttp.lastIndexOf("/")+1);
	}
	

	public Long getImageFileSize() {
		return imageFileSize;
	}
	
	

	public Long getVersionId() {
		return versionId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setImageHttp(String imageHttp) {
		this.imageHttp = imageHttp;
	}

	public void setImageFileSize(Long imageFileSize) {
		this.imageFileSize = imageFileSize;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}
	
	public void setImageOverFileSize(Long imageOverFileSize) {
		this.imageOverFileSize = imageOverFileSize;
	}
	
	public void setImageOverHttp(String imageOverHttp) {
		this.imageOverHttp = imageOverHttp;
	}

	
}
