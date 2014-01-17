package org.shyyko.ex.fm.reanimate.alternative.net.zaycev.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shyyko.ex.fm.reanimate.alternative.distance.LevenshteinDistance;
import org.shyyko.ex.fm.reanimate.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MailRuSearchJsoup {
	private static final String SONG_SELECTOR = ".result__content";
	private static final String SONG_TITLE_SELECTOR = ".result__title > a";
	private static final String SONG_URL_SELECTOR = "> a";

	private List<MailRuSong> songs;

	public MailRuSearchJsoup(String pageToParse) {
		Document document = Jsoup.parse(pageToParse);
		if (document.select(".not-found").isEmpty()) {
			Elements elements = document.select(SONG_SELECTOR);
			songs = new ArrayList<MailRuSong>(elements.size());
			for (Element element : elements) {
				MailRuSong mailRuSong = new MailRuSong();
				mailRuSong.setLink(element.select(SONG_URL_SELECTOR).get(0).attr("href").replace("pages", "online"));
				mailRuSong.setTitle(element.select(SONG_TITLE_SELECTOR).get(0).text());
				songs.add(mailRuSong);
			}
		}
	}

	public List<MailRuSong> getSongs() {
		return songs;
	}

	public MailRuSong getBestMatchingSongByLevenshteinDistance(Track track) {
		if (songs == null || songs.isEmpty()) {
			return null;
		}

		int minimumDistance = Integer.MAX_VALUE;
		MailRuSong bestMatching = null;
		String fullTitle = track.getCreator() + " - " + track.getTitle();
		for (MailRuSong song : songs) {
			int distance = LevenshteinDistance.getLevenshteinDistance(fullTitle, song.getTitle());
			if (distance < minimumDistance) {
				bestMatching = song;
				minimumDistance = distance;
			}
		}
		return bestMatching;
	}
}
