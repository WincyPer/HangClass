package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Timer;

public class Hang {
    //MASTER
    /////////////////////////////////////////////
    //                                         //
    //                VARIABLES                //
    //                                         //
    ///////////////////////////////////////////// 
    
    //ELEVATOR
    private HangElevator elevator;

    //PIVOT
    private HangPivot pivot;

    //COUNTERS AND OTHER VARIABLES
    private int setUpMidCount = 0;
    private int setUpHighCount = 0;
    private int setUpHighGrabCount = 0;


    private Timer timer;

    /////////////////////////////////////////////
    //                                         //
    //              CONSTRUCTOR                //
    //                                         //
    /////////////////////////////////////////////

    public Hang (HangPivot Pivot, HangElevator Elevator){
        elevator = Elevator;
        pivot = Pivot;
        timer = new Timer();
    }

    /////////////////////////////////////////////
    //                                         //
    //               ENUMERATIONS              //
    //                                         //
    /////////////////////////////////////////////

    //PIVOT ENUMERATIONS
    private enum hangStates{
        MIDHANG, HIGHHANG, HIGHHANGGRAB, PIVOTMANUAL, ELEVATORMANUAL, TESTING, NOTHING
    }
    
    private hangStates hangMode = hangStates.NOTHING; 

    public void setMidHang() {
        hangMode = hangStates.MIDHANG; 
    }

    public void setHighHang() {
        hangMode = hangStates.HIGHHANG; 
    }

    public void setHighHangGrab(){
        hangMode = hangStates.HIGHHANGGRAB;
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
        setUpHighGrabCount = 0;
    }    

    private void testing(){}

    private void midHangGrab() {
        switch(setUpMidCount) {
            case 0: 
            //pivot outward 
            if ((pivot.backLimitTouched() || pivot.outwardEncReached())) {      //If the back limit of pivot is touched OR back enc. limit is reached, STOP
                pivot.setStop();
                setUpMidCount++; 
            } else {                                                            //Else, pivot outward
                pivot.setPivOutward(); 
            }
            break; 

            case 1: 
            //elevator extend 
            if (elevator.topLimitTouched()) {      //If the top limit of elevator is touched || enc limit is reached, STOP
                elevator.setElevatorStop();
                setUpMidCount++; 
            } else {
                if (!elevator.topEncoderLimitReached()) {                                      //else if top encoder isnt reached, extend at normal rate
                    elevator.setElevatorExtend();
                } else {
                    elevator.setElevatorExtendSlow();                                         // else extend at normal speed 
                }
            }
            break; 

            case 2: 
            timer.start(); 
            if (timer.get() >= 5) {     //WHEN TIMER > 5, STOP AND MOVE ON
                timer.stop(); 
                setUpMidCount++; 
            }
            break;  

            case 3: 
            // elevator retract 
            timer.reset(); 
            if (elevator.bottomLimitTouched()) {   // if bottom limit is touched || bottom encoder limit is reached, 
                elevator.setElevatorStop();   
                setUpMidCount++;                                           // stop
            } 
            else {
                if(!elevator.botEncoderLimitReached()) {                                    // else if close to bottom limit 
                    elevator.setElevatorRetract();                                  // retract slowly 
                } else {
                    elevator.setElevatorRetractSlow();                                      // else retract at normal speed 
                }
            }
            break; 

            case 4: 
            // pivot to mid
            if(pivot.middleEncReached()){       //if middle encounter count is reached, stop
                pivot.setStop();       
                setUpMidCount++;
            }
            else{
                pivot.setPivInward();       //else pivot inward
            }
            
            break; 

            case 5: 
            timer.reset(); 
            break; 
        }
    }

