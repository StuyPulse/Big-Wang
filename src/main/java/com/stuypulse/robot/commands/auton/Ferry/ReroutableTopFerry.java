package com.stuypulse.robot.commands.auton.Ferry;

import com.pathplanner.lib.path.PathPlannerPath;
import com.stuypulse.robot.commands.auton.FollowPathAlignAndShoot;
import com.stuypulse.robot.commands.auton.FollowPathAndIntake;
import com.stuypulse.robot.commands.conveyor.ConveyorShootRoutine;
import com.stuypulse.robot.commands.shooter.ShooterPodiumShot;
import com.stuypulse.robot.commands.shooter.ShooterSetRPM;
import com.stuypulse.robot.commands.swerve.SwerveDriveResetOdometry;
import com.stuypulse.robot.commands.swerve.SwerveDriveStop;
import com.stuypulse.robot.commands.swerve.SwerveDriveToPose;
import com.stuypulse.robot.commands.swerve.SwerveDriveToShoot;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.constants.Settings.Auton;
import com.stuypulse.robot.subsystems.intake.Intake;
import com.stuypulse.robot.subsystems.swerve.SwerveDrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ReroutableTopFerry extends SequentialCommandGroup {

    private Pose2d getPathStartPose(PathPlannerPath path) {
        return path.getPreviewStartingHolonomicPose();
    }

    public ReroutableTopFerry(PathPlannerPath... paths) {

        PathPlannerPath D_TO_E = paths[6];
        PathPlannerPath E_TO_F = paths[7];
        PathPlannerPath E_FERRY_REROUTE = paths[8];

        Command INTAKE_E = new FollowPathAndIntake(paths[2]);

        addCommands (
            new ParallelCommandGroup(
                new WaitCommand(Auton.SHOOTER_STARTUP_DELAY)
                    .andThen(new ShooterPodiumShot()),
                
                SwerveDriveToPose.speakerRelative(42.5)
                    .withTolerance(0.03, 0.03, 3)
            ),

            new ConveyorShootRoutine(0.65),

            new ShooterSetRPM(Settings.Shooter.WING_FERRY),

            // intake D
            new FollowPathAndIntake(paths[0]),

            new ConditionalCommand(
                // if we have a note, we can shoot D
                new SequentialCommandGroup(

                    // shoot D, intake E
                    SwerveDrive.getInstance().followPathCommand(paths[1]),
                    new SwerveDriveStop(),
                    new ConveyorShootRoutine(.75),
                    INTAKE_E,

                    new ConditionalCommand(
                        // if we have a note, we can shoot E
                        new SequentialCommandGroup(

                            // shoot E, intake F
                            SwerveDrive.getInstance().followPathCommand(paths[3]),
                            new SwerveDriveStop(),
                            new ConveyorShootRoutine(.75),
                            new ShooterPodiumShot(),

                            new SwerveDriveResetOdometry(() -> getPathStartPose(paths[4])),
                            new FollowPathAndIntake(paths[4]),

                            new ConditionalCommand(
                                // if we have a note, we can shoot F
                                new FollowPathAlignAndShoot(paths[5], new SwerveDriveToShoot()), 
                                new InstantCommand(), 
                                Intake.getInstance()::hasNote)
                        ), 
                        // else intake F
                        new SequentialCommandGroup(
                            new FollowPathAndIntake(E_TO_F),
                            
                            new ConditionalCommand(
                                // if we have a note, we can shoot F
                                new FollowPathAlignAndShoot(paths[5], new SwerveDriveToShoot()), 
                                new InstantCommand(), 
                                Intake.getInstance()::hasNote)
                        ), 
                        Intake.getInstance()::hasNote)
                ), 
                // don't shoot D, intake E
                new SequentialCommandGroup(
                    new FollowPathAndIntake(D_TO_E),

                    new ConditionalCommand(
                        new SequentialCommandGroup(
                            // shoot E, intake F
                            SwerveDrive.getInstance().followPathCommand(E_FERRY_REROUTE),
                            new SwerveDriveStop(),
                            new ConveyorShootRoutine(.75),
                            new ShooterPodiumShot(),
                            
                            new SwerveDriveResetOdometry(() -> getPathStartPose(paths[4])),
                            new FollowPathAndIntake(paths[4]),
                            
                            new ConditionalCommand(
                                // if we have a note, we can shoot F
                                new FollowPathAlignAndShoot(paths[5], new SwerveDriveToShoot()), 
                                new InstantCommand(), 
                                Intake.getInstance()::hasNote)
                        ), 
                        new SequentialCommandGroup(
                            new FollowPathAndIntake(E_TO_F),
                            
                            new ConditionalCommand(
                                // if we have a note, we can shoot F
                                new FollowPathAlignAndShoot(paths[5], new SwerveDriveToShoot()), 
                                new InstantCommand(), 
                                Intake.getInstance()::hasNote)
                        ), 
                        Intake.getInstance()::hasNote)
                ), 
                Intake.getInstance()::hasNote)
        );
    }
}
