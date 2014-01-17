package org.shyyko.ex.fm.reanimate.alternative;

import org.shyyko.ex.fm.reanimate.alternative.exception.AlternativeRetrievingException;
import org.shyyko.ex.fm.reanimate.model.Track;

/**
 */
public interface IsAlternative {
	/**
	 * Finds alternative url for toFind track. The alternative can be not "exact" match.
	 *
	 * @param toFind track to find
	 * @return track with the same creator and title or null if it's not possible to find track
	 */
	Track getAlternative(Track toFind) throws AlternativeRetrievingException;
}
