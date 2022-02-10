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
            //resets encoder
            pivot.resetEnc();
            elevator.encoderReset();
            setUpMidCount++;
            break;

            case 1: 
            //pivot outward (to set up angle for mid rung grab)
            if ((pivot.backLimitTouched() || pivot.outwardEncReached())) {      //if the back limit of pivot is touched OR back enc. limit is reached, STOP
                pivot.setStop();
                setUpMidCount++; 
            } else {                                                            //else, pivot outward
                pivot.setPivOutward(); 
            }
            break; 

            case 2: 
            //elevator extend (all the way to the top)
            if (elevator.topLimitTouched()) {      //if the top limit of elevator is touched || enc limit is reached, STOP
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

            case 3: 
            //add a delay in between, to allow drivers to choose when to retract
            timer.start(); 
            if (timer.get() >= 5) {     //if timer > 5, stop the timer
                timer.stop(); 
                setUpMidCount++; 
            }
            break;  

            case 4: 
            // elevator retract (pulls all the way up)
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

            case 5: 
            // pivot to mid (to place pivot hook above mid rung)
            if(pivot.middleEncReached()){       //if middle encounter count is reached, stop
                pivot.setStop();       
                setUpMidCount++;
            }
            else{
                pivot.setPivInward();       //else pivot inward
            }
            break; 

/*          QUESTIONABLE IF THE DRIVERS WANT IT, BUT WE HAVE TO TALK WITH THEM :)
            case 6:
            // elevator extends (to secure pivot hook)
            if(!elevator.botEncoderLimitReached()){
                elevator.setElevatorStop();
                setUpMidCount++;
            }
            else{
                elevator.setElevatorExtendSlow();
            }
*/
            case 6: 
            timer.reset();  //resets timer
            break; 
        }
    }

    private void highHangSetup(){
        switch(setUpHighCount){
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
                }
            }
            break; 
        }

            
    }

    private void highHangGrab(){
        switch(setUpHighGrabCount){
    /*        case 0:
            //retract elevator
            if(!elevator.botEncoderLimitReached()){
                elevator.setElevatorRetract();
            }

            else{
                elevator.setElevatorRetractSlow();
                setUpHighGrabCount++;
            }*/
            
            case 0:
            if(elevator.pivotableEncoderReached()){
                pivot.setStop();
                elevator.setElevatorRetractSlow();
            }

            else{
                if(pivot.outwardEncReached()){
                pivot.setStop();
                setUpHighGrabCount++;
                }
                else{
                pivot.setPivOutward();
                }

                if(elevator.bottomLimitTouched()){
                elevator.setElevatorStop();
                }
                else{
                elevator.setElevatorRetractSlow();
                }
            }
            break;
/*
            case 1:
            if(!pivot.middleEncReached()){
                pivot.setPivInward();
            }

            else{
                pivot.setStop();
                setUpHighGrabCount++;
            }
            break;
        */
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
            break;

            case PIVOTMANUAL:
            testing();
            break;

            case ELEVATORMANUAL:
            testing();
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
