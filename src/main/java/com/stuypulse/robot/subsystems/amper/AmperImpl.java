package com.stuypulse.robot.subsystems.amper;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.stuypulse.robot.constants.Motors;
import com.stuypulse.robot.constants.Ports;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.constants.Settings.Amper.Lift;
import com.stuypulse.stuylib.control.Controller;
import com.stuypulse.stuylib.control.feedback.PIDController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class AmperImpl extends Amper {
    
    private final CANSparkMax scoreMotor;
    private final CANSparkMax liftMotor;
    private final RelativeEncoder liftEncoder;

    private final DigitalInput alignedSwitch;
    private final DigitalInput minSwitch;
    private final DigitalInput ampIRSensor;

    public final Controller liftController;

    public AmperImpl() {
        liftController = new PIDController(Lift.PID.kP, Lift.PID.kI, Lift.PID.kD);

        scoreMotor = new CANSparkMax(Ports.Amper.SCORE, MotorType.kBrushless);
        liftMotor = new CANSparkMax(Ports.Amper.LIFT, MotorType.kBrushless);
        liftEncoder = liftMotor.getEncoder();

        liftEncoder.setPositionConversionFactor(Settings.Amper.Lift.Encoder.POSITION_CONVERSION);
        liftEncoder.setVelocityConversionFactor(Settings.Amper.Lift.Encoder.VELOCITY_CONVERSION);

        alignedSwitch = new DigitalInput(Ports.Amper.ALIGNED_BUMP_SWITCH);
        minSwitch = new DigitalInput(Ports.Amper.LIFT_BOTTOM_LIMIT);
        ampIRSensor = new DigitalInput(Ports.Amper.AMP_IR);

        Motors.Amper.LIFT_MOTOR.configure(liftMotor);
        Motors.Amper.SCORE_MOTOR.configure(scoreMotor);
    }

    @Override
    public boolean hasNote() {
        return !ampIRSensor.get();
    }

    @Override
    public boolean liftAtBottom() {
        return !minSwitch.get();
    }

    @Override
    public double getLiftHeight() {
        return liftEncoder.getPosition();
    }

    @Override
    public boolean touchingAmp() {
        return !alignedSwitch.get();
    }

    @Override
    public void score() {
        scoreMotor.set(Settings.Amper.Score.ROLLER_SPEED.get());
    }

    @Override
    public void intake() {
        scoreMotor.set(-Settings.Amper.Score.ROLLER_SPEED.get());
    }

    @Override
    public void stopLift() {
        liftMotor.stopMotor();
    }

    @Override
    public void stopRoller() {
        scoreMotor.stopMotor();
    }

    @Override
    public void periodic() {
        super.periodic();

        liftController.update(targetHeight.get(), getLiftHeight());

        if (liftAtBottom() && liftController.getOutput() < 0) {
            stopLift();
        } else {
            liftMotor.setVoltage(liftController.getOutput());
        }

        SmartDashboard.putNumber("Amper/Intake Speed", scoreMotor.get());
        SmartDashboard.putNumber("Amper/Lift Speed", liftMotor.get());
        SmartDashboard.putNumber("Amper/Intake Current", scoreMotor.getOutputCurrent());
        SmartDashboard.putNumber("Amper/Lift Current", liftMotor.getOutputCurrent());
        SmartDashboard.putNumber("Amper/Lift Height", getLiftHeight());
    }

}
