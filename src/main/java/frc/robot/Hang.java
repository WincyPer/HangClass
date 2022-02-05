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

    private double closeTopLimit = 0.50 * 2094;                
    private double closeBotLimit = 600; 
    private double extendSpeed = 0.40;
    private double slowExtendSpeed = 0.30;
    private double retractSpeed = -0.40;
    private double slowRetractSpeed = -0.30;

    private final double highHangElevator = 800.0; 
    
    //PIVOT MOTOR
    private MotorController pivotMotor;
    private TalonEncoder pivotEncoder; 
    private DigitalInput frontLimit;   
    private DigitalInput backLimit;
    private AHRS navX;

    private final double inwardPivotPos = -600.0; //INWARD POSITION FOR THE ANGLES OF HIGH HANG & UP
    private final double midPivotPos = -800.0;
    private final double outwardPivotPos = -1500.0; //OUTWARD POSITION FOR GETTING ONTO RUNG
    private final double inwardPivotSpeed = 0.25;
    private final double outwardPivotSpeed = -0.25;
    private final double highHangGrab = -700.0; 

    //COUNTERS AND OTHER VARIABLES
    private int setUpMidCount = 0;
    private int setUpHighCount = 0; 

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
    
    pivotStates pivotMode = pivotStates.STOP;   //DEFAULTS PIVOT MODE TO STOP 

    public void setPivotInward(){       //SETS TO PIVOT INWARD, REST OF METHODS FOLLOW RESPECTIVES STATES
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

    elevatorStates elevatorMode = elevatorStates.STOP;      //DEFAULTS ELEVATOR MODE TO STOP

    public void setElevatorExtend(){        //SETS TO ELEVATOR EXTEND, REST OF METHODS FOLLOW RESPECTIVE STATES
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

    private boolean topLimitTouched(){      //CHECKS IF TOP SWITCH OF THE ELEVATOR IS REACHED
        return topLimit.get();
    }

    private boolean bottomLimitTouched(){       //CHECKS IF BOTTOM SWITCH OF THE ELEVATOR IS REACHED
        return botLimit.get(); 
    }

    private boolean upwardLimitReached(){
        return elevatorEncoder.getIntegratedSensorPosition() < closeTopLimit;    //TRUE IF PAST TOP ENCODER CHECK 
    }

    private boolean downwardLimitReached() {
        return elevatorEncoder.getIntegratedSensorPosition() > closeBotLimit;    //TRUE IF PAST BOTTOM ENCODER CHECK
    }

    private boolean frontLimitTouched(){        //CHECKS IF FRONT SWITCH OF THE PIVOT ARMS IS REACHED
        return frontLimit.get(); 
    }

    private boolean backLimitTouched(){     //CHECKS IF BACK SWITCH OF THE PIVOT ARMS IS REACHED
        return backLimit.get(); 
    }

    private boolean outwardLimitReached(){  //CHECKS IF PIVOT ENCODER REACHED OUTWARD 
        return pivotEncoder.get() > outwardPivotPos; 
    }

    private boolean inwardLimitReached(){  //CHECKS IF PIVOT ENCODER REACHED INWARD 
        return pivotEncoder.get() < inwardPivotPos; 
    }

    private boolean midLimitReached(){  //CHECKS IF PIVOT ENCODER REACHED MIDDLE
        return pivotEncoder.get() < midPivotPos; 
    }

    public void hangEncReset(){     //RESETTING BOTH PIVOT AND ELEVATOR ENCODERS
        pivotEncoder.reset(); 
        elevatorEncoder.setIntegratedSensorPosition(0, 0); 
    }

    /////////////////////////////////////////////
    //                                         //
    //                 METHODS                 //
    //                                         //
    /////////////////////////////////////////////

    //  PIVOT METHODS  //
    private void pivotOutwardLim(){        //  PIVOTS OUTWARD UNTIL IT REACHES THE MAX ENCODER COUNT OR TOUCHES THE LIMIT SWITCH  //
        if(backLimitTouched()){     //IF BACK LIMIT IS TOUCHED (TRUE/FALSE & LESS/MORE MAY DIFFER ON NEW ROBOT)
            pivotMotor.set(0);  //SET SPEED TO 0
        }

        else{     
            if(outwardLimitReached()){      //ELSE IF LIMIT IS NOT TOUCHED PIVOT OUTWARD
                pivotMotor.set(outwardPivotSpeed);
            }

            else{        
                pivotMotor.set(0);      
            }
        }
    }

    private void pivotInwardLim(){     //  PIVOTS INWARD UNTIL IT REACHES THE MAX ENCODER COUNT OR TOUCHES THE LIMIT SWITCH  //
        if(frontLimitTouched()){   //IF FRONT LIMIT IS TOUCHED
            pivotMotor.set(0);     // SET SPEED TO 0 
        }

        else{       
            if(!inwardLimitReached()){        //ELSE IF FRONT LIMIT IS NOT TOUCHED PIVOT INWARD  
                pivotMotor.set(inwardPivotSpeed);
            }

            else{   
                pivotMotor.set(0);
            }
        }
    }

    private void pivotOutward(){      //PIVOTS OUTWARD, UNLESS BACK LIMIT IS TOUCHED
        pivotMotor.set(outwardPivotSpeed);
    }

    private void pivotInward(){       //PIVOTS INWARD, UNLESS FRONT LIMIT IS TOUCHED
        pivotMotor.set(inwardPivotSpeed);
    }

    public void manualPivot(double pivotSpeed){         //PIVOTS TO A GIVEN SPEED, USE FOR TESTING
        pivotMotor.set(pivotSpeed);
    }

    private void pivotStop(){       //STOPS HANG PIVOT
        pivotMotor.set(0);
    }

    private void pivotTesting(){        //EMPTY CODE FOR TESTING
    }

    //  ELEVATOR METHODS  //
    private void elevExtendLim(){
        if(topLimitTouched()){     //IF TOP LIMIT TOUCHED                                                         
            elevatorStop();  //STOP ELEVATOR
        } else {
            if (upwardLimitReached()) {   //ELSE IF TOP LIMIT IS NOT TOUCHED, EXTEND UNTIL TOP LIMIT IS TOUCHED 
                elevatorMotor.set(slowExtendSpeed); 
            } else {
                elevatorMotor.set(extendSpeed); 
            }
        }
    }

    private void elevRetractLim(){
        if(bottomLimitTouched()){       //IF BOTTOM LIMIT IS TOUCHED 
            elevatorStop();             //STOP ELEVATOR
            elevatorEncoder.setIntegratedSensorPosition(0, 0); 
        } else {
            if (downwardLimitReached()) {   //ELSE IF BOTTOM LIMIT IS NOT TOUCHED, RETRACT UNTIL BOTTOM LIMIT IS TOUCHED 
                elevatorMotor.set(slowRetractSpeed); 
            } else {
                elevatorMotor.set(retractSpeed); 
            }
        }
    }

    private void elevExtend(){                                          //set speed to extend
        elevatorMotor.set(extendSpeed);
    }                
    
    private void elevRetract(){
        elevatorMotor.set(retractSpeed);
    }

    private void extendSlow(){
        elevatorMotor.set(slowExtendSpeed); 
    }

    private void retractSlow(){
        elevatorMotor.set(slowRetractSpeed); 
    }

    public void manualElevator(double joystickY){       //PIVOTS TO A GIVEN SPEED, USE FOR TESTING
        elevatorMotor.set(joystickY);
    }

    private void elevatorStop(){        //STOP ELEVATOR 
        elevatorMotor.set(0);
    }

    private void elevatorTesting(){     //EMPTY FOR TESTING

    }

    public void resetCounters(){
        setUpMidCount = 0;
        setUpHighCount = 0; 
    }    

    /////////////////////////////////////////////
    //                                         //
    //                   RUN                   //
    //                                         //
    /////////////////////////////////////////////

    
    public void run(){
        //SMART DASHBOARD DISPLAYS
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

        SmartDashboard.putNumber("MID HANG COUNTER", setUpMidCount); 
        SmartDashboard.putNumber("HIGH HANG COUNTER", setUpHighCount); 
        
        switch(pivotMode){
            case PIVINWARD:
            pivotInwardLim();
            break; 

            case PIVOUTWARD:
            pivotOutwardLim();
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
            elevExtend(); 
            break; 

            case RETRACT:
            elevRetract();
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
