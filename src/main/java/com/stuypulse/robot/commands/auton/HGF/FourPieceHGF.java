package com.stuypulse.robot.commands.auton.HGF;

import com.pathplanner.lib.path.PathPlannerPath;
import com.stuypulse.robot.commands.auton.FollowPathAlignAndShoot;
import com.stuypulse.robot.commands.auton.FollowPathAndIntake;
import com.stuypulse.robot.commands.intake.IntakeShootRoutine;
import com.stuypulse.robot.commands.shooter.ShooterPodiumShot;
import com.stuypulse.robot.commands.shooter.ShooterStop;
import com.stuypulse.robot.commands.shooter.ShooterWaitUntilReady;
import com.stuypulse.robot.commands.swerve.SwerveDriveToPose;
import com.stuypulse.robot.commands.swerve.SwerveDriveToShoot;
import com.stuypulse.robot.constants.Settings.Auton;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class FourPieceHGF extends SequentialCommandGroup {

    public FourPieceHGF(PathPlannerPath... paths) {
        addCommands(
            new ParallelCommandGroup(
                new WaitCommand(Auton.SHOOTER_STARTUP_DELAY)
                    .andThen(new ShooterPodiumShot()),

                SwerveDriveToPose.speakerRelative(-45)
                    .withTolerance(0.1, 0.1, 2)
            ),

            new ShooterWaitUntilReady(),
            new IntakeShootRoutine(),
            new ShooterStop(),

            new FollowPathAndIntake(paths[0]),
            new FollowPathAlignAndShoot(paths[1], new SwerveDriveToShoot()),
            new FollowPathAndIntake(paths[2]),
            new FollowPathAlignAndShoot(paths[3], new SwerveDriveToShoot()),
            new FollowPathAndIntake(paths[4]),
            new FollowPathAlignAndShoot(paths[5], new SwerveDriveToShoot())
        );
    }

}
