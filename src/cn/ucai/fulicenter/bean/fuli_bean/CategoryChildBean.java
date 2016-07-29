package cn.ucai.fulicenter.bean.fuli_bean;

import java.io.Serializable;

public class CategoryChildBean implements Serializable {

	private int id;
	private int parentId;
	/** 大类名称 */
	private String name;
	/** 图片地址 */
	private String imageUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
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
		return "CategoryChildBean [id=" + id + ", parentId=" + parentId
				+ ", name=" + name + ", imageUrl=" + imageUrl + "]";
	}

}
