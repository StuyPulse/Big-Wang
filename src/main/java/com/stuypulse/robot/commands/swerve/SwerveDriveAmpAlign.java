package com.stuypulse.robot.commands.swerve;

import com.stuypulse.robot.Robot;
import com.stuypulse.robot.constants.Field;
import com.stuypulse.robot.constants.Settings.Alignment;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class SwerveDriveAmpAlign extends SequentialCommandGroup {

    private static final double AMP_WALL_SETUP_X_DISTANCE = Units.inchesToMeters(2.0);
    private static final double AMP_WALL_SETUP_Y_DISTANCE = Units.inchesToMeters(4.0);
    private static final double AMP_WALL_SETUP_DEGREE = 5;

    private static final double AMP_WALL_SCORE_X_DISTANCE = Units.inchesToMeters(0.75);
    private static final double AMP_WALL_SCORE_Y_DISTANCE = Units.inchesToMeters(1.0);
    private static final double AMP_WALL_SCORE_DEGREE = 2;

    private static Pose2d getTargetPose(double distanceToWall) {
        Translation2d amp = Field.getAllianceAmpPose().getTranslation();
        Translation2d delta = new Translation2d(0, Robot.isBlue() ? -distanceToWall : distanceToWall);
        Rotation2d targetAngle = Rotation2d.fromDegrees(Robot.isBlue() ? 270 : 90);

        return new Pose2d(amp.plus(delta), targetAngle);
    }

    public SwerveDriveAmpAlign() {
        addCommands(
            new SwerveDriveToPose(() -> getTargetPose(Alignment.AMP_WALL_SETUP_DISTANCE.get()))
                .withTolerance(AMP_WALL_SETUP_X_DISTANCE, AMP_WALL_SETUP_Y_DISTANCE, AMP_WALL_SETUP_DEGREE),
            
            new SwerveDriveToPose(() -> getTargetPose(Alignment.AMP_WALL_SCORE_DISTANCE.get()))
                .withTolerance(AMP_WALL_SCORE_X_DISTANCE, AMP_WALL_SCORE_Y_DISTANCE, AMP_WALL_SCORE_DEGREE)
        );
    }
    
}
