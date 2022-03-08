// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
//IMPORTS

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import java.nio.channels.NetworkChannel;
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
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.NeutralMode; 
import edu.wpi.first.math.controller.PIDController;

import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //HANG MOTORS AND SENSORS
  private WPI_TalonSRX hangPivotMotor;
  private PIDController pivotPID;
  private WPI_TalonFX hangElevatorMotor;
  private WPI_TalonSRX weightAdjusterMotor; 
  private TalonEncoder pivotEncoder;
  private TalonFXSensorCollection elevatorEncoder;
  private SingleChannelEncoder weightAdjusterEnc; 
  private DigitalInput weightAdjusterDIO; 
  
  private DigitalInput hangTopLimit;
  private DigitalInput hangBotLimit;
  private DigitalInput hangFrontLimit;
  private DigitalInput hangBackLimit;
  private AHRS navX;

  private Joystick joystick;
  private Joystick joystick1;
  private HangElevator elevator;
  private HangPivot pivot;
  private Hang hangClass;
  private WeightAdjuster weightAdj; 

  private Drive drive;
  private CANSparkMax frontLeft;
  private CANSparkMax frontRight;
  private CANSparkMax backLeft;
  private CANSparkMax backRight;
  
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    hangPivotMotor = new WPI_TalonSRX(4);
    hangElevatorMotor = new WPI_TalonFX(2);
    weightAdjusterMotor = new WPI_TalonSRX(9); 
    pivotEncoder = new TalonEncoder(hangPivotMotor);
    elevatorEncoder = new TalonFXSensorCollection(hangElevatorMotor);
    weightAdjusterDIO = new DigitalInput(9); 
    weightAdjusterEnc = new SingleChannelEncoder(weightAdjusterMotor, weightAdjusterDIO); 

    hangTopLimit = new DigitalInput(2);
    hangBotLimit = new DigitalInput(1);
    hangFrontLimit = new DigitalInput(3);
    hangBackLimit = new DigitalInput(0);
    navX = new AHRS(SPI.Port.kMXP);

    joystick = new Joystick(0);
    //joystick1 = new Joystick(1);

    pivot = new HangPivot(hangPivotMotor, pivotEncoder, navX, hangFrontLimit, hangBackLimit); 
    elevator = new HangElevator(hangElevatorMotor, hangTopLimit, hangBotLimit, elevatorEncoder); 
    weightAdj = new WeightAdjuster(weightAdjusterMotor, weightAdjusterEnc); 
    hangClass = new Hang(pivot, elevator, weightAdj, pivotEncoder);
    hangElevatorMotor.setNeutralMode(NeutralMode.Brake);
    hangPivotMotor.setNeutralMode(NeutralMode.Brake);


    frontLeft = new CANSparkMax(7, MotorType.kBrushless);
    backLeft = new CANSparkMax (8, MotorType.kBrushless);
    frontRight = new CANSparkMax(5, MotorType.kBrushless);
    backRight = new CANSparkMax(6, MotorType.kBrushless);
    

    drive = new Drive(frontLeft, backLeft, frontRight, backRight);

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
  /*  switch (m_autoSelected) {
      case kCustomAuto:
      break;

      case kDefaultAuto:
      default:
      break;

    } */
  }


  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
   
    SmartDashboard.putNumber("AXIS NUMBER", joystick.getRawAxis(3)); 
    SmartDashboard.putNumber("DRIVER TIME", DriverStation.getMatchTime());

    if (joystick.getRawAxis(3) == -1) {
      SmartDashboard.putString("MODE:", "REAAALLL!!!");
      //drive.arcadeDrive(-joystick1.getX(), -joystick1.getY());
      
      if(joystick.getRawButton(10)){
        drive.arcadeDrive(-joystick.getX(), -joystick.getY());
      }

      else{
        drive.arcadeDrive(0, 0);
      }

      if (joystick.getRawButton(3)) {
        hangClass.setMidHang();
      } 
      else if (joystick.getRawButton(4)) {
        hangClass.setHighHang();
      } 

      else if(joystick.getRawButton(5)){
        hangClass.setHighHangGrab();
      }

      else if(joystick.getRawButton(7)){
        hangClass.setTesting();
        elevator.setElevatorExtendLim();
      }

      else if(joystick.getRawButton(8)){
        hangClass.setTesting();
        elevator.setElevatorRetractLim();
      }

      else if(joystick.getRawButton(9)){
        hangClass.setTesting();
        pivot.setTesting();
        pivot.manualPivot(joystick.getY());
      }

      else if(joystick.getRawButton(11)){
        hangClass.resetCounters();
      }

      else if (joystick.getRawButton(12)) {
        pivot.resetEnc();
        elevator.encoderReset();
      }

      else {
        hangClass.setNothing(); 
      }

      hangClass.run();
      
    }
      
      else if (joystick.getRawAxis(3) == 1) {       //MAKE SURE TO BE ADDING PIDS TO ELEVATOR AND PIVOT
        SmartDashboard.putString("MODE:", "ARTIFICIAL");

        // joystick1 > drive & pivot 
        //joystick > elevator & weightAdj

        if(joystick.getRawButton(3)){
          pivot.setTesting();
          pivot.manualPivot(joystick.getY());
        }

        else{
          pivot.setStop();
        }

        if(joystick.getRawButton(7)){
          elevator.setElevatorExtendLim();
        }

        else if(joystick.getRawButton(8)){
          elevator.setElevatorRetractLim();
        }

        else if (joystick.getRawButton(12)){
          elevator.setElevatorTest();
          elevator.manualElev(joystick.getY());
        }

        else{
          elevator.setElevatorStop();
        }

        if(joystick.getRawButton(9)){
          weightAdj.setWeightTest();
          weightAdj.manualWeight(joystick.getY());
        }

        else{
          weightAdj.setWeightStop();
        }

        if(joystick.getRawButton(10)){
          drive.arcadeDrive(-joystick.getX(), -joystick.getY());
        }
        else{
          drive.arcadeDrive(0, 0);
        }

        if (joystick.getPOV()== 0) {
          pivot.resetEnc();
          elevator.encoderReset();
          weightAdj.weightReset();
        }
        

        pivot.run();
        elevator.run();
        weightAdj.run();

      
/*
        if(joystick1.getRawButton(5)){
        drive.arcadeDrive(-joystick1.getX(), -joystick1.getY());
        }

        else{
          drive.arcadeDrive(0, 0);
        } 

        if (joystick.getRawButton(3)) {
          elevator.setElevatorTest();
          elevator.testing(joystick.getY());
        } 

        else if(joystick.getRawButton(4)){
          elevator.setElevatorExtend();
        }

        else if(joystick.getRawButton(5)){
          elevator.setElevatorRetract();
        }

        else{
          elevator.setElevatorStop();
        }

        if (joystick1.getRawButton(6)) {
          pivot.setTesting();
          pivot.manualPivot(-joystick1.getY());
        } 

        else if (joystick1.getRawButton(12)) {
          pivot.resetEnc();
          elevator.encoderReset();
        } 

        else {
          pivot.setStop();
        }

        if(joystick.getRawButton(7)){
          weightAdj.setWeightTest();
          weightAdj.manualWeight(joystick.getY());
        }

        else{
          weightAdj.setWeightStop();
        }

        weightAdj.run();
        elevator.run();
        pivot.run();
*/
      } 
      
    
    

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
