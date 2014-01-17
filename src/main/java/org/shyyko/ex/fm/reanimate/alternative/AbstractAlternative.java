package org.shyyko.ex.fm.reanimate.alternative;

import org.shyyko.ex.fm.reanimate.model.Track;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 */
public abstract class AbstractAlternative implements IsAlternative {
	protected abstract String getBaseUrl(Track track) throws UnsupportedEncodingException;
	protected String getNullSafeString(String string) {
		return string == null ? "" : string;
	}

	protected abstract String getBaseUrlPrefix();
}
