package cn.hbm.fulicenter.activity.bean;

import java.io.Serializable;

//分类大类资源
public class CategoryGroupBean implements Serializable {

    private int id;
    /**
     * 大类名称
     */
    private String name;
    /**
     * 图片地址
     */
    private String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "CategoryParent [id=" + id + ", name=" + name + ", imageUrl="
                + imageUrl + "]";
    }

}
