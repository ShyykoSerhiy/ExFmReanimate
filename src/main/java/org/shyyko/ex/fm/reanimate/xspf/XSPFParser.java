package org.shyyko.ex.fm.reanimate.xspf;

import org.shyyko.ex.fm.reanimate.model.Track;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class XSPFParser {
	private static final String TRACK = "track";
	private static final String ALBUM = "album";
	private static final String CREATOR = "creator";
	private static final String IMAGE = "image";
	private static final String TITLE = "title";
	private static final String LOCATION = "location";

	public List<Track> parse(File file) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		List<Track> tracks = null;
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			tracks = new LinkedList<Track>();
			NodeList nodes = doc.getElementsByTagName(TRACK);
			for (int i = 0; i < nodes.getLength(); i++) {
				Element trackElement = (Element) nodes.item(i);
				Track track = new Track();
				track.setAlbum(getElementValue(trackElement, ALBUM));
				track.setCreator(getElementValue(trackElement, CREATOR));
				track.setImage(getElementValue(trackElement, IMAGE));
				track.setLocation(getElementValue(trackElement, LOCATION));
				track.setTitle(getElementValue(trackElement, TITLE));
				tracks.add(track);
			}
		} catch (Exception exception) {
			throw exception;
		}
		return tracks;
	}

	private static String getElementValue(Element element, String childTag) {
		NodeList nodes = element.getElementsByTagName(childTag);
		if (nodes.getLength() > 0) {
			return nodes.item(0).getTextContent();
		}
		return null;
	}
}
