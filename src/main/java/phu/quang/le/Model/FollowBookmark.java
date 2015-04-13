package phu.quang.le.Model;

import java.sql.Date;
import java.util.List;

public class FollowBookmark {
	private int bookmarkID;
	private String firstName;
	private String lastName;
	private String url;
	private String title;
	private List<String> tags;
	private Date date;
	private int copyTimes;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getCopyTimes() {
		return copyTimes;
	}

	public void setCopyTimes(int copyTimes) {
		this.copyTimes = copyTimes;
	}

	public int getBookmarkID() {
		return bookmarkID;
	}

	public void setBookmarkID(int bookmarkID) {
		this.bookmarkID = bookmarkID;
	}

}
