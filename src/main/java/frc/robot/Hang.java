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

    //COUNTERS AND OTHER VARIABLES
    public int setUpMidCount = 0;
    private int setUpHighCount = 0; 

    /////////////////////////////////////////////
    //                                         //
    //              CONSTRUCTOR                //
    //                                         //
    /////////////////////////////////////////////

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

    private void testing(){

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
                    elevator.setElevatorExtendSlow();
                } else {
                    elevator.setElevatorStop();
                    setUpMidCount++; 
                }
            }
            break; 

            case 2: 
            // elevator retract 
            if (!elevator.bottomLimitTouched() || elevator.botEncoderLimitReached()) {
                elevator.setElevatorRetract();
            } else {
                if(!elevator.bottomLimitTouched()) {
                    elevator.setElevatorRetractSlow();
                } else {
                    elevator.setElevatorStop();
                    setUpMidCount++; 
                }
            }
            break; 

            case 3: 
            //pivot to mid
            if (!pivot.middleEncReached() ) {
                pivot.setPivInward();
            } else {
                pivot.setStop();
                setUpMidCount++; 
            }
            break; 

            case 4: 
            //elevator extend
            if (!elevator.topLimitTouched()|| !elevator.topEncoderLimitReached()) {
                elevator.setElevatorExtend();
            }
            else {

                if(!elevator.topLimitTouched()){
                    elevator.setElevatorExtendSlow();
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
                elevator.setElevatorExtend(); 
            } else {
                elevator.setElevatorStop(); 
                setUpHighCount++; 
            }
            break; 

            case 1: 
            //pivot inwards 
            if (!pivot.inwardEncReached() || !pivot.frontLimitTouched()){
                pivot.setPivInward();
            }

            else{
                pivot.setStop();
                setUpHighCount++;
            }
            break; 

            case 2: 
            //elevator extend 
            if (!elevator.topLimitTouched() || !elevator.topEncoderLimitReached()) {
                elevator.setElevatorExtend();
            } 
            
            else {
                if(!elevator.topLimitTouched()){
                    elevator.setElevatorExtendSlow();
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
                pivot.setPivOutward();
            } 
            else {
                pivot.setStop();
                setUpHighCount++; 
            }
            break; 

            case 4: 
            //elevator retract 
            if (!elevator.bottomLimitTouched() && !elevator.botEncoderLimitReached()) {
                elevator.setElevatorRetract();
            } 
            else {
                elevator.setElevatorStop();
            }
            break; 
        }
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