    private void highHangSetup(){
        switch(setUpHighGrabCount){
            case 0: 
            // extend elevator (to a certain encoder extent)
            if (elevator.topEncoderLimitReached()) {    // if top limit or small encoder limit isn't reached
                elevator.setElevatorStop(); 
                setUpHighCount++;                                            // extend at a normal speed 
            } else {
                elevator.setElevatorExtend();                                             // else stop
            }
            break; 

            case 1: 
            //pivot inwards 
            if (pivot.inwardEncReached() || pivot.frontLimitTouched()){           // if neither inward limit is reached 
                pivot.setStop();
                setUpHighCount++;                                                // pivot inward 
            }
            else{
                pivot.setPivInward();                                                    // else stop 
            }
            break; 

            case 2: 
            //elevator extend 
            if (elevator.topLimitTouched()) {                                    // if neither top limit is reached 
                elevator.setElevatorStop();                                          // extend at normal speed 
            } 
            else {
                if(!elevator.topEncoderLimitReached()){                                      // else if close to top limit 
                    elevator.setElevatorExtendSlow();                                 // extend slowly 
                }
                else{
                    elevator.setElevatorExtend();                                       //else stop
                    setUpHighCount++;
                }
                
            }
            break; 
        }
            
    }

    private void highHangGrab(){
        switch(setUpHighGrabCount){
            case 0:
            if(!elevator.botEncoderLimitReached()){
                elevator.setElevatorRetract();
            }

            else{
                elevator.setElevatorRetractSlow();
                setUpHighGrabCount++;
            }

            case 1:
            //if(pivot.backLimitTouched()){
            if(!elevator.pivotableEncoderReached()){
                pivot.setStop();
                elevator.setElevatorRetractSlow();
                setUpHighGrabCount++;
            }
            else{
                if(pivot.outwardEncReached()){
                pivot.setStop();
                }
                else{
                    pivot.setPivOutward();
                }
                if(elevator.botEncoderLimitReached()){
                    elevator.setElevatorRetractSlow();
                }
                else{
                    elevator.setElevatorRetract();
                }
            } 

        }
    }
    

    public void manualPivot(double pivSpeed){
        pivot.setTesting();     //SETS PIVOT STATE TO TESTING
        pivot.manualPivot(pivSpeed);
    }

    public void manualPivotButton(boolean buttonIn, boolean buttonOut){
        pivot.setTesting();     //SETS PIVOT STATE TO TESTING

        if(buttonIn){
            pivot.pivotInward();        //PIVOT INWARD WHEN GIVEN BUTTON IS PRESSED
        }
        else if(buttonOut){
            pivot.pivotOutward();       //PIVOT OUTWARD WHEN GIVEN BUTTON IS PRESSED
        }
        else{
            pivot.setStop();
        }
    }

    public void manualElevator(double elevSpeed){
        elevator.setElevatorTest();     //SETS PIVOT STATE TO TESTING
        elevator.manualElev(elevSpeed);
    }

    public void manualElevatorButton(boolean buttonExtend, boolean buttonRetract) {
        elevator.setElevatorTest();     //SETS ELEVATOR STATE TO TESTING
       
        if (buttonExtend) {     
            elevator.setElevatorExtend();       //EXTENDS WHEN GIVEN BUTTON IS PRESSED
        } else if (buttonRetract) {
            elevator.setElevatorRetract();      //RETRACTS WHEN GIVEN BUTTON IS PRESSED
        } else {
            elevator.setElevatorStop();     
        }
    }

    private void stop(){        //STOPS ELEVATOR AND PIVOT
        elevator.setElevatorStop();
        pivot.setStop();
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
        SmartDashboard.putString("HANG STATE", hangMode.toString());
        SmartDashboard.putNumber("TIMER", timer.get()); 

        switch(hangMode){
            case MIDHANG:
            midHangGrab();
            break;

            case HIGHHANG:
            highHangSetup();
            break;

            case HIGHHANGGRAB:
            highHangGrab();

            case PIVOTMANUAL:
            break;

            case ELEVATORMANUAL:
            break;

            case TESTING:
            testing();
            break;

            case NOTHING:
            stop();
            break;

        }

        pivot.run(); 
        elevator.run();

    }
}
