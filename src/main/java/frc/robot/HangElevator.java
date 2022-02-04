package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


//FALCON 500 (1)

public class HangElevator{
   
    //MOTORS
    private MotorController elevatorMotor;

    //ENCODERS
    private TalonFXSensorCollection elevatorEncoder;

    //SENSORS
    private DigitalInput limitTop;          //4000                    
    private DigitalInput limitBot;          //-1200

    //VALUES
    private double closeTopLimit = 0.50* 2094;                   //encoder value, when close to the top limit switch, start to slow down         
    private double closeBotLimit = 600;                   // -- bottom switch --
    private double extendSpeed = 0.40;
    private double slowExtendSpeed = 0.30;
    private double retractSpeed = -0.40;
    private double slowRetractSpeed = -0.30;

    //CONSTRUCTOR
    public HangElevator(MotorController elevMotor, DigitalInput limitSwitchTop, DigitalInput limitSwitchBottom, TalonFXSensorCollection elevEncoder){
        elevatorMotor = elevMotor;
        limitTop = limitSwitchTop;
        limitBot = limitSwitchBottom;
        elevatorEncoder = elevEncoder;
    }
    
    //ENUMERATIONS/STATES
    private enum elevatorState{
        EXTEND, RETRACT, STOP, TESTING;
    }
    
    private elevatorState runState = elevatorState.STOP;        

    public void elevatorExtend(){
        runState = elevatorState.EXTEND;
    }

    public void elevatorRetract(){
        runState = elevatorState.RETRACT;
    }

    public void elevatorStop(){
        runState = elevatorState.STOP;
    }

    public void elevatorTest(){
        runState = elevatorState.TESTING;
    }

    //CHECKS
    private boolean topLimitTouched(){      
        return limitTop.get();
    }

    private boolean bottomLimitTouched(){      
        return limitBot.get(); 
    }


    //STOP
    private void stop(){
        elevatorMotor.set(0);
    }

    //TESTING
    public void testing(){

    }

    private void elevExtend(){                                          //set speed to extend
        elevatorMotor.set(extendSpeed);
    }                
    
    private void elevRetract(){
        elevatorMotor.set(retractSpeed);
    }

    private void elevExtendSlow(){
        elevatorMotor.set(slowExtendSpeed);
    }
    
    private void elevRetractSlow(){
        elevatorMotor.set(slowRetractSpeed);
    }

    public void manualElev(double speed){
        if(!topLimitTouched() || !bottomLimitTouched())
        elevatorMotor.set(speed);
    }

    public void encoderReset(){
        elevatorEncoder.setIntegratedSensorPosition(0, 0);
    }
    
    //CHECK
    private boolean topLimitCheck(){                                                        //return true if past top encoder check
        return elevatorEncoder.getIntegratedSensorPosition() > closeTopLimit;
    }

    private boolean botLimitCheck(){                                                                //return true if past bottom encoder check
        return elevatorEncoder.getIntegratedSensorAbsolutePosition() < closeBotLimit;
    }

    //EXTEND
    private void extend(){
        if(topLimitTouched()){                                                            //if not at top limit
            if(elevatorEncoder.getIntegratedSensorPosition() < closeTopLimit){              //and not close to limit
                elevatorMotor.set(extendSpeed);                                                          //extend fast
            }
            else{                                                                           //if close to limit
                elevatorMotor.set(slowExtendSpeed);                                                          //extend slow
            }
        }
        else{                                                                           //until at top limit
            elevatorMotor.set(0);                                                           //stop extension
        }
    }

    //RETRACT
    private void retract(){
        if(bottomLimitTouched()){
            if(elevatorEncoder.getIntegratedSensorPosition() > closeBotLimit){
                elevatorMotor.set(retractSpeed);
            }
            else{
                elevatorMotor.set(slowRetractSpeed);
            }
        }
        else{
            elevatorMotor.set(0);
            elevatorEncoder.setIntegratedSensorPosition(0, 0);
        }
    }

    //RUN
    public void run(){
        SmartDashboard.putNumber("ElevatorEncoder:", elevatorEncoder.getIntegratedSensorPosition());
        SmartDashboard.putBoolean("Elevator Top Limit:", !limitTop.get());
        SmartDashboard.putBoolean("Elevator Bottom Limit:", !limitBot.get());
        SmartDashboard.putNumber("Elevator Arm Speed:", elevatorMotor.get());
        SmartDashboard.putString("Elevator Run State:", runState.toString());
        switch(runState){
            
            case STOP:
            stop();
            break;

            case EXTEND:
            extend();
            break;

            case RETRACT:
            retract();
            break;
            
            case TESTING:
            testing();
            break;

            default:
            stop();
            break;
        }
        
    }
    
}