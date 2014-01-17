package org.shyyko.ex.fm.reanimate.alternative.net.zaycev;

import com.google.gson.Gson;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.shyyko.ex.fm.reanimate.alternative.AbstractAlternative;
import org.shyyko.ex.fm.reanimate.alternative.exception.AlternativeRetrievingException;
import org.shyyko.ex.fm.reanimate.alternative.net.zaycev.jsoup.MailRuSearchJsoup;
import org.shyyko.ex.fm.reanimate.model.Track;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 */
public class ZaycevNetAlternative extends AbstractAlternative {
	private static final String ZAYCEV_NET = "http://zaycev.net";
	private static final String URL = "http://go.mail.ru/search_site?q=%s&p=1&aux=Kd7dJd";
	private static final String SEARCH_RESULT_SELECTOR = "#js-result__li-1 > div > a";
	private static final String TRACKS_SELECTOR = ".musicset-track-list__item.musicset-track";

	@Override
	public Track getAlternative(Track toFind) throws AlternativeRetrievingException {
		Track track = null;
		CookieStore cookieStore = new BasicCookieStore();
		HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		try {
			RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
			//getting to zaycev.net for cookies :)
			httpClient.execute(createHttpGetWithConfig(ZAYCEV_NET, requestConfig));
			//searching by mail.ru search
			String response = httpClient.execute(createHttpGetWithConfig(getBaseUrl(toFind), requestConfig), new BasicResponseHandler());
			MailRuSearchJsoup mailRuSearchJsoup = new MailRuSearchJsoup(response);
			String href = mailRuSearchJsoup.getBestMatchingSongByLevenshteinDistance(toFind).getLink();
			//going to zaycev.net and getting track, retrieving json data pointing to track url
			response = httpClient.execute(createHttpGetWithConfig(href, requestConfig), new BasicResponseHandler());
			Document document = Jsoup.parse(response);
			Elements elements = document.select(TRACKS_SELECTOR);
			if (elements.isEmpty()) {
				throw new Exception("Search on zaycev returned no results");
			}
			//getting href to json(data-url)
			href = ZAYCEV_NET + elements.get(0).attr("data-url"); // first is the more accurate result
			response = httpClient.execute(createHttpGetWithConfig(href, requestConfig), new BasicResponseHandler());
			String fileUrl = new Gson().fromJson(response, DataUrl.class).getUrl();
			//now having url for music file all that left is to download it
			track = toFind.clone();
			track.setLocation(fileUrl);
		} catch (Exception e) {
			throw new AlternativeRetrievingException(e);
		}
		return track;
	}

	private static HttpGet createHttpGetWithConfig(String url, RequestConfig requestConfig) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		return httpGet;
	}

	@Override
	protected String getBaseUrl(Track track) throws UnsupportedEncodingException {
		String query = URLEncoder.encode(
				getNullSafeString(track.getCreator()) + ' ' + getNullSafeString(track.getTitle()),
				"ISO-8859-1"
		);

		return String.format(URL, query);
	}

	@Override
	protected String getBaseUrlPrefix() {
		return URL;
	}

	public static final class DataUrl {
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}


}
