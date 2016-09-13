package com.zero.waterfalllib.widget.bean;

/**
 * @author linzewu
 * @date 16/9/10
 */
public class WaterFallBean {
    
    private String mUrl;  //图片链接
    private String mTitle;  //标题
    private String mClickLink;  //点击链接

    public WaterFallBean() {
    }
    
    public WaterFallBean(String url, String title, String clickLink) {
        this.mUrl = url;
        this.mTitle = title;
        this.mClickLink = clickLink;
    }
    
    public void setUrl(String url) {
        this.mUrl = url;
    }
    
    public void setTitle(String title) {
        this.mTitle = title;
    }
    
    public void setClickLink(String clickLink) {
        this.mClickLink = clickLink;
    }
    
    public String getUrl() {
        return this.mUrl;
    }
    
    public String getTitle() {
        return this.mTitle;
    }
    
    public String getClickLink() {
        return this.mClickLink;
    }
}
