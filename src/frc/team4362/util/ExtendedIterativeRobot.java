package frc.team4362.util;

import java.util.List;
import java.util.Optional;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team4362.commands.any.Wait;
import frc.team4362.commands.auton.DriveAcrossLineAuton;
import frc.team4362.util.command.Commands;
import frc.team4362.util.command.DelayedAutonFactory;
import frc.team4362.util.command.PowerUpCommandGroup;
import frc.team4362.util.joy.Gemstick;

import static frc.team4362.util.command.Commands.autonOf;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ExtendedIterativeRobot extends IterativeRobot {
	protected final XboxController m_controller;
	protected final Gemstick m_leftStick, m_rightStick;
	protected SendableChooser<Command> m_autonSelector;

	public ExtendedIterativeRobot() {
		resetAutonSelector();

		m_leftStick  = new Gemstick("Left Stick", 0);
		m_rightStick = new Gemstick("Right Stick", 1);
		m_controller = new XboxController(2);
	}

	public abstract List<Command> getTeleopCommands();
	public abstract SendableChooser<Command> getAutonCommands();

	@Override
	public void robotInit() {
		resetAutonSelector();
	}

	public void teleopStart() {
		System.out.println("No implementation of teleopStart... Override me!");
	}

	public void autonomousStart() {
		System.out.println("No implementation of autonomousStart... Override me!");
	}

	@Override
	public final void autonomousInit() {
		System.out.println("Entering auton...");

		Scheduler.getInstance().removeAll();
		Scheduler.getInstance().add(
				new Wait(DelayedAutonFactory.getAppropriateDelay()) {
					@Override
					public void end() {
						Scheduler.getInstance().add(autonOf(getSelectedAuton(), new Wait(15000)));
					}
				}
		);

		autonomousStart();
	}

	@Override
	public final void teleopInit() {
		Scheduler.getInstance().removeAll();
		getTeleopCommands().forEach(Scheduler.getInstance()::add);
		teleopStart();
	}

	public final Command getSelectedAuton() {
		final Command auton =
				Optional.ofNullable(m_autonSelector.getSelected())
					    .orElseGet(DriveAcrossLineAuton::new);

		if (auton instanceof PowerUpCommandGroup) {
			((PowerUpCommandGroup) auton).init();
		}

		return auton;
	}

	protected void resetAutonSelector() {
		m_autonSelector = getAutonCommands();
		SmartDashboard.putData("auton", m_autonSelector);
	}
}
