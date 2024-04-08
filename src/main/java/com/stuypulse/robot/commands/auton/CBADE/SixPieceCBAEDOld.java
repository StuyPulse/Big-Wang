package com.stuypulse.robot.commands.auton.CBADE;

import com.pathplanner.lib.path.PathPlannerPath;
import com.stuypulse.robot.commands.auton.FollowPathAlignAndShoot;
import com.stuypulse.robot.commands.auton.FollowPathAndIntake;
import com.stuypulse.robot.commands.conveyor.ConveyorShootRoutine;
import com.stuypulse.robot.commands.shooter.ShooterPodiumShot;
import com.stuypulse.robot.commands.swerve.SwerveDriveToPose;
import com.stuypulse.robot.commands.swerve.SwerveDriveToShoot;
import com.stuypulse.robot.constants.Settings.Auton;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

/**
 * Shoots second shot at podium, shoots first shot between C and B
 */
public class SixPieceCBAEDOld extends SequentialCommandGroup {

    public SixPieceCBAEDOld(PathPlannerPath... paths) {
        addCommands(
            new ParallelCommandGroup(
                new WaitCommand(Auton.SHOOTER_STARTUP_DELAY)
                    .andThen(new ShooterPodiumShot()),
                
                SwerveDriveToPose.speakerRelative(-15)
                    .withTolerance(0.1, 0.1, 3)
            ),

            new ConveyorShootRoutine(),

            new FollowPathAndIntake(paths[0]),
            new SwerveDriveToShoot(),
            new ConveyorShootRoutine(),

            new FollowPathAndIntake(paths[1]),
            new SwerveDriveToShoot(),
            new ConveyorShootRoutine(),

            new FollowPathAndIntake(paths[2]),
            new SwerveDriveToShoot(),
            new ConveyorShootRoutine(),

            new FollowPathAndIntake(paths[3]),
            new FollowPathAlignAndShoot(paths[4], new SwerveDriveToShoot()),
            
            new FollowPathAndIntake(paths[5]),
            new FollowPathAlignAndShoot(paths[6], new SwerveDriveToShoot())
        );
    }
    
}
