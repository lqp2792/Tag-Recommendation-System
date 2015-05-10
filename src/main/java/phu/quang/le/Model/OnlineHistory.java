package phu.quang.le.Model;

public class OnlineHistory {
	private int userID;
	private int onlineCount;
	private String lastedOnline;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getLastedOnline() {
		return lastedOnline;
	}

	public void setLastedOnline(String lastedOnline) {
		this.lastedOnline = lastedOnline;
	}

	public int getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

}
