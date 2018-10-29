package frc.team4362.commands.auton;

import frc.team4362.commands.RunIntakes;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

public final class SideSwitchAuton extends PowerUpCommandGroup {
	public enum Side {
		LEFT(1.0, 'L'),
		RIGHT(-1.0, 'R');

		public final double multiplier;
		public final char character;

		Side(final double m, final char ch) {
			multiplier = m;
			character = ch;
		}
	}

	private final Side m_side;

	public SideSwitchAuton(final Side s) {
		m_side = s;
	}

	@Override
	public void init() {
		addSequential(Navigate.to(0, 130, 0, 0.9, 6000));

		if (getOurSwitchSide() == m_side.character) {
			addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 0, 6000));
			addSequential(Navigate.to(0, 20, 0, 0.5,1000));
			addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING, 750));
		}
	}
}
