package frc.team4362.util;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

@SuppressWarnings("WeakerAccess")
public final class Point {
	public final double x, y;

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public static Point origin() {
		return new Point(0, 0);
	}

	public static double distance(final Point a, final Point b) {
		return sqrt(pow(b.x - a.x, 2) + pow(b.y - a.y, 2));
	}
}
