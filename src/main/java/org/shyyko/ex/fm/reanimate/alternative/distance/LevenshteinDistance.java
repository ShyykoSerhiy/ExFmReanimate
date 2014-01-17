package org.shyyko.ex.fm.reanimate.alternative.distance;

/**
 */
public class LevenshteinDistance {
	public static int getLevenshteinDistance(String first, String second) {
		if (first.equals(second)) return 0;
		if (first.length() == 0) return second.length();
		if (second.length() == 0) return first.length();

		int[] previousRow = new int[second.length() + 1];
		int[] currentRow = new int[second.length() + 1];

		for (int i = 0; i < previousRow.length; i++)
			previousRow[i] = i;

		for (int i = 0; i < first.length(); i++) {
			currentRow[0] = i + 1;

			for (int j = 0; j < second.length(); j++) {
				int cost = (first.charAt(i) == second.charAt(j)) ? 0 : 1;
				currentRow[j + 1] = min(currentRow[j] + 1, previousRow[j + 1] + 1, previousRow[j] + cost);
			}

			for (int j = 0; j < previousRow.length; j++)
				previousRow[j] = currentRow[j];
		}

		return currentRow[second.length()];
	}

	private static <T extends Comparable> T min(T... values) {
		if (values.length == 0) {
			return null;
		}
		T minimum = values[0];

		for (T value : values) {
			if (value.compareTo(minimum) < 0) {
				minimum = value;
			}
		}
		return minimum;
	}
}
