package phu.quang.le.Model;

import java.util.List;

public class TagTopic {
	private String tag;
	private List<Integer> topicIDs;

	public List<Integer> getTopicIDs() {
		return topicIDs;
	}

	public void setTopicIDs(List<Integer> topicIDs) {
		this.topicIDs = topicIDs;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
