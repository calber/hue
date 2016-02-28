package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Swupdate {

@SerializedName("updatestate")
@Expose
private Integer updatestate;
@SerializedName("url")
@Expose
private String url;
@SerializedName("text")
@Expose
private String text;
@SerializedName("notify")
@Expose
private Boolean notify;

/**
*
* @return
* The updatestate
*/
public Integer getUpdatestate() {
return updatestate;
}

/**
*
* @param updatestate
* The updatestate
*/
public void setUpdatestate(Integer updatestate) {
this.updatestate = updatestate;
}

/**
*
* @return
* The url
*/
public String getUrl() {
return url;
}

/**
*
* @param url
* The url
*/
public void setUrl(String url) {
this.url = url;
}

/**
*
* @return
* The text
*/
public String getText() {
return text;
}

/**
*
* @param text
* The text
*/
public void setText(String text) {
this.text = text;
}

/**
*
* @return
* The notify
*/
public Boolean getNotify() {
return notify;
}

/**
*
* @param notify
* The notify
*/
public void setNotify(Boolean notify) {
this.notify = notify;
}

}
