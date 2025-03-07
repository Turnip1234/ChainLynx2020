/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.RampSubsystem;
import edu.wpi.first.cameraserver.CameraServer;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  //Creates an instance of the drivetrain subsystem
  private DrivetrainSubsystem drivetrain = new DrivetrainSubsystem();
  private RampSubsystem ramp = new RampSubsystem();

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private SequentialCommandGroup m_autoCommand;

  //Creates a joystick instance
  private Joystick driveStick = new Joystick(0);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    CameraServer.getInstance().startAutomaticCapture();
    }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    switch (m_autoSelected) {
      case kCustomAuto:
        m_autoCommand = new SequentialCommandGroup(
          new RunCommand(() -> {
            drivetrain.setPower(0.5, 0.5);
          }, drivetrain).withTimeout(5.0),
          new RunCommand(() -> {
            ramp.setPower(0.7)
          }).withTimeout(3.0);
        )
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }

    if (m_autoCommand != null) {
      m_autoCommand.schedule();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    
  }

  @Override
  public void teleopInit() {
    if (m_autoCommand != null) {
      m_autoCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    //Reads X and Y values from the joystick
    double throttle = driveStick.getY();
    double turn = driveStick.getX();
    boolean isOuttakePressed = driveStick.getRawButton(Constants.kOuttakeButton);
    boolean isReverseOuttakepressed = driveStick.getRawButton(Constants.kReverseOuttakeButton); 
    //Feeds the drive function with joystick values
    drivetrain.drive(throttle, turn);
    if (isOuttakePressed) {
      ramp.setPower(0.5);
    }
    else {
      ramp.setPower(0);
    }
  
  if (isReverseOuttakepressed) {
    ramp.setPower(0.-5);
  } 
  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
