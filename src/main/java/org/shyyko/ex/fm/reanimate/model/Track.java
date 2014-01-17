package org.shyyko.ex.fm.reanimate.model;

/**
 */
public class Track implements Cloneable {
	private String album;
	private String creator;
	private String image;
	private String title;
	private String location;
	private String localFile;

	public Track() {
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	@Override
	public Track clone() throws CloneNotSupportedException {
		Track track = (Track) super.clone();
		track.setAlbum(track.getAlbum());
		track.setCreator(track.getCreator());
		track.setImage(track.getImage());
		track.setLocalFile(track.getLocalFile());
		track.setLocation(track.getLocation());
		track.setTitle(track.getTitle());
		return track;
	}

	@Override
	public String toString() {
		return "Track{" +
				"album='" + album + '\'' +
				", creator='" + creator + '\'' +
				", image='" + image + '\'' +
				", title='" + title + '\'' +
				", location='" + location + '\'' +
				", localFile='" + localFile + '\'' +
				'}';
	}
}
