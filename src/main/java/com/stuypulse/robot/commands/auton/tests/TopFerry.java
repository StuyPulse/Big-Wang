package com.stuypulse.robot.commands.auton.tests;

import com.pathplanner.lib.path.PathPlannerPath;
import com.stuypulse.robot.commands.auton.FollowPathAlignAndShoot;
import com.stuypulse.robot.commands.auton.FollowPathAndIntake;
import com.stuypulse.robot.commands.auton.FollowPathFerryIntake;
import com.stuypulse.robot.commands.conveyor.ConveyorShootRoutine;
import com.stuypulse.robot.commands.shooter.ShooterPodiumShot;
import com.stuypulse.robot.commands.swerve.SwerveDriveToPose;
import com.stuypulse.robot.commands.swerve.SwerveDriveToShoot;
import com.stuypulse.robot.constants.Settings.Auton;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TopFerry extends SequentialCommandGroup {
    public TopFerry(PathPlannerPath... paths) {
        addCommands (
            new ParallelCommandGroup(
                new WaitCommand(Auton.SHOOTER_STARTUP_DELAY)
                    .andThen(new ShooterPodiumShot()),
                
                SwerveDriveToPose.speakerRelative(45)
            ),

            new ConveyorShootRoutine(),

            // intake D
            new FollowPathAndIntake(paths[0]),

            // shoot D, intake E
            new FollowPathFerryIntake(paths[1], 0.25),

            // shoot E, intake F
            new FollowPathFerryIntake(paths[2], 0.25),

            // shoot F
            new FollowPathAlignAndShoot(paths[3], new SwerveDriveToShoot())
        );
    }
}
