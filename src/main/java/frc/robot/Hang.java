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

    //ELEVATOR MOTOR                                        //NUMBERS ARE NOT FINAL, STILL NEED TO FIND CORRECT NUMBERS
    private MotorController elevatorMotor;
    private TalonFXSensorCollection elevatorEncoder;
    private DigitalInput topLimit;                  
    private DigitalInput botLimit;

    private double closeTopLimit = 0.50* 2094;                
    private double closeBotLimit = 600; 
    private double extendSpeed = 0.40;
    private double slowExtendSpeed = 0.30;
    private double retractSpeed = -0.40;
    private double slowRetractSpeed = -0.30;
    
    //PIVOT MOTOR
    private MotorController pivotMotor;
    private TalonEncoder pivotEncoder; 
    private DigitalInput frontLimit;   
    private DigitalInput backLimit;
    private AHRS navX;

    private final double inwardPivotPos = -1100.0;
    private final double outwardPivotPos = -1500.0;
    private final double inwardPivotSpeed = 0.25;
    private final double outwardPivotSpeed = -0.25;

    /////////////////////////////////////////////
    //                                         //
    //              CONSTRUCTOR                //
    //                                         //
    /////////////////////////////////////////////

    public Hang(MotorController elevMotor, DigitalInput limitSwitchTop, DigitalInput limitSwitchBottom, TalonFXSensorCollection elevEncoder, MotorController hangPivotMotor, TalonEncoder hangPivotEncoder, AHRS gyro, DigitalInput frontLimitSwitch, DigitalInput backLimitSwitch ){
        elevatorMotor = elevMotor;
        elevatorEncoder = elevEncoder;
        topLimit = limitSwitchTop;
        botLimit = limitSwitchBottom;
        pivotMotor = hangPivotMotor;
        pivotEncoder = hangPivotEncoder;
        frontLimit = frontLimitSwitch;
        backLimit = backLimitSwitch;
        navX = gyro;
    }

    /////////////////////////////////////////////
    //                                         //
    //               ENUMERATIONS              //
    //                                         //
    /////////////////////////////////////////////

    //PIVOT ENUMERATIONS
    private enum pivotStates{
        PIVINWARD, PIVOUTWARD, TESTING, STOP
    }
    
    pivotStates pivotMode = pivotStates.STOP;

    public void setPivotInward(){
        pivotMode = pivotStates.PIVINWARD; 
    }

    public void setPivotOutward(){
        pivotMode = pivotStates.PIVOUTWARD; 
    }

    public void setPivotTesting(){
        pivotMode = pivotStates.TESTING; 
    }

    public void setPivotStop(){
        pivotMode = pivotStates.STOP; 
    }

    //ELEVATOR ENUMERATIONS
    private enum elevatorStates{
        EXTEND, RETRACT, TESTING, STOP
    }

    elevatorStates elevatorMode = elevatorStates.STOP; 

    public void setElevatorExtend(){
        elevatorMode = elevatorStates.EXTEND; 
    }

    public void setElevatorRetract(){
        elevatorMode = elevatorStates.RETRACT; 
    }

    public void setElevatorTesting(){
        elevatorMode = elevatorStates.TESTING; 
    }

    public void setElevatorStop(){
        elevatorMode = elevatorStates.STOP; 
    }

    /////////////////////////////////////////////
    //                                         //
    //                  CHECKS                 //
    //                                         //
    /////////////////////////////////////////////

    private boolean topLimitTouched(){
        return topLimit.get();
    }

    private boolean bottomLimitTouched(){
        return botLimit.get(); 
    }

    private boolean frontLimitTouched(){
        return frontLimit.get(); 
    }

    private boolean backLimitTouched(){
        return backLimit.get(); 
    }

    public void hangEncReset(){
        pivotEncoder.reset(); 
        elevatorEncoder.setIntegratedSensorPosition(0, 0); 
    }

    /////////////////////////////////////////////
    //                                         //
    //                 METHODS                 //
    //                                         //
    /////////////////////////////////////////////

    //PIVOT METHODS
    private void pivotOutward(){
        if(backLimitTouched()){
            if(pivotEncoder.get() > outwardPivotPos){
                pivotMotor.set(outwardPivotSpeed);
            }

            else{
                pivotMotor.set(0);
            }
        }

        else{
            pivotMotor.set(0);
        }
    }

    private void pivotInward(){
        if(frontLimitTouched()){   //IF THE FRONT LIMIT IS NOT TOUCHED
            if(pivotEncoder.get() < inwardPivotPos){    //IF THE PIVOT ENCODER IS LESS THAN ITS POSITION, PIVOT INWARD
                pivotMotor.set(inwardPivotSpeed);
            }

            else{   //STOP IF POSITION IS REACHED
                pivotMotor.set(0);
            }
        }

        else{
            pivotMotor.set(0);
        }
    }

    private void manualPivotOutward(){
        if(backLimitTouched()){        
            pivotMotor.set(outwardPivotSpeed);
        }

        else{
            pivotMotor.set(0);
        }
    }

    private void manualPivotInward(){
        if(frontLimitTouched()){       //IF THE FRONT LIMIT IS NOT TOUCHED, PIVOT INWARD
            pivotMotor.set(inwardPivotSpeed);       
        }

        else{
            pivotMotor.set(0);
        }
    }

    public void manualPivot(double pivotSpeed){
        pivotMotor.set(pivotSpeed);
    }

    private void pivotStop(){       //STOPS HANG PIVOT
        pivotMotor.set(0);
    }

    private void pivotTesting(){ 
    }

    //ELEVATOR METHODS
    private void elevatorExtend(){
        if(topLimitTouched()){                                                            //if not at top limit
            if(elevatorEncoder.getIntegratedSensorPosition() < closeTopLimit){              //and not close to limit
                elevatorMotor.set(extendSpeed);                                                          //extend fast
            }
            else{                                                                           //if close to limit
                elevatorMotor.set(slowExtendSpeed);                                                          //extend slow
            }
        }
        else{                                                                           //until at top limit
            elevatorMotor.set(0);                                                          //stop extension
        }
    }

    private void elevatorRetract(){
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

    public void manualElevator(double joystickY){
        elevatorMotor.set(joystickY);
    }

    private void elevatorStop(){
        elevatorMotor.set(0);
    }

    private void elevatorTesting(){

    }

    /////////////////////////////////////////////
    //                                         //
    //                   RUN                   //
    //                                         //
    /////////////////////////////////////////////

    
    public void run(){
        SmartDashboard.putNumber("PIVOT ENCODER", pivotEncoder.get());
        SmartDashboard.putString("PIVOT STATE", pivotMode.toString());
        SmartDashboard.putNumber("PIVOT SPEED", pivotMotor.get());
        SmartDashboard.putBoolean("BACK LIMIT", backLimit.get());
        SmartDashboard.putBoolean("FRONT LIMIT", frontLimit.get());
        SmartDashboard.putNumber("NAVX PITCH", navX.getPitch());

        SmartDashboard.putNumber("ELEVATOR ENCODER", elevatorEncoder.getIntegratedSensorPosition());
        SmartDashboard.putNumber("ELEVATOR SPEED", elevatorMotor.get());
        SmartDashboard.putString("ELEVATOR STATE", elevatorMode.toString());
        SmartDashboard.putBoolean("BOTTOM LIMIT", !botLimit.get());
        SmartDashboard.putBoolean("TOP LIMIT", !topLimit.get());
        
        switch(pivotMode){
            case PIVINWARD:
            pivotInward();
            break; 

            case PIVOUTWARD:
            pivotOutward();
            break; 

            case TESTING:
            pivotTesting();
            break; 

            case STOP:
            pivotStop();
            break; 
        }

        switch(elevatorMode){
            case EXTEND:
            elevatorExtend(); 
            break; 

            case RETRACT:
            elevatorRetract();
            break; 

            case TESTING:
            elevatorTesting();
            break; 

            case STOP:
            elevatorStop();
            break; 
        }
    }
}
