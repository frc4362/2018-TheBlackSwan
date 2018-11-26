package frc.team4362.kinematics.math;

import java.util.Objects;
import java.util.TreeMap;

public class InterpolatingTreeMap<
	 K extends InverseInterpolable<K> & Comparable<K>,
	 V extends Interpolable<V>>
	 extends TreeMap<K, V>
{
	private int m_max;

	public InterpolatingTreeMap(final int maxSize) {
		m_max = maxSize;
	}

	public InterpolatingTreeMap() {
		this(0);
	}

	@Override
	public V put(final K key, final V value) {
		if (m_max > 0.0 && m_max <= size()) {
			remove(firstKey());
		}

		super.put(key, value);

		return value;
	}

	public V getInterpolated(final K key) {
		final V val = get(key);

		if (Objects.isNull(val)) {
			final K top = ceilingKey(key),
					bot = floorKey(key);

			if (Objects.isNull(top) && Objects.isNull(bot)) {
				return null;
			} else if (Objects.isNull(top)) {
				return get(bot);
			} else if (Objects.isNull(bot)) {
				return get(top);
			}

			final V topElement = get(top),
					botElement = get(bot);

			return botElement.interpolate(topElement, bot.inverseInterpolate(top, key));
		} else {
			return val;
		}
	}
}
