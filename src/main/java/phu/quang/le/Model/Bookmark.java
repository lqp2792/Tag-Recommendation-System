package phu.quang.le.Model;

import java.sql.Date;
import java.util.List;

public class Bookmark {
	private int bookmarkID;
	private String url;
	private String title;
	private List<String> tags;
	private Date date;
	private int viewTimes;
	private double totalRating;
	private double rated;
	private int copyTimes;
	private boolean isCopied;

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

	public int getBookmarkID() {
		return bookmarkID;
	}

	public void setBookmarkID(int bookmarkID) {
		this.bookmarkID = bookmarkID;
	}

	public int getViewTimes() {
		return viewTimes;
	}

	public void setViewTimes(int viewTimes) {
		this.viewTimes = viewTimes;
	}

	public int getCopyTimes() {
		return copyTimes;
	}

	public void setCopyTimes(int copyTimes) {
		this.copyTimes = copyTimes;
	}

	public double getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(double totalRating) {
		this.totalRating = totalRating;
	}

	public double getRated() {
		return rated;
	}

	public void setRated(double rated) {
		this.rated = rated;
	}

	public boolean isCopied() {
		return isCopied;
	}

	public void setCopied(boolean isCopied) {
		this.isCopied = isCopied;
	}

}
