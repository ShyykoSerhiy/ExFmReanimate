package org.shyyko.ex.fm.reanimate.alternative.org.fivemp3;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.shyyko.ex.fm.reanimate.alternative.AbstractAlternative;
import org.shyyko.ex.fm.reanimate.alternative.exception.AlternativeRetrievingException;
import org.shyyko.ex.fm.reanimate.alternative.org.fivemp3.jsoup.FiveMp3Song;
import org.shyyko.ex.fm.reanimate.alternative.org.fivemp3.jsoup.FiveMp3SongJsoup;
import org.shyyko.ex.fm.reanimate.model.Track;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Alternative finder from "http://5mp3.org/music/"
 */
public class FiveMp3OrgAlternative extends AbstractAlternative {
	private static final String BASE_URL = "http://5mp3.org/music/";

	@Override
	public Track getAlternative(Track toFind) throws AlternativeRetrievingException {
		HttpClient httpClient = HttpClients.createDefault();
		Track track = null;
		try {
			String response = httpClient.execute(new HttpGet(getBaseUrl(toFind)), new BasicResponseHandler());
			FiveMp3SongJsoup fiveMp3SongJsoup = new FiveMp3SongJsoup(response);
			FiveMp3Song fiveMp3Song = fiveMp3SongJsoup.getBestMatchingSongByLevenshteinDistance(toFind);
			if (fiveMp3Song == null) {
				return null;
			}

			track = toFind.clone();
			track.setLocation(fiveMp3Song.getUrl());

		} catch (Exception e) {
			throw new AlternativeRetrievingException(e);
		}
		return track;
	}

	@Override
	protected String getBaseUrl(Track track) throws UnsupportedEncodingException {
		return getBaseUrlPrefix() +
				URLEncoder.encode(
						getNullSafeString(track.getCreator()) + ' ' + getNullSafeString(track.getTitle()),
						"ISO-8859-1"
				);
	}

	@Override
	protected String getBaseUrlPrefix() {
		return BASE_URL;
	}
}
