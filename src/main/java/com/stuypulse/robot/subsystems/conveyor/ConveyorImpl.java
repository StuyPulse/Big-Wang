package com.stuypulse.robot.subsystems.conveyor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.stuypulse.robot.constants.Motors;
import com.stuypulse.robot.constants.Ports;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.stuylib.streams.booleans.BStream;
import com.stuypulse.stuylib.streams.booleans.filters.BDebounce;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class ConveyorImpl extends Conveyor {
    
    private final CANSparkMax gandalfMotor;
    private final CANSparkMax shooterFeederMotor;
    private final DigitalInput irSensor;

    private BStream isAtShooter;

    protected ConveyorImpl() {
        gandalfMotor = new CANSparkMax(Ports.Conveyor.GANDALF_MOTOR_PORT, MotorType.kBrushless);
        shooterFeederMotor = new CANSparkMax(Ports.Conveyor.SHOOTER_FEEDER_MOTOR_PORT, MotorType.kBrushless);

        irSensor = new DigitalInput(Ports.Conveyor.IR_SENSOR_PORT);

        Motors.Conveyor.GANDALF_MOTOR.configure(gandalfMotor);
        Motors.Conveyor.SHOOTER_FEEDER_MOTOR.configure(shooterFeederMotor);

        isAtShooter = 
            BStream.create(() -> !irSensor.get())
                .filtered(new BDebounce.Both(Settings.Conveyor.DEBOUNCE_TIME));

        
    }

    @Override
    public boolean isNoteAtShooter() {
        return isAtShooter.get();
    }

    @Override
    public void toShooter() {
        gandalfMotor.set(Settings.Conveyor.GANDALF_SHOOTER_SPEED.get());
        shooterFeederMotor.set(Settings.Conveyor.SHOOTER_FEEDER_SPEED.get());
    }

    @Override
    public void toAmp() {
        gandalfMotor.set(Settings.Conveyor.GANDALF_AMP_SPEED.get());

    }

    public void stop() {
        gandalfMotor.set(0);
        shooterFeederMotor.set(0);
    }

    @Override
    public void periodic() {

        //logging
        SmartDashboard.putNumber("Conveyor/Gandalf Motor Current", gandalfMotor.getOutputCurrent());
        SmartDashboard.putNumber("Conveyor/Shooter Feeder Motor Current", shooterFeederMotor.getOutputCurrent());

        SmartDashboard.putNumber("Conveyor/Gandalf Motor Speed", gandalfMotor.get());
        SmartDashboard.putNumber("Conveyor/Shooter Feeder Motor Spped", shooterFeederMotor.get());

    }


}