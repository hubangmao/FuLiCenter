package cn.hbm.fulicenter.activity.bean;


public class Avatar extends Message implements java.io.Serializable {
	private static final long serialVersionUID = 2137809455396377048L;

	// Fields

	/**
	 * 
	 */
	private Integer mavatarId;
	private Integer mavatarUserId;
	private String mavatarUserName;
	private String mavatarPath;
	private Integer mavatarType;

	// Constructors

	/** default constructor */
	public Avatar() {
	}

	/** full constructor */
	public Avatar(Integer MAvatarUserId, String MAvatarUserName,
			String MAvatarPath, Integer MAvatarType) {
		this.mavatarUserId = MAvatarUserId;
		this.mavatarUserName = MAvatarUserName;
		this.mavatarPath = MAvatarPath;
		this.mavatarType = MAvatarType;
	}

	// Property accessors
	public Integer getMAvatarId() {
		return this.mavatarId;
	}

	public void setMAvatarId(Integer MAvatarId) {
		this.mavatarId = MAvatarId;
	}

	public Integer getMAvatarUserId() {
		return this.mavatarUserId;
	}

	public void setMAvatarUserId(Integer MAvatarUserId) {
		this.mavatarUserId = MAvatarUserId;
	}

	public String getMAvatarUserName() {
		return this.mavatarUserName;
	}

	public void setMAvatarUserName(String MAvatarUserName) {
		this.mavatarUserName = MAvatarUserName;
	}

	public String getMAvatarPath() {
		return this.mavatarPath;
	}

	public void setMAvatarPath(String MAvatarPath) {
		this.mavatarPath = MAvatarPath;
	}

	public Integer getMAvatarType() {
		return this.mavatarType;
	}

	public void setMAvatarType(Integer MAvatarType) {
		this.mavatarType = MAvatarType;
	}

	@Override
	public String toString() {
		return "Avatar{" +
				"mavatarId=" + mavatarId +
				", mavatarUserId=" + mavatarUserId +
				", mavatarUserName='" + mavatarUserName + '\'' +
				", mavatarPath='" + mavatarPath + '\'' +
				", mavatarType=" + mavatarType +
				'}';
	}
}