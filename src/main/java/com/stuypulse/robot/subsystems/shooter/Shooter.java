package com.stuypulse.robot.subsystems.shooter;

import com.stuypulse.robot.constants.Settings.Shooter.Feedforward;
import com.stuypulse.robot.constants.Settings.Shooter.PID;
import com.stuypulse.stuylib.control.Controller;
import com.stuypulse.stuylib.control.feedforward.MotorFeedforward;
import com.stuypulse.stuylib.network.SmartNumber;
import com.stuypulse.stuylib.control.feedback.PIDController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
/* 3 motors (2 side motors, 1 placer motor)
 * 3 encoders 
 * IR sensor 
 * 
 * variables: 
 * leftMotor
 * rightMotor
 * 
 * 
 * leftEncoder
 * rightEncoder
 * 
 * 
 * leftTargetRPM
 * rightTargetRPM
 * 
 * 
 * leftController
 * rightController * 
 * functions: 
 * if IR sensor detects note: stop placer motor, then run again 
 * 
 * commands: 
 * run
 * stop 
 * 
 */
public abstract class Shooter extends SubsystemBase {
    protected SmartNumber leftTargetRPM;
    protected SmartNumber rightTargetRPM;

    protected Controller leftController;
    protected Controller rightController;
    
    public Shooter() {
        leftTargetRPM = new SmartNumber("Shooter/leftTargetRPM", 0);
        rightTargetRPM = new SmartNumber("Shooter/rightTargetRPM", 0);
        leftController = new MotorFeedforward(Feedforward.kS.getAsDouble(), Feedforward.kV.getAsDouble(), Feedforward.kA.getAsDouble()).velocity()
                        .add(new PIDController(PID.kP.getAsDouble(), PID.kI.getAsDouble(), PID.kD.getAsDouble()));
        rightController = new MotorFeedforward(Feedforward.kS.getAsDouble(), Feedforward.kV.getAsDouble(), Feedforward.kA.getAsDouble()).velocity()
                        .add(new PIDController(PID.kP.getAsDouble(), PID.kI.getAsDouble(), PID.kD.getAsDouble()));
        //controllers here 
    }    

    //abstract methods: stop, getLeftRPM,... 
    //methods: getLeftTargetRPM, getRightTargetRPM, ... setLeftTargetRPM....

    public double getLeftTargetRPM() {
        return leftTargetRPM.getAsDouble();
    }   

    public double getRightTargetRPM() {
        return rightTargetRPM.getAsDouble();
    } 
    
    public void setLeftTargetRPM(Number leftTargetRPM){
        this.leftTargetRPM.set(leftTargetRPM);
    }
    
    public void setRightTargetRPM(Number rightTargetRPM){
        this.rightTargetRPM.set(rightTargetRPM);
    }

    public abstract void stop();
    public abstract double getLeftShooterRPM();
    public abstract double getRightShooterRPM();

    protected abstract void setLeftMotorVoltageImpl(double voltage);
    protected abstract void setRightMotorVoltageImpl(double voltage);

    @Override
    public void periodic() {
        leftController.update(getLeftTargetRPM(), getLeftShooterRPM());
        rightController.update(getRightTargetRPM(), getRightShooterRPM());
        setLeftMotorVoltageImpl(leftController.getOutput());
        setRightMotorVoltageImpl(rightController.getOutput());

        periodicallyCalled();
    }

    public void periodicallyCalled() {}
}