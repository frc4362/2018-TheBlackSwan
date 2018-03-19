package frc.team4362;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4362.commands.*;
import frc.team4362.commands.log.LogAHRSHeading;
import frc.team4362.commands.log.LogEncoders;
import frc.team4362.commands.log.LogLiftPositions;
import frc.team4362.hardwares.CameraHardware;
import frc.team4362.hardwares.Hardware;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team4362.subsystems.Lift;
import frc.team4362.util.ExtendedIterativeRobot;
import frc.team4362.util.joy.DPadListener;

/**
 * It's so minimal and I l o v e it
 *
 * https://i.redd.it/mf55wyr77icy.png
 *
 * @author Ethan
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Robot extends ExtendedIterativeRobot {
	private final MouthListener m_mouthListener;

	public Robot() {
		super();

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
	public void autonomousStart() {
		Hardware.getInstance().getMXP().reset();
		Hardware.getInstance().getLift().setLiftPreset(Lift.Position.CARRY);

		Scheduler.getInstance().add(m_mouthListener);
		Scheduler.getInstance().add(new LogEncoders(Hardware.getInstance().getDriveTrain()));
	}

	@Override
	public void autonomousPeriodic() {
		stopLiftFromKillingItself();
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopStart() {
		try {
			final UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(512, 384);
			camera.setFPS(8);
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
				new DriveWithJoysticks(
						Hardware.getInstance().getDriveTrain(),
						m_leftStick,
						m_rightStick
				),
				new LiftControllerListener(
						Hardware.getInstance().getLift(),
						m_controller
				),
				new LogEncoders(Hardware.getInstance().getDriveTrain()),
				new LogLiftPositions(lift),
				new LogAHRSHeading(Hardware.getInstance().getMXP()),
				DPadListener.of(m_controller, new HashMap<DPadListener.Direction, Command>() {{
					put(DPadListener.Direction.NORTH,
							new LiftPositionChange(Lift.Position.SCALE, 0));
					put(DPadListener.Direction.EAST,
							new LiftPositionChange(Lift.Position.CARRY, 0));
					put(DPadListener.Direction.SOUTH,
							new LiftPositionChange(Lift.Position.BOTTOM, 0));
					put(DPadListener.Direction.WEST,
							new LiftPositionChange(Lift.Position.NEW_SCALE, 0));
				}}),
				m_mouthListener
		);
	}

	@Override
	public void teleopPeriodic() {
		stopLiftFromKillingItself();
		Scheduler.getInstance().run();
		Hardware.getInstance().getDriveTrain().setPIDEnabled(false);
		SmartDashboard.putNumber("climber pos",
				Hardware.getInstance().getClimber().getTalon().getSelectedSensorPosition(0));
	}

	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber(
				"left lift speed",
				Hardware.getInstance().getLift().getTalonLeft()
						.getSelectedSensorVelocity(0)
		);

		SmartDashboard.putNumber(
				"right lift speed",
				Hardware.getInstance().getLift().getTalonLeft()
						.getSelectedSensorVelocity(0)
		);
	}

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
