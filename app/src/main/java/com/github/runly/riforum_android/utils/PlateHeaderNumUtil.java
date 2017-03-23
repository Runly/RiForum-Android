package com.github.runly.riforum_android.utils;

/**
 * Created by ranly on 17-3-10.
 */

public class PlateHeaderNumUtil {
	public static int getPlateHeaderNumber(int p) {
		if (0 <= p && p < 2) {
			return 0;
		}

		if (2 <= p && p < 7) {
			return 1;
		}

		p = p - 6;
		int sum = 1;

		if (p <= 0) {
			return sum;
		} else {
			do {
				p = p - 5;
				sum++;
			} while (p > 0);
		}
		return sum;
	}

	public static int getInsertIndex(int times) {
		return 1 + (times - 1) * 5;
	}
}
