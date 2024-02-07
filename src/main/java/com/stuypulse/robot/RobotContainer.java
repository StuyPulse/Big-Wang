

/************************ PROJECT PHIL ************************/
/* Copyright (c) 2024 StuyPulse Robotics. All rights reserved.*/
/* This work is licensed under the terms of the MIT license.  */
/**************************************************************/

package com.stuypulse.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.stuypulse.robot.commands.*;
import com.stuypulse.robot.commands.amper.*;
import com.stuypulse.robot.commands.auton.*;
import com.stuypulse.robot.commands.climber.*;
import com.stuypulse.robot.commands.swerve.*;
import com.stuypulse.robot.commands.intake.*;
import com.stuypulse.robot.commands.notealignment.SwerveDriveNoteAlignedDrive;
import com.stuypulse.robot.commands.shooter.*;
import com.stuypulse.robot.commands.conveyor.*;
import com.stuypulse.robot.constants.Ports;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.subsystems.amper.Amper;
import com.stuypulse.robot.subsystems.odometry.Odometry;
import com.stuypulse.robot.subsystems.swerve.SwerveDrive;
import com.stuypulse.robot.subsystems.vision.AprilTagVision;
import com.stuypulse.robot.subsystems.vision.NoteVision;
import com.stuypulse.robot.subsystems.intake.Intake;
import com.stuypulse.robot.subsystems.shooter.Shooter;
import com.stuypulse.robot.subsystems.conveyor.Conveyor;
import com.stuypulse.stuylib.input.Gamepad;
import com.stuypulse.stuylib.input.gamepads.AutoGamepad;

import com.stuypulse.robot.subsystems.climber.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {

    // Gamepads
    public final Gamepad driver = new AutoGamepad(Ports.Gamepad.DRIVER);
    public final Gamepad operator = new AutoGamepad(Ports.Gamepad.OPERATOR);
    
    // Subsystems
    public final AprilTagVision vision = AprilTagVision.getInstance();
    public final NoteVision noteVision = NoteVision.getInstance();
    public final Odometry odometry = Odometry.getInstance();

    public final Amper amper = Amper.getInstance();
    public final Conveyor conveyor = Conveyor.getInstance();
    public final Climber climber = Climber.getInstance();
    public final Intake intake = Intake.getInstance();
    public final Shooter shooter = Shooter.getInstance();
    public final SwerveDrive swerve = SwerveDrive.getInstance();
  
    // Autons
    private static SendableChooser<Command> autonChooser;

    // RobotContainer
    public RobotContainer() {
        swerve.configureAutoBuilder();

        configureDefaultCommands();
        configureNamedCommands();
        configureButtonBindings();
        configureAutons();
    }

    /****************/
    /*** DEFAULTS ***/
    /****************/

    private void configureDefaultCommands() {
        swerve.setDefaultCommand(new SwerveDriveDrive(driver));
        intake.setDefaultCommand(new IntakeStop());
    }

    /**********************/
    /*** NAMED COMMANDS ***/
    /**********************/

    private void configureNamedCommands() {}

    /***************/
    /*** BUTTONS ***/
    /***************/

    private void configureButtonBindings() {
        configureOperatorBindings();
        configureDriverBindings();    
    }

    private void configureDriverBindings() {
        driver.getRightTriggerButton()
            .whileTrue(new IntakeAcquire())
            .whileTrue(new SwerveDriveNoteAlignedDrive(driver));
        driver.getRightButton()
            .whileTrue(new ClimberSetupRoutine());

        //TODO: what is score w IR logic and score both difference, what is align n score
        driver.getBottomButton()
            .onTrue(new ConveyorScoreNote()); /*Score w IR logic */
        driver.getTopButton()
            .whileTrue(new AmperScoreAmpRoutine()); /*align and score*/
        driver.getStartButton()
            .onTrue(new DoNothingCommand()); /*Score both */
    }

    private void configureOperatorBindings() {
        // manual climber control
        new Trigger(() -> operator.getRightStick().magnitude() > Settings.Operator.DEADBAND.get())
            .whileTrue(new ClimberDrive(operator));
        //TODO: manual lift control    
        new Trigger(() -> operator.getLeftStick().magnitude() > Settings.Operator.DEADBAND.get())
            .whileTrue(new DoNothingCommand());
        operator.getSelectButton().whileTrue(new ShooterToAmp());
        
        operator.getLeftTriggerButton().whileTrue(new IntakeDeacquire());
        operator.getRightTriggerButton().whileTrue(new IntakeAcquire());

        operator.getStartButton().onTrue(new AmperScore());

        operator.getBottomButton().onTrue(new ClimberScoreRoutine());
        operator.getTopButton().onTrue(new ConveyorToShooter());
        operator.getRightButton().onTrue(ConveyorToAmp.withCheckLift());
    }

    /**************/
    /*** AUTONS ***/
    /**************/

    public void configureAutons() {
        autonChooser = AutoBuilder.buildAutoChooser();

        SmartDashboard.putData("Autonomous", autonChooser);
    }

    public Command getAutonomousCommand() {
        return autonChooser.getSelected();
    }
}