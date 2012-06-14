package com.jpn.takatsuka.texstone.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="REGISTRY")
public class Registry {
	
	public static final String CURRENT_VERSION = "CURRENT_VERSION";
	public static final String INSTALL_VERSION = "INSTALL_VERSION";
	public static final String NEXT_SERVER_CHECK = "NEXT_SERVER_CHECK";

    
    private static final String STR_REGISTRY_KEY = "KEY";
    private static final String STR_REGISTRY_VALUE = "VALUE";
    
    
	@DatabaseField(id = true, columnName=STR_REGISTRY_KEY)
	String key;
	
	@DatabaseField(columnName=STR_REGISTRY_VALUE)
	long value;

	
	public String getKey() {
		return key;
	}
	
	public long getValue() {
		return value;
	}

	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setValue(long value) {
		this.value = value;
	}
	
	public Registry() {
	}
	
	public Registry(String key, long value) {
		this.key = key;
		this.value = value;
	}

	






 
    

}
 
