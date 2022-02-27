// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
//IMPORTS
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

import javax.swing.text.DefaultStyledDocument.ElementSpec;

import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;

import org.ejml.ops.ReadMatrixCsv;


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
  private HangElevator elevator;
  private HangPivot pivot;
  private Hang hangClass;

  private Drive drive;
  private CANSparkMax frontleft;
  private CANSparkMax frontright;
  private CANSparkMax backleft;
  private CANSparkMax backright;
  
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    hangPivotMotor = new WPI_TalonSRX(4);
    hangElevatorMotor = new WPI_TalonFX(2);
    pivotEncoder = new TalonEncoder(hangPivotMotor);
    elevatorEncoder = new TalonFXSensorCollection(hangElevatorMotor);

    hangTopLimit = new DigitalInput(2);
    hangBotLimit = new DigitalInput(1);
    hangFrontLimit = new DigitalInput(3);
    hangBackLimit = new DigitalInput(0);
    navX = new AHRS(SPI.Port.kMXP);

    joystick = new Joystick(0);

    pivot = new HangPivot(hangPivotMotor, pivotEncoder, navX, hangFrontLimit, hangBackLimit); 
    elevator = new HangElevator(hangElevatorMotor, hangTopLimit, hangBotLimit, elevatorEncoder); 
    hangClass = new Hang(pivot, elevator);

    drive = new Drive(frontleft, backleft, frontright, backright);

  }

 
  @Override
  public void robotPeriodic() {
  }


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
   
    SmartDashboard.putNumber("AXIS NUMBER", joystick.getRawAxis(3)); 

    if (joystick.getRawAxis(3) == -1) {
      SmartDashboard.putString("MODE:", "REAAALLL!!!");
      drive.arcadeDrive(joystick.getY(), joystick.getX());
      if (joystick.getRawButton(3)) {
        hangClass.setMidHang();
      } 
      else if (joystick.getRawButton(4)) {
        hangClass.setHighHang();
      } 

      else if(joystick.getRawButton(5)){
        hangClass.setHighHangGrab();
      }
/*
      else if(joystick.getRawButton(6)){
        hangClass.resetCounters();
      }
*/
      else if (joystick.getRawButton(8)) {
        pivot.resetEnc();
        elevator.encoderReset();
      } 
      else {
        hangClass.setNothing(); 
      }
      
      }
      
      else if (joystick.getRawAxis(3) == 1) {
        SmartDashboard.putString("MODE:", "ARTIFICIAL");
        if (joystick.getRawButton(3)) {
          hangClass.setElevatorManual();
          hangClass.manualElevator(joystick.getY());
        } 
        else if (joystick.getRawButton(4)) {
          hangClass.setPivotManual();
          hangClass.manualPivot(joystick.getY());
        } 
        else if (joystick.getRawButton(8)) {
          pivot.resetEnc();
          elevator.encoderReset();
        } 
        else {
          hangClass.setNothing();
        }

      } else {
        hangClass.setNothing(); 
      }

    hangClass.run();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    hangClass.setNothing();
    hangClass.resetCounters();
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
