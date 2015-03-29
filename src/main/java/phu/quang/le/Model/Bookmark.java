package phu.quang.le.Model;

import java.util.List;

public class Bookmark {

	private String url;
	private String title;
	private List<String> tags;

	public String getUrl () {
		return url;
	}

	public void setUrl (String url) {
		this.url = url;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle (String title) {
		this.title = title;
	}

	public List<String> getTags () {
		return tags;
	}

	public void setTags (List<String> tags) {
		this.tags = tags;
	}
}
