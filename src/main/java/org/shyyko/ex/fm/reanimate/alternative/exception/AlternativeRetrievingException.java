package org.shyyko.ex.fm.reanimate.alternative.exception;

/**
 */
public class AlternativeRetrievingException extends Exception {
	public AlternativeRetrievingException(Throwable cause) {
		super("Error while getting alternative", cause);
	}
}
