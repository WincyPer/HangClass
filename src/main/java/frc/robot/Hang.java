package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;

public class Hang {
    
    private MotorController elevatorMotor;
    private DigitalInput limitTop;                  
    private DigitalInput limitBot;
    
    private MotorController pivotMotor;
    private TalonEncoder pivotEncoder; 
    private DigitalInput frontLimit;   
    private DigitalInput backLimit;

    
}
