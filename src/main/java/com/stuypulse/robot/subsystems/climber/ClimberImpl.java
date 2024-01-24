package com.stuypulse.robot.subsystems.climber;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.stuypulse.robot.constants.Motors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// import static com.stuypulse.robot.constants.Settings.Climber.Encoder.*;
// import static com.stuypulse.robot.constants.Settings.Climber.*;
// import static com.stuypulse.robot.constants.Ports.Climber.*;


public class ClimberImpl extends Climber {
    private final CANSparkMax rightMotor;
    private final CANSparkMax leftMotor;

    private final RelativeEncoder rightEncoder;
    private final RelativeEncoder leftEncoder;

    private final DigitalInput topRightLimit;
    private final DigitalInput topLeftLimit;
    private final DigitalInput bottomRightLimit;
    private final DigitalInput bottomLeftLimit;

    protected ClimberImpl() {
        rightMotor = new CANSparkMax(RIGHT_MOTOR, MotorType.kBrushless);
        leftMotor = new CANSparkMax(LEFT_MOTOR, MotorType.kBrushless);

        rightEncoder = rightMotor.getEncoder();
        leftEncoder = leftMotor.getEncoder();

        rightEncoder.setPositionConversionFactor(ENCODER_MULTIPLIER);
        leftEncoder.setPositionConversionFactor(ENCODER_MULTIPLIER);

        topRightLimit = new DigitalInput(TOP_RIGHT_LIMIT);
        topLeftLimit = new DigitalInput(TOP_LEFT_LIMIT);
        bottomRightLimit = new DigitalInput(BOTTOM_RIGHT_LIMIT);
        bottomLeftLimit = new DigitalInput(BOTTOM_LEFT_LIMIT);

        Motors.Climber.LEFT_MOTOR.configure(leftMotor);
        Motors.Climber.RIGHT_MOTOR.configure(rightMotor);
    }

	@Override
	public double getHeight() {
        return (leftEncoder.getPosition() + rightEncoder.getPosition()) / 2;
    }

    @Override
    public double getVelocity() {
        return (leftEncoder.getVelocity() + rightEncoder.getVelocity()) / 2;
    }

    @Override
    public void setVoltage(double voltage) {
        if (atTop() && voltage > 0) {
            DriverStation.reportWarning("Climber Top Limit Reached", false);
            voltage = 0.0;
        } else if (atBottom() && voltage < 0) {
            DriverStation.reportWarning("Climber Bottom Limit Reached", false);
            voltage = 0.0;
        }

        rightMotor.setVoltage(voltage);
        leftMotor.setVoltage(voltage);
    }

    @Override
    public boolean atTop() {
        return !topRightLimit.get() || !topLeftLimit.get();
    }

    @Override
    public boolean atBottom() {
        return !bottomRightLimit.get() || !bottomLeftLimit.get();
    }

	@Override
	public void periodicallyCalled() {
        SmartDashboard.putNumber("Climber/Target Height", getTargetHeight());
        SmartDashboard.putNumber("Climber/Height", getHeight());
        
        SmartDashboard.putNumber("Climber/Velocity", getVelocity());

        if (atBottom()) {
            leftEncoder.setPosition(MIN_HEIGHT);
            rightEncoder.setPosition(MIN_HEIGHT);
        }
	}
}