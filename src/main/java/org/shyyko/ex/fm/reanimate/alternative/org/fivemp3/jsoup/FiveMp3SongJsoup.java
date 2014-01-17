package org.shyyko.ex.fm.reanimate.alternative.org.fivemp3.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shyyko.ex.fm.reanimate.alternative.distance.LevenshteinDistance;
import org.shyyko.ex.fm.reanimate.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FiveMp3SongJsoup {
	private static final String PLAYLIST_SELECTOR = "#playlist > .searchr";
	private static final String SONG_SELECTOR = ".track";
	private static final String SONG_CREATOR_SELECTOR = "b";
	private static final String SONG_TITLE_SELECTOR = "a:nth-child(3)";
	private static final String SONG_URL_SELECTOR = "a:nth-child(1)";

	private List<FiveMp3Song> songs;

	public FiveMp3SongJsoup(String pageToParse) {
		Document document = Jsoup.parse(pageToParse);
		Element playlist = document.select(PLAYLIST_SELECTOR).get(0);
		Elements songElements = playlist.select(SONG_SELECTOR);

		songs = new ArrayList<FiveMp3Song>(songElements.size());
		for (Element songElement : songElements) {
			String creator = songElement.select(SONG_CREATOR_SELECTOR).get(0).text();
			String title = songElement.select(SONG_TITLE_SELECTOR).get(0).text();
			String url = songElement.select(SONG_URL_SELECTOR).get(0).attr("href");
			FiveMp3Song fiveMp3Song = new FiveMp3Song(creator, title, url);
			songs.add(fiveMp3Song);
		}
	}

	public List<FiveMp3Song> getSongs() {
		return songs;
	}

	public FiveMp3Song getBestMatchingSongByLevenshteinDistance(Track track) {
		if (songs == null || songs.isEmpty()) {
			return null;
		}

		int minimumDistance = Integer.MAX_VALUE;
		FiveMp3Song bestMatching = null;
		for (FiveMp3Song song : songs) {
			int creatorDistance = LevenshteinDistance.getLevenshteinDistance(track.getTitle(), song.getTitle());
			int titleDistance = LevenshteinDistance.getLevenshteinDistance(track.getTitle(), song.getTitle());
			int fullDistance = creatorDistance + titleDistance;
			if (fullDistance < minimumDistance) {
				bestMatching = song;
				minimumDistance = fullDistance;
			}
			if (minimumDistance == 0) {
				break;
			}
		}
		return bestMatching;
	}
}
