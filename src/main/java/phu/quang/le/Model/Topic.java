package phu.quang.le.Model;

import java.util.List;

public class Topic {

	private int topicID;
	private double topicProbality;
	private List<RecommendTag> recommendTags;

	public int getTopicID () {
		return topicID;
	}

	public void setTopicID (int topicID) {
		this.topicID = topicID;
	}

	public double getTopicProbality () {
		return topicProbality;
	}

	public void setTopicProbality (double topicProbality) {
		this.topicProbality = topicProbality;
	}

	public List<RecommendTag> getRecommendTags () {
		return recommendTags;
	}

	public void setRecommendTags (List<RecommendTag> recommendTags) {
		this.recommendTags = recommendTags;
	}
}
