package cn.ucai.fulicenter.activity.bean;

import java.io.Serializable;

public class ColorBean implements Serializable {

	private int catId;
	private int colorId;
	private String colorName;
	private String colorCode;
	private String colorImg;

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
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

	public String getColorImg() {
		return colorImg;
	}

	public void setColorImg(String colorImg) {
		this.colorImg = colorImg;
	}

	@Override
	public String toString() {
		return "ColorBean [catId=" + catId + ", colorId=" + colorId
				+ ", colorName=" + colorName + ", colorCode=" + colorCode
				+ ", colorImg=" + colorImg + "]";
	}

}