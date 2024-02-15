/************************ PROJECT IZZI *************************/
/* Copyright (c) 2024 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/

package com.stuypulse.robot.commands.notealignment.robotrelative;

import static com.stuypulse.robot.constants.Settings.Alignment.*;
import static com.stuypulse.robot.constants.Settings.NoteDetection.*;

import com.stuypulse.stuylib.control.angle.feedback.AnglePIDController;
import com.stuypulse.stuylib.control.feedback.PIDController;
import com.stuypulse.stuylib.streams.booleans.BStream;
import com.stuypulse.stuylib.streams.booleans.filters.BDebounceRC;

import com.stuypulse.robot.constants.Settings.Swerve;
import com.stuypulse.robot.subsystems.odometry.Odometry;
import com.stuypulse.robot.subsystems.swerve.SwerveDrive;
import com.stuypulse.robot.subsystems.vision.NoteVision;
import com.stuypulse.robot.util.HolonomicController;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class SwerveDriveTranslateToNote extends Command {

    private final SwerveDrive swerve;
    private final NoteVision vision;
    private final Odometry odometry;

    private final HolonomicController controller;
    private final BStream aligned;

    public SwerveDriveTranslateToNote() {
        this.swerve = SwerveDrive.getInstance();
        this.vision = NoteVision.getInstance();
        odometry = Odometry.getInstance();

        controller = new HolonomicController(
            new PIDController(Translation.kP, Translation.kI, Translation.kD),
            new PIDController(Translation.kP, Translation.kI, Translation.kD),
            new AnglePIDController(Rotation.kP, Rotation.kI, Rotation.kD));

        SmartDashboard.putData("Note Detection/Controller", controller);

        aligned = BStream.create(this::isAligned).filtered(new BDebounceRC.Rising(DEBOUNCE_TIME));

        addRequirements(swerve);
    }

    private boolean isAligned() {
        return controller.isDone(THRESHOLD_X.get(), THRESHOLD_Y.get(), THRESHOLD_ANGLE.get());
    }

    @Override
    public void execute() {
        Translation2d robotToNote = vision.getRobotRelativeNotePose();
        Rotation2d kZero = new Rotation2d();
        Pose2d targetPose = new Pose2d(
            new Translation2d(Swerve.CENTER_TO_INTAKE_FRONT, 0)
                .rotateBy(odometry.getPose().getRotation()),
            kZero);

        // translate to note only if note in view
        if (vision.hasNoteData()) {
            swerve.setChassisSpeeds(controller.update(targetPose, new Pose2d(robotToNote, kZero)));
        }

        SmartDashboard.putBoolean("Note Detection/Is Aligned", aligned.get());
    }

    @Override
    public boolean isFinished() {
        return aligned.get();
    }
}
