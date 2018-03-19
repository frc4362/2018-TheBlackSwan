package frc.team4362.util.joy;

import java.util.function.Function;

import frc.team4362.util.func.FunctionPipeline;

import edu.wpi.first.wpilibj.Joystick;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Gemstick extends Joystick {
	private static int instances = 0;
	
	private static final int FRAME_LENGTH = 50;

	public enum StickLens {
		X(JoystickFrame::getX), 
		Y(JoystickFrame::getY), 
		Z(JoystickFrame::getZ), 
		MAGNITUDE(JoystickFrame::getMagnitude), 
		AZIMUTH(JoystickFrame::getAzimuth);

		private final Function<JoystickFrame, Double> m_getter;

		StickLens(final Function<JoystickFrame, Double> getter) {
			m_getter = getter;
		}

		public double apply(final JoystickFrame frame) {
			return m_getter.apply(frame);
		}
	}

	public enum POVState {
		NONE(-1), 
		N(0), 
		NE(1), 
		E(2), 
		SE(3), 
		S(4), 
		SW(5), 
		W(6), 
		NW(7);

		private final int m_val;

		POVState(final int val) {
			m_val = val;
		}

		public int getValue() {
			return m_val;
		}

		public static POVState of(final int val) {
			switch (val) {
			case -1: return POVState.NONE;
			case 0: return POVState.N;
			case 1: return POVState.NE;
			case 2: return POVState.E;
			case 3: return POVState.SE;
			case 4: return POVState.S;
			case 5: return POVState.SW;
			case 6: return POVState.W;
			case 7: return POVState.NW;
			default: 
				throw new RuntimeException("Invalid POVState state!");
			}
		}
	}

	@SuppressWarnings("unused")
	private static class Funcs {
		private static final double TWO_THIRDS = 2.0 / 3.0;

		/**
		 * [1.0 - range, 1.0] -> [0.0, 1.0]
		 */
		private static double scale(final double input, final double range) {
			return (1.0 / range) * (input - ((1.0 - range) * Math.signum(input)));
		}

		private static double deadband(final double input, final double threshold) {
			return Math.abs(input) > threshold ? scale(input, 1 - threshold) : 0;
		}

		/**
		 * Simple rectangle deadband
		 */
		public static Function<JoystickFrame, JoystickFrame> makeRectangleDeadband(final double limitX, final double limitY) {
			return (stick) -> {
				final double x = stick.getX(), 
						     y = stick.getY();

				return new JoystickFrame(
						Math.abs(x) > limitX ? scale(x, 1 - limitX) : 0,
						Math.abs(y) > limitY ? scale(y, 1 - limitY) : 0,
						stick.getZ()
				);
			};
		}

		// TODO: MAKE WORK
		/**
		 * Deadbands the joystick in a circle
		 * @param radius 0-sqrt(2)
		 */
		public static Function<JoystickFrame, JoystickFrame> makeRadialDeadband(final double radius) {
			return (stick) -> {
				final double scaledRadius = radius / Math.sqrt(2.0); // this is from 0-1, not 0-sqrt(2)
				final boolean valid = radius < stick.getMagnitude();

				return new JoystickFrame(
						valid ? stick.getX() : 0,
						valid ? stick.getY() : 0,
						stick.getZ()
				);
			};
		}

		/**
		 * Deadbands the joystick in the shape of a 2/3 degree superellipse, otherwise known as a squashed astroid
		 * The logic behind this is that 
		 * 	1. The controller is unlikely to come to a rest offset from the origin in two axis of movement
		 *  2. Movement in more than one axis is unlikely to be accidental. 
		 *  3. Therefore, an astroid deadzone is very efficient as it will protect against slight default position errors,
		 *  	and will still allow for quick multiaxial movement.
		 * @param limit Approximately the total width of the astroid. 
		 */
		public static Function<JoystickFrame, JoystickFrame> makeAstroidDeadband(final double limit) {
			return (stick) -> {
				final double value = 
						Math.pow(Math.abs(stick.getX()), TWO_THIRDS) 
						+ Math.pow(Math.abs(stick.getY()), TWO_THIRDS);

				final boolean valid = limit < value;

				return new JoystickFrame(
						valid ? stick.getX() : 0,
						valid ? stick.getY() : 0,
						stick.getZ()
				);
			};
		}

		/**
		 * Deadbands the joystick in the shape of a square rotated 45 degrees
		 * @param diagonal The length of the diagonal of the created square
		 */
		public static Function<JoystickFrame, JoystickFrame> makeDiamondDeadband(final double diagonal) {
			return (stick) -> {
				final double x = stick.getX(),
						     y = stick.getY();

				final boolean valid = (diagonal / 2) < (Math.sqrt(Math.abs(x) + Math.abs(y)));

				return new JoystickFrame(
						valid ? stick.getX() : 0,
						valid ? stick.getY() : 0,
						stick.getZ()
				);
			};
		}
		
		/**
		 * Deadband the joystick twist
		 * @param threshold The minimum proportion of rotation to get a value
		 */
		public static Function<JoystickFrame, JoystickFrame> makeZDeadband(final double threshold) {
			return (stick) -> new JoystickFrame(
					stick.getX(),
					stick.getY(),
					deadband(stick.getZ(), threshold)
			);
		}

		public static Function<JoystickFrame, JoystickFrame> makeInverts(
				final boolean x,
				final boolean y,
				final boolean z
		) {
			return (stick) -> new JoystickFrame(
					stick.getX() * (x ? -1 : 1),
					stick.getY() * (y ? -1 : 1),
					stick.getZ() * (z ? -1 : 1)
			);
		}
	}

	protected FunctionPipeline<JoystickFrame> m_pipeline;

	protected final String m_name;
	
	private JoystickFrame m_lastFrame;

	public Gemstick(final String name, final int port) {
		super(port);

		m_name = name;

		m_pipeline = new FunctionPipeline<>(
				Funcs.makeInverts(false, true, false),
				Funcs.makeRectangleDeadband(0.12, 0.12),
				Funcs.makeZDeadband(0.08)
		);

		m_lastFrame = new JoystickFrame(this);

		instances++;
	}

	public Gemstick(final int port) {
		this("Jostick " + String.format("%02d", instances), port);
	}

	public POVState getPOVState() {
		final int s = super.getPOV();
		
		if (s == -1) {
			return POVState.NONE;
		} else {
			return POVState.of(s / 45);
		}
	}

	public HIDType getType() {
		return HIDType.kHIDJoystick;
	}

	public final String getName() {
		return m_name;
	}

	private boolean isLastFrameExpired() {
		return System.currentTimeMillis() - m_lastFrame.getTime() > FRAME_LENGTH;
	}
	
	public JoystickFrame getRawFrame() {
		if (isLastFrameExpired()) {
			m_lastFrame = new JoystickFrame(this);
		}
		
		return m_lastFrame;
	}

	public JoystickFrame getFrame() {
		return m_pipeline.apply(getRawFrame());
	}

	public double get(final StickLens lens) {
		return lens.apply(getFrame());
	}
}
