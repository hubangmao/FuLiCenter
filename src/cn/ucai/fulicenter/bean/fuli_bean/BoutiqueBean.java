package cn.ucai.fulicenter.bean.fuli_bean;

import java.io.Serializable;

public class BoutiqueBean implements Serializable {
    /** 分类id*/
    private int id;
    /** 标题*/
    private String title;
    
    /** 简介*/
    private String description;
    
    /** 名称*/
    private String name;

    /** 图片地址*/
    private String imageurl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public String toString() {
        return "BoutiqueBean [id=" + id + ", title=" + title + ", description="
                + description + ", name=" + name + ", imageurl=" + imageurl
                + "]";
    }

}
