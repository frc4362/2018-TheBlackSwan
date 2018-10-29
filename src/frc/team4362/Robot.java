package frc.team4362;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.team4362.commands.*;
import frc.team4362.commands.auton.DriveAcrossLineAuton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.BetterTimedRobot;
import frc.team4362.util.joy.DPadListener;

/**
 * It's so minimal and I l o v e it
 * EDIT 4/7: F
 * EDIT 10/25: FFFF
 *
 * https://i.redd.it/mf55wyr77icy.png
 *
 * @author Ethan Malzone
 * @see BetterTimedRobot
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Robot extends BetterTimedRobot {
	private final MouthListener m_mouthListener;

	/**
	 * Avoid doing work here, and NEVER do networking- called VERY early
	 * in the robot runtime
	 */
	public Robot() {
		super(true);
		m_mouthListener = new MouthListener(
			Hardware.getInstance().getLift(),
			Hardware.getInstance().getIntakes().getMouth()
		);
	}

	@Override
	public SendableChooser<Command> getAutonCommands() {
		return new AutonSelector();
	}

	@Override
	public Command getDefaultAuton() {
		return new DriveAcrossLineAuton();
	}

	@Override
	public List<Sendable> getSendables() {
		return Arrays.asList(
				Scheduler.getInstance(),
				Hardware.getInstance().getDriveTrain(),
				Hardware.getInstance().getLift(),
				Hardware.getInstance().getMXP(),
				JoystickDriver.getDriveModeSelector());
	}

	@Override
	public void autonomousStart() {
		Hardware.getInstance().getMXP().reset();
		Hardware.getInstance().getLift().setLiftPreset(Lift.Position.CARRY);
		Scheduler.getInstance().add(m_mouthListener);
	}

	@Override
	public void autonomousPeriodic() {
		stopLiftFromKillingItself();
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopStart() {
		Hardware.getInstance().getDriveTrain().setPIDEnabled(false);
		Hardware.getInstance().getLift().setLiftPreset(Lift.Position.CARRY);

		// make the camera
		try {
			// used to be 512x384@8fps
			final UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(173, 128);
			camera.setFPS(24);

			System.out.println("Camera feed supplied to CameraServer");
		} catch (final Exception ex) {
			System.out.println("CAMERA ERROR");
		}

		OperatorInterface.getInstance().configureControls(
				m_leftStick,
				m_rightStick,
				m_controller,
				m_mouthListener
		);
	}

	@Override
	public List<Command> getTeleopCommands() {
		final Lift lift = Hardware.getInstance().getLift();

		return Arrays.asList(
				new JoystickDriver(
						Hardware.getInstance().getDriveTrain(),
						m_leftStick,
						m_rightStick
				),
				new LiftControllerListener(
						Hardware.getInstance().getLift(),
						m_controller
				),
				DPadListener.of(m_controller,
					new HashMap<DPadListener.Direction, Command>() {{
						put(DPadListener.Direction.NORTH,
								new LiftPositionChange(Lift.Position.SCALE, 0));
						put(DPadListener.Direction.EAST,
								new LiftPositionChange(Lift.Position.CARRY, 0));
						put(DPadListener.Direction.SOUTH,
								new LiftPositionChange(Lift.Position.BOTTOM, 0));
						put(DPadListener.Direction.WEST,
								new LiftPositionChange(Lift.Position.NEW_SCALE, 0));
					}}),
				m_mouthListener,
				new ClimbListener(m_controller)
		);
	}

	@Override
	public void teleopPeriodic() {
		stopLiftFromKillingItself();
		Scheduler.getInstance().run();
	}

	/**
	 * Called every single tick- but called before the Teleop and Auton versions,
	 * so is not always usable
	 * can't have stopLiftFromKillingItself() call due to call order
	 */
	@Override
	public void robotPeriodic() {
		Hardware.getInstance().getLEDs().update();
	}

	// basically keeps the lift mostly in place by convincing it's at the setpoint
	private void stopLiftFromKillingItself() {
		final Lift lift = Hardware.getInstance().getLift();
		if (lift.isAtSetpoint()) {
			lift.getTalonLeft().disable();
			lift.getTalonRight().disable();
		} else {
			lift.adjustPosition(0.0);
		}
	}
}
