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
    //ELEVATOR
    private HangElevator elevator;

    //PIVOT
    private HangPivot pivot;

    /*
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

    */

    //COUNTERS AND OTHER VARIABLES
    public int setUpMidCount = 0;
    private int setUpHighCount = 0; 

    /////////////////////////////////////////////
    //                                         //
    //              CONSTRUCTOR                //
    //                                         //
    /////////////////////////////////////////////
/*
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
    */

    public Hang(HangPivot Pivot, HangElevator Elevator){
        elevator = Elevator;
        pivot = Pivot;
    }


    /////////////////////////////////////////////
    //                                         //
    //               ENUMERATIONS              //
    //                                         //
    /////////////////////////////////////////////

    //PIVOT ENUMERATIONS
    private enum hangStates{
        MIDHANG, HIGHHANG, PIVOTMANUAL, ELEVATORMANUAL, TESTING, NOTHING
    }
    
    private hangStates hangMode = hangStates.NOTHING; 

    public void setMidHang() {
        hangMode = hangStates.MIDHANG; 
    }

    public void setHighHang() {
        hangMode = hangStates.HIGHHANG; 
    }

    public void setPivotManual() {
        hangMode = hangStates.PIVOTMANUAL; 
    }

    public void setElevatorManual() {
        hangMode = hangStates.ELEVATORMANUAL; 
    }

    public void setTesting() {
        hangMode = hangStates.TESTING; 
    }

    public void setNothing() {
        hangMode = hangStates.NOTHING; 
    }

    /////////////////////////////////////////////
    //                                         //
    //                 METHODS                 //
    //                                         //
    /////////////////////////////////////////////

    public void resetCounters(){
        setUpMidCount = 0;
        setUpHighCount = 0; 
    }    

    private void midHangGrab() {
        switch(setUpMidCount) {
            case 0: 
            //pivot outward 
            if ((pivot.backLimitTouched() || pivot.outwardEncReached())) {
                pivot.setStop();
                setUpMidCount++; 
            } else {
                pivot.setPivOutward(); 
            }
            break; 

            case 1: 
            //elevator extend 
            if (elevator.topLimitTouched() || elevator.topEncoderLimitReached()) {
                elevator.setElevatorExtend();
            } else {
                if (!elevator.topLimitTouched()) {
                    elevator.extendSlow();
                } else {
                    elevator.setElevatorStop();
                    setUpMidCount++; 
                }
            }
            break; 

            case 2: 
            // elevator retract 
            if (!elevator.bottomLimitTouched() || elevator.botEncoderLimitReached()) {
                elevator.elevRetract();
            } else {
                if(!elevator.bottomLimitTouched()) {
                    elevator.retractSlow();
                } else {
                    elevator.setElevatorStop();
                    setUpMidCount++; 
                }
            }
            break; 

            case 3: 
            //pivot to mid
            if (!pivot.middleEncReached() ) {
                pivot.pivotInward();
            } else {
                pivot.stopPivot();
                setUpMidCount++; 
            }
            break; 

            case 4: 
            //elevator extend
            if (!elevator.topLimitTouched()|| !elevator.topEncoderLimitReached()) {
                elevator.elevExtend();
            }
            else {

                if(!elevator.topLimitTouched()){
                    elevator.extendSlow();
                }

                else{
                    elevator.setElevatorStop();
                }
                
            }
            break; 
        }
    }

    private void highHangGrab(){
        switch(setUpHighCount){
            case 0: 
            // extend elevator (some)
            if (!elevator.topLimitTouched() && !elevator.topEncoderLimitReached()) {  
                elevator.elevExtend(); 
            } else {
                elevator.setElevatorStop(); 
                setUpHighCount++; 
            }
            break; 

            case 1: 
            //pivot inwards 
            if (!pivot.inwardEncReached() || !pivot.frontLimitTouched()){
                pivot.pivotInward();
            }

            else{
                pivot.stopPivot();
                setUpHighCount++;
            }
            break; 

            case 2: 
            //elevator extend 
            if (!elevator.topLimitTouched() || !elevator.topEncoderLimitReached()) {
                elevator.elevExtend();
            } 
            
            else {
                if(!elevator.topLimitTouched()){
                    elevator.extendSlow();
                }

                else{
                    elevator.setElevatorStop();
                    setUpHighCount++;
                }
                
            }
            break; 

            case 3: 
            //pivot outwards
            if (!pivot.backLimitTouched() && pivot.isGrabbingHigh()) {
                pivot.pivotOutward();
            } 
            else {
                pivot.stopPivot();
                setUpHighCount++; 
            }
            break; 

            case 4: 
            //elevator retract 
            if (!elevator.bottomLimitTouched() && !elevator.botEncoderLimitReached()) {
                elevator.elevRetract();
            } 
            else {
                elevator.setElevatorStop();
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
        
        SmartDashboard.putNumber("MID HANG COUNTER", setUpMidCount); 
        SmartDashboard.putNumber("HIGH HANG COUNTER", setUpHighCount);

        pivot.run(); 
        elevator.run();
    }
}
