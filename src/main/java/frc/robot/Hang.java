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
    public int setUpMidCount = 0;
    private int setUpHighCount = 0; 

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
                if (!elevator.topEncoderLimitReached()) {                                      //else if top limit isn't touched but close to top, extend slowly 
                    elevator.setElevatorExtend();
                } else {
                    elevator.setElevatorExtendSlow();                                         // else extend at normal speed 
                }
            }
            break; 

            case 2: 
            timer.start(); 
            if (timer.get() >= 5) {
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
            if(pivot.middleEncReached()){
                pivot.setStop();
                setUpMidCount++;
            }
            else{
                pivot.setPivInward();
            }
            
            break; 

            case 5:
            if (elevator.topLimitTouched()) {      //If the top limit of elevator is touched || enc limit is reached, STOP
                elevator.setElevatorStop();
                setUpMidCount++; 
            } else {
                if (!elevator.topEncoderLimitReached()) {                                      //else if top limit isn't touched but close to top, extend slowly 
                    elevator.setElevatorExtend();
                } else {
                    elevator.setElevatorExtendSlow();                                         // else extend at normal speed 
                }
            }
            break; 

            case 6: 
            timer.reset(); 
            break; 
        }
    }

    private void highHangGrab(){
        switch(setUpHighCount){
            case 0: 
            // extend elevator (some)
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
/*
            case 3: 
            //pivot outwards
            if (!pivot.backLimitTouched() && pivot.isGrabbingHigh()) {       // if back limit isn't touched or encoder limit for grabbing high rung isnt reached 
                pivot.setPivOutward();                                       // pivot outward 
            } 
            else {
                pivot.setStop();                                            // else stop 
                setUpHighCount++; 
            }
            break; 

            case 4: 
            //elevator retract 
            if (!elevator.bottomLimitTouched() && !elevator.botEncoderLimitReached()) {   // if neither bottom limit is reached 
                elevator.setElevatorRetract();                                            // retract at normal speed 
            }  
            else {
                if(!elevator.bottomLimitTouched()) {                                    // else if close to bottom limit 
                    elevator.setElevatorRetractSlow();                                  // retract slowly 
                } else {
                    elevator.setElevatorRetract();                                      // else retract at normal speed 
                }                                           
            }
            break; 
            */
        }   
    

    public void manualPivot(double pivSpeed){
        pivot.setTesting();
        pivot.manualPivot(pivSpeed);
    }

    public void manualElevator(double elevSpeed){
        elevator.setElevatorTest();
        elevator.manualElev(elevSpeed);
    }

    private void stop(){
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
            highHangGrab();
            break;

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
