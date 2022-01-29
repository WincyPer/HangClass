package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;

public class Hang {
    
    /////////////////////////////////////////////
    //                                         //
    //                VARIABLES                //
    //                                         //
    ///////////////////////////////////////////// 

    //ELEVATOR MOTOR
    private MotorController elevatorMotor;
    private TalonFXSensorCollection elevatorEncoder;
    private DigitalInput limitTop;                  
    private DigitalInput limitBot;
    
    //PIVOT MOTOR
    private MotorController pivotMotor;
    private TalonEncoder pivotEncoder; 
    private DigitalInput frontLimit;   
    private DigitalInput backLimit;
    private AHRS navX;

    /////////////////////////////////////////////
    //                                         //
    //              CONSTRUCTORS               //
    //                                         //
    /////////////////////////////////////////////

    public Hang(MotorController elevMotor, DigitalInput limitSwitchTop, DigitalInput limitSwitchBottom, TalonFXSensorCollection elevEncoder, MotorController hangPivotMotor, TalonEncoder hangPivotEncoder, AHRS gyro, DigitalInput frontLimitSwitch, DigitalInput backLimitSwitch ){
        elevatorMotor = elevMotor;
        limitTop = limitSwitchTop;
        limitBot = limitSwitchBottom;
        elevatorEncoder = elevEncoder;
        pivotMotor = hangPivotMotor;
        pivotEncoder = hangPivotEncoder;
        frontLimit = frontLimitSwitch;
        backLimit = backLimitSwitch;
        navX = gyro;
    }

    private enum hangStates{

    }

    
}
