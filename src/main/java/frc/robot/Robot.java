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
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Timer;

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
  private WPI_VictorSPX weightAdjusterMotor; 
  private TalonEncoder pivotEncoder;
  private TalonFXSensorCollection elevatorEncoder;
  private SingleChannelEncoder weightAdjusterEnc; 
  private DigitalInput weightAdjusterDIO; 

  private WPI_TalonSRX intakeMotor;
  private DigitalInput intakeSensor;
  private Timer intakeTimer;
  private WPI_TalonSRX intakeExt;
  private DigitalInput intakeExtChannel;
  private DigitalInput intakeArmLim;
  private SingleChannelEncoder intakeExtEnc;
  private WPI_VictorSPX outerRollers;
  private Intake intake;
  
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

    intakeMotor = new WPI_TalonSRX(3);
    intakeSensor = new DigitalInput(4);
    intakeTimer = new Timer();

    intakeExt = new WPI_TalonSRX(5);
    intakeExtChannel = new DigitalInput(5);
    intakeArmLim = new DigitalInput(6);
    intakeExtEnc = new SingleChannelEncoder(intakeExt, intakeExtChannel);
    outerRollers = new WPI_VictorSPX(0);
    intake = new Intake(intakeMotor, intakeExt, outerRollers, intakeExtEnc, intakeSensor, intakeArmLim);
    
    hangPivotMotor = new WPI_TalonSRX(4);
    hangElevatorMotor = new WPI_TalonFX(2);
    weightAdjusterMotor = new WPI_VictorSPX(5); 
    pivotEncoder = new TalonEncoder(hangPivotMotor);
    elevatorEncoder = new TalonFXSensorCollection(hangElevatorMotor);
    weightAdjusterDIO = new DigitalInput(9); 
    weightAdjusterEnc = new SingleChannelEncoder(weightAdjusterMotor, weightAdjusterDIO); 

    hangTopLimit = new DigitalInput(2);
    hangBotLimit = new DigitalInput(1);
    hangFrontLimit = new DigitalInput(3);
    hangBackLimit = new DigitalInput(0);
    navX = new AHRS(SPI.Port.kMXP);

    pivot = new HangPivot(hangPivotMotor, pivotEncoder, navX, hangFrontLimit, hangBackLimit); 
    elevator = new HangElevator(hangElevatorMotor, hangTopLimit, hangBotLimit, elevatorEncoder); 
    weightAdj = new WeightAdjuster(weightAdjusterMotor, weightAdjusterEnc); 
    hangClass = new Hang(pivot, elevator, intake, weightAdj, pivotEncoder);
    hangElevatorMotor.setNeutralMode(NeutralMode.Brake);
    hangPivotMotor.setNeutralMode(NeutralMode.Brake);

    frontLeft = new CANSparkMax(7, MotorType.kBrushless);
    backLeft = new CANSparkMax (8, MotorType.kBrushless);
    frontRight = new CANSparkMax(5, MotorType.kBrushless);
    backRight = new CANSparkMax(6, MotorType.kBrushless);
    
    joystick = new Joystick(0);
    //joystick1 = new Joystick(1);
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
    
    if (joystick.getRawAxis(3) == -1) {
      SmartDashboard.putString("MODE:", "REAAALLL!!!");
      //drive.arcadeDrive(-joystick1.getX(), -joystick1.getY());
      
      if(joystick.getRawButton(11)){
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

      else if(joystick.getRawButton(10)){
        hangClass.setTesting();
        elevator.setExtendLimSlow();
      }

      else if(joystick.getRawButton(12)){
        hangClass.setTesting();
        elevator.setRetractLimSlow();
      }

      else if(joystick.getRawButton(9)){
        hangClass.setTesting();
        pivot.setTesting();
        pivot.manualPivot(joystick.getY());
          
        if(joystick.getRawButton(7)){
          elevator.setExtendLimFast();
         }
  
        else if(joystick.getRawButton(8)){
          elevator.setRetractLimFast();
         }

        else{
          elevator.setElevatorStop();
        }
      }

      else if(joystick.getRawButton(7)){
        hangClass.setTesting();
        elevator.setExtendLimFast();
      }

      else if(joystick.getRawButton(8)){
        hangClass.setTesting();
        elevator.setRetractLimFast();
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

        if(joystick.getRawButton(1)){
            pivot.setTesting();
            pivot.manualPivot(joystick.getY());
        }

        else if(joystick.getRawButton(2)){
            pivot.setPivInwardLim();
        }

        else if(joystick.getRawButton(3)){
            pivot.setPivOutwardLim();
        }

        else{
            pivot.setStop();
        }

        if(joystick.getRawButton(4)){
          intake.setArmTestingMode();
          intake.manualIntakeExt(joystick.getY());
        }

        else if(joystick.getRawButton(5)){
          intake.setExtend();
        }

        else if(joystick.getRawButton(6)){
          intake.setMidway();
        }

        else{
          intake.setArmStopMode();
        }

        if(joystick.getRawButton(7)){
          elevator.setExtendLimSlow();
        }

        else if(joystick.getRawButton(8)){
          elevator.setRetractLimSlow();
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

        else if(joystick.getRawButton(11)){
          weightAdj.resetEncoder();
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
          //weightAdj.weightReset();
        }

        else if(joystick.getPOV() == 180){
          hangClass.resetCounters();
        }
        
        intake.intakeRun();
        pivot.run();
        elevator.run();
        weightAdj.run();
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
    weightAdj.resetEncoder();
    weightAdj.resetTimer();
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
