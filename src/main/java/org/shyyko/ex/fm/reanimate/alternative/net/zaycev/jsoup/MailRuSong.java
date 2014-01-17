package org.shyyko.ex.fm.reanimate.alternative.net.zaycev.jsoup;

/**
*/
public class MailRuSong {
	private String title;
	private String link;

	MailRuSong(String title, String link) {
		this.title = title;
		this.link = link;
	}

	MailRuSong() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String song) {
		this.title = song;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
