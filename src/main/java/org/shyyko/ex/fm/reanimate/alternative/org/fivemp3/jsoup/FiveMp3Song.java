package org.shyyko.ex.fm.reanimate.alternative.org.fivemp3.jsoup;

/**
 */
public class FiveMp3Song {
	/**
	 * Song creator
	 */
	private String creator;
	/**
	 * Song title
	 */
	private String title;
	/**
	 * Url of the song
	 */
	private String url;

	public FiveMp3Song(String creator, String title, String url) {
		this.creator = creator;
		this.title = title;
		this.url = url;
	}

	public FiveMp3Song() {
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
