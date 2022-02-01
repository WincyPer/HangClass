// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;


public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //HANG MOTORS AND SENSORS
  private WPI_TalonSRX hangPivotMotor;
  private WPI_TalonFX hangElevatorMotor;
  private TalonEncoder pivotEncoder;
  private TalonFXSensorCollection elevatorEncoder;
  
  private DigitalInput hangTopLimit;
  private DigitalInput hangBotLimit;
  private DigitalInput hangFrontLimit;
  private DigitalInput hangBackLimit;
  private AHRS navX;

  private Joystick joystick;
  private Hang hangClass;

  
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

                                            //PORT NUMBERS ARE NOT FINAL FOR THE NEW ROBOT
    hangPivotMotor = new WPI_TalonSRX(0);
    hangElevatorMotor = new WPI_TalonFX(0);
    pivotEncoder = new TalonEncoder(hangPivotMotor);
    elevatorEncoder = new TalonFXSensorCollection(hangElevatorMotor);

    hangTopLimit = new DigitalInput(0);
    hangBotLimit = new DigitalInput(1);
    hangFrontLimit = new DigitalInput(2);
    hangBackLimit = new DigitalInput(3);
    navX = new AHRS(SPI.Port.kMXP);

    hangClass = new Hang(hangElevatorMotor, hangTopLimit, hangBotLimit, elevatorEncoder, hangPivotMotor, pivotEncoder, navX, hangFrontLimit, hangBackLimit);


  }

 
  @Override
  public void robotPeriodic() {}


  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
      break;

      case kDefaultAuto:
      default:
      break;

    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
    if(joystick.getRawButton(7)){
      hangClass.setElevatorRetract();
    }

    else if(joystick.getRawButton(8)){
      hangClass.setElevatorExtend();
    }

    else if (joystick.getRawButton(3)) {
      hangClass.setElevatorTesting();
      hangClass.manualElevator(joystick.getY());
    }

    else{
      hangClass.setElevatorStop();
    }

    if (joystick.getRawButton(9)) {
      hangClass.setPivotInward();
    }

    else if (joystick.getRawButton(10)){
      hangClass.setPivotOutward();
    }

    else if (joystick.getRawButton(4)) {
      hangClass.setPivotTesting();
      hangClass.manualPivot(joystick.getY());
    }

    else{
      hangClass.setPivotStop();
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    hangClass.setPivotStop();
    hangClass.setElevatorStop();
    hangClass.resetCounters();
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
