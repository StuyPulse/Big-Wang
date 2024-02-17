/************************ PROJECT IZZI *************************/
/* Copyright (c) 2024 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/

package com.stuypulse.robot.subsystems.amper;

import com.stuypulse.stuylib.math.SLMath;
import com.stuypulse.stuylib.network.SmartNumber;

import com.stuypulse.robot.Robot;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.constants.Settings.Amper.Lift;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/*
AMP:
1 motor
1 limit switch
IR Sensor

LIFT:
1 motor
1 encoder
Bottom (shooter) limit switch
*/

public abstract class Amper extends SubsystemBase {

    private static final Amper instance;

    static {
        if (Robot.isReal()) {
            instance = new AmperImpl();
        } else {
            instance = new AmperSim();
        }
    }

    public static Amper getInstance() {
        return instance;
    }

    private final SmartNumber targetHeight;

    private final Mechanism2d mechanism2d;
    private final MechanismLigament2d lift2d;

    public Amper() {
        targetHeight = new SmartNumber("Amper/Target Height", 0); // TODO: determine the default value

        mechanism2d = new Mechanism2d(3, 3);
        mechanism2d.getRoot("Base Origin", 1, 1).append(new MechanismLigament2d(
            "Base",
            1,
            0,
            10,
            new Color8Bit(Color.kOrange)));

        lift2d = mechanism2d.getRoot("Lift Origin", 1.5, 1).append(new MechanismLigament2d(
            "Lift",
            Settings.Amper.Lift.VISUALIZATION_MIN_LENGTH,
            Settings.Amper.Lift.ANGLE_TO_GROUND.getDegrees(),
            10,
            new Color8Bit(Color.kAqua)));

        SmartDashboard.putData("Lift Mechanism", mechanism2d);
    }

    /*** LIFT CONTROL ***/

    public void setTargetHeight(double height) {
        targetHeight.set(
                SLMath.clamp(
                        height, Settings.Amper.Lift.MIN_HEIGHT, Settings.Amper.Lift.MAX_HEIGHT));
    }

    public final double getTargetHeight() {
        return targetHeight.get();
    }

    public final boolean isAtTargetHeight(double epsilonMeters) {
        return Math.abs(getTargetHeight() - getLiftHeight()) < epsilonMeters;
    }

    public abstract boolean liftAtBottom();

    public abstract boolean liftAtTop();

    public abstract double getLiftHeight();

    public abstract void stopLift();

    /*** IR SENSOR ***/

    public abstract boolean hasNote();

    /*** SCORE ROLLERS ***/

    public abstract void score();

    public abstract void intake();

    public abstract void stopRoller();

    public abstract double getNoteDistance();

    // public abstract boolean touchingAmp();

    /*** LIFT CONFIG ***/

    public abstract void setVoltageOverride(double voltage);

    public abstract void setConstraints(double maxVelocity, double maxAcceleration);

    public final void resetConstraints() {
        setConstraints(Lift.VEL_LIMIT, Lift.ACCEL_LIMIT);
    }

    @Override
    public void periodic() {
        lift2d.setLength(Settings.Amper.Lift.VISUALIZATION_MIN_LENGTH + getLiftHeight());
    }
}
