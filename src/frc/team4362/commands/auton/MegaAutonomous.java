package frc.team4362.commands.auton;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import frc.team4362.commands.LiftPositionChange;
import frc.team4362.commands.RunIntakes;
import frc.team4362.hardwares.Hardware;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.IntakeWheelSet;
import frc.team4362.util.command.PowerUpCommandGroup;

import static frc.team4362.util.command.Commands.commandOf;

@SuppressWarnings("WeakerAccess")
public class MegaAutonomous extends PowerUpCommandGroup {
	public enum Side {
		LEFT(1, 'L', 0.0, 0.0, 0.0),
		RIGHT(-1, 'R', 12.0, 3.0, 10.0);

		public final int multiplier;
		public final char character;
		public final double xtraInches;
		public final double xtraFirstDegrees;
		public final double xtraSecondDegrees;

		Side(
				final int m,
				final char ch,
				final double xInches,
				final double xFirstDegrees,
				final double xSecondDegrees
		) {
			multiplier = m;
			character = ch;
			xtraInches = xInches;
			xtraFirstDegrees = xFirstDegrees;
			xtraSecondDegrees = xSecondDegrees;
		}
	}

	private final Side m_side;
	private final boolean m_prioritizeSwitch, m_doCross;

	public MegaAutonomous(final Side s, final boolean switchPriority, final boolean doCross) {
		m_side = s;
		m_prioritizeSwitch = switchPriority;
		m_doCross = doCross;
	}

	@Override
	public void init() {
		addSequential(commandOf(() ->
			Hardware.getInstance().getIntakes().getMouth().set(DoubleSolenoid.Value.kForward)));
		addSequential(new LiftPositionChange(Lift.Position.CARRY, 0));

		System.out.println("Scale on our side: " + isScaleOnOurSide());
		System.out.println("Switch on our side: " + isSwitchOnOurSide());

		if (isScaleOnOurSide()) {
			closeScaleFromStart();
			pickUpSecondCube();

			System.out.println("AUTON : FIRST CUBE SCALE ON OUR SIDE");

			if (m_prioritizeSwitch && isSwitchOnOurSide()) {
				putSecondCubeInSwitch();
				System.out.println("AUTON : SECOND CUBE SWITCH ON OUR SIDE");
			} else {
				putSecondCubeInScale();
				System.out.println("AUTON : SECOND CUBE SCALE ON OUR SIDE");
			}
		} else if (m_prioritizeSwitch && isSwitchOnOurSide()) {
			closeSwitchOnly();
			System.out.println("AUTON : PRIORITIZING SWITCH ON OUR SIDE");
		} else if (m_doCross) {
			farScaleFromStart();
			System.out.println("AUTON : PRIORITIZING CROSSING FOR THE SCALE");
		} else if (isSwitchOnOurSide()) {
			closeSwitchOnly();
			System.out.println("AUTON : FALLING BACK TO THE CLOSE SWITCH");
		} else {
			doHalfCross();
			System.out.println("AUTON : JUST CROSSING THE LINE");
		}
	}

	private boolean isScaleOnOurSide() {
		return getOurScaleSide() == m_side.character;
	}

	private boolean isSwitchOnOurSide() {
		return getOurSwitchSide() == m_side.character;
	}

	protected void closeScaleFromStart() {
		addSequential(Navigate.to(0, 186, 0, 0.9, 10000));
		addSequential(Navigate.to(0, 70 + m_side.xtraInches, 0, 0.8, 10000));
		addSequential(commandOf(() ->
			Hardware.getInstance().getDriveTrain().drive(0, 0)));
		addSequential(new LiftPositionChange(Lift.Position.TOP, 500));
		addSequential(Navigate.to(0, 0, 55 * m_side.multiplier, 500, 2000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
		addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 0));
	}

	protected void farScaleFromStart() {
		addSequential(Navigate.to(0, 188, 0, 0.9, 6000));
		addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 9, 4000));
		addSequential(new DriveStraight(176, 0.9, 7000));
		addSequential(new LiftPositionChange(Lift.Position.TOP, 0));
		addSequential(Navigate.to(0, 0, -104 * m_side.multiplier, 3, 4000));
		addSequential(Navigate.to(0, 18, 0, 0.7, 3000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
		addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 1000));
		addSequential(Navigate.to(0, 0, -139 * m_side.multiplier, 0, 4000));
		addParallel(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 1500));
		addSequential(Navigate.to(0, 47, 0, 0.5, 2000));
		addSequential(Navigate.to(0, -47, 0, -0.5, 2000));
	}

	protected void closeSwitchOnly() {
		addSequential(Navigate.to(0, 130, 0, 0.9, 6000));
		addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 2, 6000));
		addSequential(Navigate.to(0, 20, 0, 0.5,1000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
	}

	protected void pickUpSecondCube() {
		addSequential(
			new TurnToAngle((152 + m_side.xtraFirstDegrees) * m_side.multiplier, 3000));

		addSequential(Navigate.to(0, 70, 0, 0.7, 4500));

		addParallel(Navigate.to(0, 23, 0, 0.35, 1850));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 1850));
	}

	protected void putSecondCubeInScale() {
		addSequential(Navigate.to(0, -24, 0, -0.6, 2000));
		addSequential(new LiftPositionChange(Lift.Position.TOP, 0));
		addSequential(
			Navigate.to(0, 0,
				(-120 - m_side.xtraSecondDegrees) * m_side.multiplier, 0, 3000));
		addSequential(Navigate.to(0, 28, 0, 0.25, 2000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
	}

	protected void putSecondCubeInSwitch() {
		addSequential(new LiftPositionChange(Lift.Position.CARRY_PLUS, 1000));
		addSequential(Navigate.to(0, 24, 0, 0.7, 2000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING, 1000));
	}

	protected void doHalfCross() {
		addSequential(Navigate.to(0, 188, 0, 0.9, 10000));
		addSequential(Navigate.to(0, 0, 90, 1, 4000));
		addSequential(Navigate.to(0, 50, 0, 0.7, 5000));
	}
}
