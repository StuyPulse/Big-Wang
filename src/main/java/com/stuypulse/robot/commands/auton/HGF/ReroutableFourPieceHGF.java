package com.stuypulse.robot.commands.auton.HGF;

import com.pathplanner.lib.path.PathPlannerPath;
import com.stuypulse.robot.commands.FastAlignShootSpeakerRelative;
import com.stuypulse.robot.commands.auton.FollowPathAlignAndShootFast;
import com.stuypulse.robot.commands.auton.FollowPathAndIntake;
import com.stuypulse.robot.commands.conveyor.ConveyorShootRoutine;
import com.stuypulse.robot.commands.shooter.ShooterPodiumShot;
import com.stuypulse.robot.commands.shooter.ShooterWaitForTarget;
import com.stuypulse.robot.commands.swerve.SwerveDriveToPose;
import com.stuypulse.robot.subsystems.intake.Intake;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class ReroutableFourPieceHGF extends SequentialCommandGroup {



    public ReroutableFourPieceHGF(PathPlannerPath... paths) {
        addCommands(
            new ParallelCommandGroup(
                new WaitCommand(0.25)
                    .andThen(new ShooterPodiumShot()),

                SwerveDriveToPose.speakerRelative(-55)
                    .withTolerance(0.1, 0.1, 2)
            ),

            new ShooterWaitForTarget(),
            ConveyorShootRoutine.untilNoteShot(0.75),

            new FollowPathAndIntake(paths[0]),
            new ConditionalCommand(
                new SequentialCommandGroup(
                    new FollowPathAlignAndShootFast(paths[1], new FastAlignShootSpeakerRelative(-45, 1.0)),
                    new FollowPathAndIntake(paths[2]),
                    new ConditionalCommand(
                        new SequentialCommandGroup(
                            new FollowPathAlignAndShootFast(paths[3], new FastAlignShootSpeakerRelative(-45)),
                            new FollowPathAndIntake(paths[4]),
                            new FollowPathAlignAndShootFast(paths[5], SwerveDriveToPose.speakerRelative(-45))),
                        new SequentialCommandGroup(
                            new FollowPathAndIntake(paths[7]),
                            new FollowPathAlignAndShootFast(paths[5], SwerveDriveToPose.speakerRelative(-45))),
                        Intake.getInstance()::hasNote)),
                new SequentialCommandGroup(
                    new FollowPathAndIntake(paths[6]),
                    new ConditionalCommand(
                        new SequentialCommandGroup(
                            new FollowPathAlignAndShootFast(paths[3], new FastAlignShootSpeakerRelative(-45)),
                            new FollowPathAndIntake(paths[4]),
                            new FollowPathAlignAndShootFast(paths[5], SwerveDriveToPose.speakerRelative(-45))),
                        new SequentialCommandGroup(
                            new FollowPathAndIntake(paths[7]),
                            new FollowPathAlignAndShootFast(paths[5], SwerveDriveToPose.speakerRelative(-45))),
                        Intake.getInstance()::hasNote)),
                Intake.getInstance()::hasNote)
        );
    }

}