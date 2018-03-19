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
		LEFT(1, 'L'),
		RIGHT(-1, 'R');

		public final int multiplier;
		public final char character;

		Side(final int m, final char ch) {
			multiplier = m;
			character = ch;
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
			justCrossTheLine();
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
		addSequential(Navigate.to(0, 64, 0, 0.8, 10000));
		addSequential(commandOf(() ->
			Hardware.getInstance().getDriveTrain().drive(0, 0)));
		addSequential(new LiftPositionChange(Lift.Position.TOP, 500));
		addSequential(Navigate.to(0, 0, 55 * m_side.multiplier, 500, 2000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
		addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 0));
	}

	protected void farScaleFromStart() {
		addSequential(Navigate.to(0, 200, 0, 0.8, 6000));
		addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 9, 4000));
		addSequential(Navigate.to(0, 193, 0, 0.8, 7000));
		addSequential(new LiftPositionChange(Lift.Position.TOP, 0));
		addSequential(Navigate.to(0, 0, -100 * m_side.multiplier, 3, 4000));
		addSequential(Navigate.to(0, 18, 0, 0.7, 3000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
		addSequential(new LiftPositionChange(Lift.Position.BOTTOM, 1000));
		addSequential(Navigate.to(0, 0, -140 * m_side.multiplier, 0, 4000));
		addSequential(Navigate.to(0, 30, 0, 0.5, 2000));
	}

	protected void closeSwitchOnly() {
		addSequential(Navigate.to(0, 130, 0, 0.9, 6000));
		addSequential(Navigate.to(0, 0, 90 * m_side.multiplier, 2, 6000));
		addSequential(Navigate.to(0, 20, 0, 0.5,1000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
	}

	protected void pickUpSecondCube() {
		addSequential(new TurnToAngle(152 * m_side.multiplier, 3000));

		addSequential(Navigate.to(0, 54, 0, 0.7, 3000));

		addParallel(Navigate.to(0, 23, 0, 0.35, 2250));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.INTAKING, 2250));
	}

	protected void putSecondCubeInScale() {
		addSequential(Navigate.to(0, -24, 0, -0.3, 2000));
		addSequential(new LiftPositionChange(Lift.Position.TOP, 0));
		addSequential(Navigate.to(0, 0, -135 * m_side.multiplier, 0, 3000));
		addSequential(Navigate.to(0, 12, 0, 0.25, 2000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING), 0.75);
	}

	protected void putSecondCubeInSwitch() {
		addSequential(new LiftPositionChange(Lift.Position.CARRY_PLUS, 2000));
		addSequential(Navigate.to(0, 18, 0, 0.5, 3000));
		addSequential(new RunIntakes(IntakeWheelSet.SpeedPreset.OUTTAKING, 500));
	}

	protected void justCrossTheLine() {
		addSequential(Navigate.to(0, 140, 0, 0.9, 10000));
	}
}
