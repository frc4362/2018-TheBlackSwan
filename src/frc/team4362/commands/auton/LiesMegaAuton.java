package frc.team4362.commands.auton;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class LiesMegaAuton extends MegaAutonomous {
	protected final boolean m_isScaleSide, m_isSwitchSide;

	public LiesMegaAuton(
			final Side s,
			final boolean switchPriority,
			final boolean doCross,
			final boolean isScaleSide,
			final boolean isSwitchSide
	) {
		super(s, switchPriority, doCross);

		m_isScaleSide = isScaleSide;
		m_isSwitchSide = isSwitchSide;
	}

	@Override
	protected boolean isScaleOnOurSide() {
		return m_isScaleSide;
	}

	@Override
	protected boolean isSwitchOnOurSide() {
		return m_isSwitchSide;
	}
}
