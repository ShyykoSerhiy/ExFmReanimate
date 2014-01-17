package org.shyyko.ex.fm.reanimate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.shyyko.ex.fm.reanimate.alternative.IsAlternative;
import org.shyyko.ex.fm.reanimate.alternative.net.zaycev.ZaycevNetAlternative;
import org.shyyko.ex.fm.reanimate.download.AudioDownloader;
import org.shyyko.ex.fm.reanimate.model.Track;
import org.shyyko.ex.fm.reanimate.xspf.XSPFParser;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class Controller {
	public TableView tracksTableView;
	public MediaView mediaView;
	public ProgressBar downloadProgressBar;
	public ProgressBar downloadSuccessProgressBar;
	public ProgressBar downloadFailureProgressBar;
	private List<Track> tracks;
	private Window window;
	private IsAlternative alternative;
	private MediaPlayer mediaPlayer;

	public void init(Window window) {
		this.window = window;
		alternative = new ZaycevNetAlternative();
	}

	@SuppressWarnings("unchecked")
	public void onOpenXspfFileMenuClick(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(window);
		if (file != null && file.exists()) {
			XSPFParser xspfParser = new XSPFParser();
			try {
				tracks = xspfParser.parse(file);
				tracksTableView.getItems().addAll(tracks);
			} catch (Exception e) {
				//e.printStackTrace();  //todo
			}
		}
		tracksTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() > 1) {
					Track track = (Track) tracksTableView.getSelectionModel().getSelectedItem();
					try {
						track = alternative.getAlternative(track);
					} catch (Exception e) {
						showError("Error while finding alternatives", e);
						return;
					}
					Media media = new Media(track.getLocation());
					//mediaView.setMediaPlayer(player);
					if (mediaPlayer != null) {
						mediaPlayer.pause();
					}
					mediaPlayer = new MediaPlayer(media);
					mediaPlayer.setOnError(new Runnable() {
						@Override
						public void run() {
							showError("Error while playing", mediaPlayer.getError());
						}
					});
					mediaPlayer.play();
				}
			}
		});
	}

	public void onDownloadMenuClick(ActionEvent event) {
		Object selectedItem = tracksTableView.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showError("No track selected", null);
			return;
		}
		Track selectedTrack = (Track) selectedItem;
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jFileChooser.showSaveDialog(null);
		File file = jFileChooser.getSelectedFile();
		if (file != null) {
			try {
				downloadProgressBar.setProgress(0);

				boolean downloaded = new AudioDownloader(alternative).downloadFile(selectedTrack, file);
				System.out.println("1 " + downloaded);
				if (!downloaded) {
					selectedTrack = alternative.getAlternative(selectedTrack);
					downloaded = new AudioDownloader(alternative).downloadFile(selectedTrack, file);
					System.out.println("2 " + downloaded);
				}

				downloadProgressBar.setProgress(0);
			} catch (Exception e) {
				showError("Error during saving", e);
			}
		}
	}

	private void showError(String error, Exception e) {
		//todo
	}

	public void onDownloadAllMenuClick(ActionEvent event) {
		if (tracks == null || tracks.isEmpty()) {
			showError("No tracks to download", null);
			return;
		}
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(window);
		if (file != null) {
			AudioDownloader audioDownloader = new AudioDownloader(alternative);
			audioDownloader.setOnProgressListener(new AudioDownloader.OnProgressListener() {
				@Override
				public void onProgress(double progress, double success, double failure) {
					downloadProgressBar.setProgress(progress);
					downloadSuccessProgressBar.setProgress(success);
					downloadFailureProgressBar.setProgress(failure);
				}
			});
			audioDownloader.downloadTracks(tracks, file);
		}
	}
}
