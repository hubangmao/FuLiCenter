package cn.ucai.fulicenter.activity.bean;

import java.io.Serializable;

/**
 * 分类商品的实体类
 * @author yao
 *
 */
public class GoodDetailsBean implements Serializable {

    private int id;
    /** 商品id*/
    private int goodsId;
    /** 所属类别的id*/
    private int catId;
    /** 商品的中文名称*/
    private String goodsName;
    /** 商品的英文名称*/
    private String goodsEnglishName;
    /** 商品简介*/
    private String goodsBrief;
    /** 商品原始价格*/
    private String shopPrice;
    /** 商品的RMB价格 */
    private String currencyPrice;
    /** 商品折扣价格 */
    private String promotePrice;
    /** 人民币折扣价格*/
    private String rankPrice;
    /**是否折扣*/
    private boolean isPromote;
    /** 商品缩略图地址*/
    private String goodsThumb;
    /** 商品图片地址*/
    private String goodsImg;
    /** 商品上架日期，单位：毫秒*/
    private long addTime;
    /** 分享的链接地址*/
    private String shareUrl;
    /** 属性*/
    private PropertyBean[] properties;

    public  PropertyBean[] getProperties() {
        return properties;
    }
    public void setProperties( PropertyBean[] properties) {
        this.properties = properties;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(int goodId) {
        this.goodsId = goodId;
    }
    public int getCatId() {
        return catId;
    }
    public void setCatId(int catId) {
        this.catId = catId;
    }
    public String getGoodsName() {
        return goodsName;
    }
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    public String getGoodsEnglishName() {
        return goodsEnglishName;
    }
    public void setGoodsEnglishName(String goodsEnglishName) {
        this.goodsEnglishName = goodsEnglishName;
    }
    public String getGoodsBrief() {
        return goodsBrief;
    }
    public void setGoodsBrief(String goodsBrief) {
        this.goodsBrief = goodsBrief;
    }
    public String getShopPrice() {
        return shopPrice;
    }
    public void setShopPrice(String shopPrice) {
        this.shopPrice = shopPrice;
    }
    public String getCurrencyPrice() {
        return currencyPrice;
    }
    public void setCurrencyPrice(String currencyPrice) {
        this.currencyPrice = currencyPrice;
    }
    public String getPromotePrice() {
        return promotePrice;
    }
    public void setPromotePrice(String promotePrice) {
        this.promotePrice = promotePrice;
    }
    public String getRankPrice() {
        return rankPrice;
    }
    public void setRankPrice(String rankPrice) {
        this.rankPrice = rankPrice;
    }
    public boolean isPromote() {
        return isPromote;
    }
    public String getGoodsThumb() {
        return goodsThumb;
    }
    public void setGoodsThumb(String goodsThumb) {
        this.goodsThumb = goodsThumb;
    }
    public String getGoodsImg() {
        return goodsImg;
    }
    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }
    
    public long getAddTime() {
        return addTime;
    }
    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
    
    public String getShareUrl() {
        return shareUrl;
    }
    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
    public void setPromote(boolean isPromote) {
        this.isPromote = isPromote;
    }
    @Override
    public String toString() {
        return "CategoryGoodBean [id=" + id + ", goodsId=" + goodsId + ", catId="
                + catId + ", goodsName=" + goodsName + ", goodsEnglishName="
                + goodsEnglishName + ", goodsBrief=" + goodsBrief
                + ", shopPrice=" + shopPrice + ", currencyPrice="
                + currencyPrice + ", promotePrice=" + promotePrice
                + ", rankPrice=" + rankPrice + ", isPromote=" + isPromote
                + ", goodsThumb=" + goodsThumb + ", goodsImg=" + goodsImg
                + ", addTime=" + addTime + ", shareUrl=" + shareUrl + "]";
    }

}
