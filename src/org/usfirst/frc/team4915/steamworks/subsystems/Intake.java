package org.usfirst.frc.team4915.steamworks.subsystems;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends SpartronicsSubsystem
{

    public static enum State
    {
        OFF,
        ON,
        REVERSE
    }

    private static final double INTAKE_SPEED = 0.75;

    private CANTalon m_intakeMotor;

    public Logger m_logger;

    public Intake()
    {
        m_logger = new Logger("Intake", Logger.Level.DEBUG);
        try
        {
            m_intakeMotor = new CANTalon(RobotMap.INTAKE_MOTOR);
            m_intakeMotor.changeControlMode(TalonControlMode.PercentVbus);
            m_logger.info("Intake initialized");
            SmartDashboard.putString("Intake Status", "Initialized");
        }
        catch (Exception e)
        {
            m_logger.exception(e, false);
            m_initialized = false;
            SmartDashboard.putString("Intake Status", "Error");
        }
    }
    
    @Override
    protected void initDefaultCommand()
    {

    }
    
    public double getIntakeSpeed(State s)
    {
        switch(s)
        {
            case ON:  return INTAKE_SPEED;
            case REVERSE: return -INTAKE_SPEED;
            case OFF: return 0;
        
        }
        return 0;
    }

    public String getStateString(State s)
    {
        switch(s)
        {
            case OFF: return "OFF";
            case ON: return "ON";
            case REVERSE: return "REVERSE";
        }
        return null;
    }

    public void setIntake(State state)  // this is called in execute, don't want to spew logs
    {
        if (initialized())
        {
            m_intakeMotor.set(getIntakeSpeed(state));
        }
    }
}
