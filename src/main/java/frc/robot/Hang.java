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
    private TalonEncoder elevatorEncoder;
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

    public Hang(MotorController elevMotor, DigitalInput limitSwitchTop, DigitalInput limitSwitchBottom, TalonEncoder elevEncoder, MotorController hangPivotMotor, TalonEncoder hangPivotEncoder, AHRS gyro, DigitalInput frontLimitSwitch, DigitalInput backLimitSwitch ){
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

    private boolean upwardLimitReached(){                                                        //return true if past top encoder check
        return elevatorEncoder.get() > closeTopLimit;
    }

    private boolean downwardLimitReached(){                                                                //return true if past bottom encoder check
        return elevatorEncoder.get() < closeBotLimit;
    }

    private boolean frontLimitTouched(){        //CHECKS IF FRONT SWITCH OF THE PIVOT ARMS IS REACHED
        return frontLimit.get(); 
    }

    private boolean backLimitTouched(){     //CHECKS IF BACK SWITCH OF THE PIVOT ARMS IS REACHED
        return backLimit.get(); 
    }

    private boolean outwardLimitReached(){      //CHECKS IF PIVOT ENCODER REACHED OUTWARD
        return pivotEncoder.get() > outwardPivotPos;
    }

    private boolean inwardLimitReached(){       //CHECKS IF PIVOT ENCODER REACHED INWARD
        return pivotEncoder.get() < inwardPivotPos;
    }

    private boolean midLimitReached() {
        return pivotEncoder.get() < midPivotPos;
    }

    public void hangEncReset(){     //RESETTING BOTH PIVOT AND ELEVATOR ENCODERS
        pivotEncoder.reset(); 
        elevatorEncoder.reset();; 
    }

    /////////////////////////////////////////////
    //                                         //
    //                 METHODS                 //
    //                                         //
    /////////////////////////////////////////////

    //  PIVOT METHODS  //
    private void pivotOutwardLim(){        //  PIVOTS OUTWARD UNTIL IT REACHES THE MAX ENCODER COUNT OR TOUCHES THE LIMIT SWITCH  //
        if(!backLimitTouched()){     //IF BACK LIMIT IS NOT TOUCHED (TRUE/FALSE & LESS/MORE MAY DIFFER ON NEW ROBOT)
            
            if(outwardLimitReached()){       //IF PIVOT ENCODER IS MORE THAN NEEDED COUNT, GO. OTHERWISE, STOP.
                pivotMotor.set(outwardPivotSpeed);
            }

            else{        
                pivotMotor.set(0);      
            }
        }

        else{       //ELSE (LIMIT IS TOUCHED), TURN OFF MOTOR
            pivotMotor.set(0);
        }
    }

    private void pivotInwardLim(){     //  PIVOTS INWARD UNTIL IT REACHES THE MAX ENCODER COUNT OR TOUCHES THE LIMIT SWITCH  //
        if(!frontLimitTouched()){   //IF FRONT LIMIT IS NOT TOUCHED
            if(inwardLimitReached()){        //IF PIVOT ENCODER IS LESS THAN NEEDED COUNT, GO. OTHERWISE, STOP.  
                pivotMotor.set(inwardPivotSpeed);
            }

            else{   
                pivotMotor.set(0);
            }
        }

        else{       //ELSE (LIMIT IS TOUCHED), TURN OFF MOTOR
            pivotMotor.set(0);
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
        if(!topLimitTouched()){      //IF NOT AT TOP LIMIT                                                        
            if(upwardLimitReached()){      //EXTEND AT NORMAL SPEED IF ELEVATOR IS NOT CLOSE TO LIMIT
                elevatorMotor.set(extendSpeed);                                                          
            }
            else{       //EXTEND AT SLOW SPEED IF CLOSE TO TOP LIMIT                                                                            
                elevatorMotor.set(slowExtendSpeed);                                                          
            }
        }
        else{       //STOP WHEN TOP LIMIT IS TOUCHED                                             
            elevatorMotor.set(0);                                                          
        }
    }

    private void elevRetractLim(){
        if(!bottomLimitTouched()){       //IF NOT AT BOTTOM LIMIT
            if(downwardLimitReached()){      //EXTEND AT NORMAL SPEED IF ELEVATOR IS NOT CLOSE TO LIMIT
                elevatorMotor.set(retractSpeed);
            }
            else{       //EXTEND AT SLOW SPEED IF CLOSE TO BOTTOM LIMIT
                elevatorMotor.set(slowRetractSpeed);
            }
        }
        else{       //STOP WHEN BOTTOM LIMIT IS TOUCHED, RESETS ENCODER TO HOME POSITION
            elevatorMotor.set(0);
            elevatorEncoder.reset();
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

    private void midHangGrab() {
        switch(setUpMidCount) {
            case 0: 
            //pivot outward 
            if ((!backLimitTouched() || outwardLimitReached())) {
                pivotOutward(); 
            } else {
                pivotStop();
                setUpMidCount++; 
            }
            break; 

            case 1: 
            //elevator extend 
            if (!topLimitTouched() || upwardLimitReached()) {
                elevExtend();
            } else {
                if (!topLimitTouched()) {
                    extendSlow();
                } else {
                    elevatorStop();
                    setUpMidCount++; 
                }
            }
            break; 

            case 2: 
            // elevator retract 
            if (!bottomLimitTouched() || downwardLimitReached()) {
                elevRetract();
            } else {
                if(!bottomLimitTouched()) {
                    retractSlow();
                } else {
                    elevatorStop();
                    setUpMidCount++; 
                }
            }
            break; 

            case 3: 
            //pivot to mid
            if (!midLimitReached() ) {
                pivotInward();
            } else {
                pivotStop();
                setUpMidCount++; 
            }
            break; 

            case 4: 
            //elevator extend
            if (!topLimitTouched()|| !upwardLimitReached()) {
                elevExtend();
            }
            else {

                if(!topLimitTouched()){
                    extendSlow();
                }

                else{
                    elevatorStop();
                }
                
            }
            break; 
        }
    }

    private void highHangGrab(){
        switch(setUpHighCount){
            case 0: 
            // extend elevator (some)
            if (!topLimitTouched() && !upwardLimitReached()) {  
                elevExtend(); 
            } else {
                elevatorStop(); 
                setUpHighCount++; 
            }
            break; 

            case 1: 
            //pivot inwards 
            if (!inwardLimitReached() || !frontLimitTouched()){
                pivotInward();
            }

            else{
                pivotStop();
                setUpHighCount++;
            }
            break; 

            case 2: 
            //elevator extend 
            if (!topLimitTouched() || !upwardLimitReached()) {
                elevExtend();
            } 
            
            else {
                if(!topLimitTouched()){
                    extendSlow();
                }

                else{
                    elevatorStop();
                    setUpHighCount++;
                }
                
            }
            break; 

            case 3: 
            //pivot outwards
            if (!backLimitTouched() && pivotEncoder.get() < highHangGrab) {
                pivotOutward();
            } 
            else {
                pivotStop();
                setUpHighCount++; 
            }
            break; 

            case 4: 
            //elevator retract 
            if (!bottomLimitTouched() && !downwardLimitReached()) {
                elevRetract();
            } 
            else {
                elevatorStop();
            }
            break; 
        }
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

        SmartDashboard.putNumber("ELEVATOR ENCODER", elevatorEncoder.get());
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
            elevExtendLim(); 
            break; 

            case RETRACT:
            elevRetractLim();
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
