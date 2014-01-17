package org.shyyko.ex.fm.reanimate.download;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.shyyko.ex.fm.reanimate.alternative.IsAlternative;
import org.shyyko.ex.fm.reanimate.alternative.exception.AlternativeRetrievingException;
import org.shyyko.ex.fm.reanimate.model.Track;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class AudioDownloader {
	private static final String AUDIO_CONTENT = "audio";
	private static final String VIDEO_CONTENT = "video";
	private static Logger logger = Logger.getLogger(AudioDownloader.class.getSimpleName());
	private AtomicInteger successCounter;
	private AtomicInteger failureCounter;

	private OnProgressListener onProgressListener;
	private IsAlternative alternative;

	public AudioDownloader(IsAlternative alternative) {
		this.alternative = alternative;
		successCounter = new AtomicInteger();
		failureCounter = new AtomicInteger();
	}

	public OnProgressListener getOnProgressListener() {
		return onProgressListener;
	}

	public void setOnProgressListener(OnProgressListener onProgressListener) {
		this.onProgressListener = onProgressListener;
	}

	public void downloadTracks(List<Track> tracks, File downloadToDirectory) {
		ThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
		for (Track track : tracks) {
			poolExecutor.execute(new DownloadTrackRunnable(tracks.size(), track, downloadToDirectory));
		}
	}

	public boolean downloadFile(Track track, File downloadToDirectory) {
		if (track == null) {
			return false;
		}
		File fileToDownloadTo = new File(downloadToDirectory.getAbsolutePath()
				+ File.separator + (track.getCreator() + " - " + track.getTitle() + ".mp3").replace(File.separator, ""));
		HttpClient httpClient = buildHttpClient();
		try {
			HttpResponse httpResponse = httpClient.execute(new HttpGet(track.getLocation()));
			HttpEntity entity = httpResponse.getEntity();
			//checking if content is mp3
			String contentType = entity.getContentType().getValue();
			if (contentType.contains(AUDIO_CONTENT) || contentType.contains(VIDEO_CONTENT)) { //video/mpeg sometimes is used for mp3
				IOUtils.copy(entity.getContent(), new FileOutputStream(fileToDownloadTo));
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			//e.printStackTrace(); //todo
		}
		return false;
	}

	private static HttpClient buildHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
				.setConnectTimeout(10000)
				.setConnectionRequestTimeout(10000)
				.setSocketTimeout(10000)
				.build();
		return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
	}

	public static interface OnProgressListener {
		void onProgress(double progress, double success, double failure);
	}

	private class DownloadTrackRunnable implements Runnable {
		private final File downloadToDirectory;
		private final int songsSize;
		private Track trackToDownload;

		private DownloadTrackRunnable(int songsSize, Track trackToDownload, File downloadToDirectory) {
			this.songsSize = songsSize;
			this.trackToDownload = trackToDownload;
			this.downloadToDirectory = downloadToDirectory;
		}

		@Override
		public void run() {

			logger.log(Level.INFO, "Downloading  : " + (failureCounter.get() + successCounter.get() + "; "
					+ trackToDownload));
			boolean downloaded = downloadFile(trackToDownload, downloadToDirectory);
			if (!downloaded) {
				logger.log(Level.INFO, "xspf source is broken. Trying to find alternative");
				try {
					Track alternativeTrack = alternative.getAlternative(trackToDownload);
					logger.log(Level.INFO, "Alternative : " + alternativeTrack);
					downloaded = downloadFile(alternativeTrack, downloadToDirectory);
				} catch (AlternativeRetrievingException e) {
					//todo
					//e.printStackTrace();
				}
			}
			int successCount;
			int failureCount;
			if (downloaded) {
				logger.log(Level.INFO, "Downloaded : " + trackToDownload);
				successCount = successCounter.incrementAndGet();
				failureCount = failureCounter.get();
			} else {
				logger.log(Level.INFO, "Failed to download : " + trackToDownload);
				successCount = successCounter.get();
				failureCount = failureCounter.incrementAndGet();
			}
			if (onProgressListener != null) {
				logger.log(Level.INFO, "Progress : successes :" + successCount + "; failures :" + failureCount);
				onProgressListener.onProgress(
						(successCount + failureCount) / (double) songsSize,
						successCount / (double) songsSize,
						failureCount / (double) songsSize
				);
			}
		}
	}

}
