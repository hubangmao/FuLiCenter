package cn.hbm.fulicenter.activity.bean;

public class CollectBean {
	int id;
	private String userName;// 收藏的id
	private int goodsId;// 商品id
	private String goodsName;// 商品的中文名称
	private String goodsEnglishName;// 商品的英文名称
	private String goodsThumb;// 商品缩略图
	private String goodsImg;// 商品大图
	private long addTime;// 商品上架时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
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
	
	public CollectBean() {
		// TODO Auto-generated constructor stub
	}
	
	public CollectBean(String userName, int goodsId, String goodsName,
			String goodsEnglishName, String goodsThumb, String goodsImg,
			long addTime) {
		super();
		this.userName = userName;
		this.goodsId = goodsId;
		this.goodsName = goodsName;
		this.goodsEnglishName = goodsEnglishName;
		this.goodsThumb = goodsThumb;
		this.goodsImg = goodsImg;
		this.addTime = addTime;
	}

	@Override
	public String toString() {
		return "WardrobeBean [id=" + id + ", userName=" + userName
				+ ", goodsId=" + goodsId + ", goodsName=" + goodsName
				+ ", goodsEnglishName=" + goodsEnglishName + ", goodsThumb="
				+ goodsThumb + ", goodsImg=" + goodsImg + ", addTime="
				+ addTime + "]";
	}

}
