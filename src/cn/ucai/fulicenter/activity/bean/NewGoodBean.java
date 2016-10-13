package cn.ucai.fulicenter.activity.bean;

import java.io.Serializable;

/**
 * 新品和精选的实体类
 * @author yao
 *
 */
public class NewGoodBean implements Serializable {

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
	/** 颜色id*/
	private int colorId;
	/** 颜色名*/
	private String colorName;
	/** 颜色代码*/
	private String colorCode;
	/** 导购链接*/
	private String colorUrl;
	/** 商品上架日期，单位：毫秒*/
	private long addTime;
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
	public int getColorId() {
		return colorId;
	}
	public void setColorId(int colorId) {
		this.colorId = colorId;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public String getColorUrl() {
		return colorUrl;
	}
	public void setColorUrl(String colorUrl) {
		this.colorUrl = colorUrl;
	}
	public long getAddTime() {
		return addTime;
	}
	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	
	public boolean isPromote() {
		return isPromote;
	}
	public void setPromote(boolean isPromote) {
		this.isPromote = isPromote;
	}
	@Override
	public String toString() {
		return "NewGoodBean [id=" + id + ", goodsId=" + goodsId + ", catId="
				+ catId + ", goodsName=" + goodsName + ", goodsEnglishName="
				+ goodsEnglishName + ", goodsBrief=" + goodsBrief
				+ ", shopPrice=" + shopPrice + ", currencyPrice="
				+ currencyPrice + ", promotePrice=" + promotePrice
				+ ", rankPrice=" + rankPrice + ", is_promote=" + isPromote
				+ ", goodsThumb=" + goodsThumb + ", goodsImg=" + goodsImg
				+ ", colorId=" + colorId + ", colorName=" + colorName
				+ ", colorCode=" + colorCode + ", colorUrl=" + colorUrl
				+ ", addTime=" + addTime + "]";
	}
	
}
