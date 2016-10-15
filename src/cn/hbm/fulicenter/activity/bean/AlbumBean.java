package cn.hbm.fulicenter.activity.bean;

/**
 * 相册
 * 
 * @author yw
 */
public class AlbumBean {
    private int pid;
	private int imgId;// 图片id
	private String imgUrl;// 图片地址
	private String thumbUrl;// 缩略图地址

	public int getPid() {
		return pid;
	}



	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getImgId() {
		return imgId;
	}



	public void setImgId(int imgId) {
		this.imgId = imgId;
	}



	public String getImgUrl() {
		return imgUrl;
	}



	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}



	public String getThumbUrl() {
		return thumbUrl;
	}



	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}



	@Override
	public String toString() {
		return "AlbumBean [pid=" + pid + ", imgId=" + imgId + ", imgUrl="
				+ imgUrl + ", thumbUrl=" + thumbUrl + "]";
	}

}
