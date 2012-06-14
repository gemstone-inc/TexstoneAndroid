package com.jpn.takatsuka.texstone;

import java.util.TimeZone;



/**
 * Configuration settings for the Android client.
 */
public class Config {

    @SuppressWarnings("rawtypes")
    public static String makeLogTag(Class cls) {
        String tag = "TS_" + cls.getSimpleName();
        return (tag.length() > 30) ? tag.substring(0, 30) : tag;
    }

    public static final boolean DEBUG = true;
    public static final TimeZone TIME_ZONE =  TimeZone.getTimeZone("Asia/Tokyo");

    public static final String DATE_FORMAT_STR = "yyyy-MM-dd";


    public static final long CATEGORY_ID_NEW = 0;
    public static final long CATEGORY_ID_FAVORITE = -1;
    
    
}
