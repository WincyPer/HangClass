// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
//IMPORTS
import edu.wpi.first.wpilibj.TimedRobot;

import javax.swing.text.DefaultStyledDocument.ElementSpec;

import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import org.ejml.ops.ReadMatrixCsv;


public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //HANG MOTORS AND SENSORS
  private CANSparkMax hangPivotMotor;
  private CANSparkMax hangPivotMotorTwo; 
  private WPI_TalonSRX hangElevatorMotor;
  private RelativeEncoder pivotEncoder;
  private TalonEncoder elevatorEncoder;
  
  private DigitalInput hangTopLimit;
  private DigitalInput hangBotLimit;
  private DigitalInput hangFrontLimit;
  private DigitalInput hangBackLimit;
  private AHRS navX;

  private Joystick joystick;
  private HangElevator elevator;
  private HangPivot pivot;
  private Hang hangClass;
  

  
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

                                            //PORT NUMBERS ARE NOT FINAL FOR THE NEW ROBOT
    hangPivotMotor = new CANSparkMax(11, MotorType.kBrushless);
    hangPivotMotorTwo = new CANSparkMax(12, MotorType.kBrushless); 
    hangElevatorMotor = new WPI_TalonSRX(3);
    pivotEncoder = hangPivotMotor.getEncoder();
    elevatorEncoder = new TalonEncoder(hangElevatorMotor);

    hangTopLimit = new DigitalInput(4);
    hangBotLimit = new DigitalInput(3);
    hangFrontLimit = new DigitalInput(1);
    hangBackLimit = new DigitalInput(5);
    navX = new AHRS(SPI.Port.kMXP);

    joystick = new Joystick(0);

    pivot = new HangPivot(hangPivotMotor, hangPivotMotorTwo, pivotEncoder, navX, hangFrontLimit, hangBackLimit); 
    elevator = new HangElevator(hangElevatorMotor, hangTopLimit, hangBotLimit, elevatorEncoder); 
    hangClass = new Hang(pivot, elevator);

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
    /*
      if(joystick.getRawButton(3))
      {
        hangClass.setMidHang();
      }

      else if(joystick.getRawButton(4))
      {
        hangClass.setHighHang();
      } 
      else if(joystick.getRawButton(5))
      {
        hangClass.setElevatorManual();
        hangClass.manualElevatorButton(joystick.getRawButton(9), joystick.getRawButton(10));
      }
      else if(joystick.getRawButton(6))
      {
        hangClass.setElevatorManual();
        hangClass.manualElevator(joystick.getY());
      }
      else if(joystick.getRawButton(7))
      {
        hangClass.setTesting();
      }
      else if (joystick.getRawButton(8)){
        pivot.resetEnc();
        elevator.encoderReset();
      }
      
    else{
      hangClass.setNothing();
    }
    */
    SmartDashboard.putNumber("AXIS NUMBER", joystick.getRawAxis(3)); 

    if (joystick.getRawAxis(3) == -1) {

      if (joystick.getRawButton(3)) {
        hangClass.setMidHang();
      } 
      else if (joystick.getRawButton(4)) {
        hangClass.setHighHang();
      } 

      else if(joystick.getRawButton(5)){
        hangClass.setHighHangGrab();
      }
      
      else if (joystick.getRawButton(8)) {
        pivot.resetEnc();
        elevator.encoderReset();
      } 
      else {
        hangClass.setNothing(); 
      }
      
      }
      
      else if (joystick.getRawAxis(3) == 1) {

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
