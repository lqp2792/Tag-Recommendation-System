package phu.quang.le.Model;

import java.util.List;

public class RecommendUser {
	private int userID;
	private String firstName;
	private String lastName;
	private List<TagWeight> mostUsedTags;
	private List<Integer> mostUsedTopics;
	private int sameTagCount;
	private int sameTopicCount;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<TagWeight> getMostUsedTags() {
		return mostUsedTags;
	}

	public void setMostUsedTags(List<TagWeight> list) {
		this.mostUsedTags = list;
	}

	public List<Integer> getMostUsedTopics() {
		return mostUsedTopics;
	}

	public void setMostUsedTopics(List<Integer> mostUsedTopics) {
		this.mostUsedTopics = mostUsedTopics;
	}

	public int getSameTagCount() {
		return sameTagCount;
	}

	public void setSameTagCount(int sameTagCount) {
		this.sameTagCount = sameTagCount;
	}

	public int getSameTopicCount() {
		return sameTopicCount;
	}

	public void setSameTopicCount(int sameTopicCount) {
		this.sameTopicCount = sameTopicCount;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
}
