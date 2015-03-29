package phu.quang.le.Model;

public class TagWeight {
	private String tag;
	private int weight;

	public TagWeight(String tag, int weight) {
		setTag(tag);
		setWeight(weight);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
