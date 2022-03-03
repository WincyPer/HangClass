package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public class WeightShifter {

    // ANGLING FOR HIGHER BARS REQUIRES FOR WEIGHTSHIFTER TO GO INWARD. BEFORE SHIFTING WEIGHT (FOR THE ANGLE), MAKE SURE ELEV IS FULLY RETRACTED
    // PULLING OFF FROM LOWER BARS REQUIRES FOR WEIGHTSHIFTER TO GO OUTWARD

    //ASSUME GOING UP IS POSITIVE

    private MotorController weightAdjuster;
    private TalonEncoder weightEncoder;

    private double weightSpeedUp = 0.20;               //speed going up
    private double weightSpeedDown = -0.20;            //speed going down

    private double weightMaxUp = 0;            //encoder count for the most up it can be
    private double weightMaxDown = 0;          //encoder count for the farthest down it can be

    public WeightShifter(MotorController WeightShifter, TalonEncoder shifterEnc){
        weightAdjuster = WeightShifter;
        weightEncoder = shifterEnc;
    }

    private enum States{
        UP, DOWN, HOME, STOP;
    }

    public States weightShifterState = States.STOP;

    public void setWeightHome(){
        weightShifterState = States.HOME;
    }

    public void setWeightUp(){
        weightShifterState = States.UP;
    }

    public void setWeightDown(){
        weightShifterState = States.DOWN;
    }

    public void setWeightStop(){
        weightShifterState = States.STOP;
    }

    private void weightUp(){
        if(weightEncoder.get() <= weightMaxUp){
            weightAdjuster.set(weightSpeedUp);
        }

        else{
            weightAdjuster.set(0);
        }
    }

    private void weightDown(){
        if(weightEncoder.get() >= weightMaxDown){
        weightAdjuster.set(weightSpeedDown);
        }

        else{
            weightAdjuster.set(0);
        }
    }

    private void weightHome(){
        if(weightEncoder.get() < -50){
            weightAdjuster.set(weightSpeedUp);
        }

        else if(weightEncoder.get() > 50){
            weightAdjuster.set(weightSpeedDown);
        }

        else{
            weightAdjuster.set(0);
        }
    }

    private void stop(){
        weightAdjuster.set(0);
    }

    public void run(){
        switch(weightShifterState){

            case UP:
            weightUp();
            break;

            case DOWN:
            weightDown();
            break;

            case HOME:
            weightHome();
            break;

            case STOP:
            stop();
            break;

        }

    }
}
