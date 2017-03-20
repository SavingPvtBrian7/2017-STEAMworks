package org.usfirst.frc.team4915.steamworks.commands.groups;

import org.usfirst.frc.team4915.steamworks.Logger;
import org.usfirst.frc.team4915.steamworks.OI;
import org.usfirst.frc.team4915.steamworks.commands.DriveCurveCommand;
import org.usfirst.frc.team4915.steamworks.commands.DelayCommand;
import org.usfirst.frc.team4915.steamworks.commands.DriveStraightCommand;
import org.usfirst.frc.team4915.steamworks.commands.DriveTimedCurveCommand;
import org.usfirst.frc.team4915.steamworks.commands.FastTurnDegreesIMUCommand;
import org.usfirst.frc.team4915.steamworks.commands.IntakeSetCommand;
import org.usfirst.frc.team4915.steamworks.commands.LauncherCommand;
import org.usfirst.frc.team4915.steamworks.commands.StopCommand;
import org.usfirst.frc.team4915.steamworks.commands.TurnDegreesIMUCommand;
import org.usfirst.frc.team4915.steamworks.subsystems.Drivetrain;
import org.usfirst.frc.team4915.steamworks.subsystems.Intake;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher;
import org.usfirst.frc.team4915.steamworks.subsystems.Launcher.LauncherState;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * This is a generic command group that should work for all autonomous programs
 * we want, you just specify a number of parameters as follows:
 * "Drive", "inches", "Turn", "degrees", "Shoot" "seconds"
 * Example: DriveCommandGroup(drivetrain, oi, "Drive", "96", "Turn", "135",
 * "Shoot", "7")
 */
public class ParameterizedCommandGroup extends CommandGroup
{

    private final Logger m_logger;
    /*
     * Please don't do it this way next year. 
     * See Jack's suggestion: https://github.com/Spartronics4915/2017-STEAMworks/pull/59#pullrequestreview-22671451
     */
    public ParameterizedCommandGroup(Drivetrain drivetrain, Launcher launcher, Intake intake, OI oi, String... params)
    {
        m_logger = new Logger("ParameterizedCommandGroup", Logger.Level.DEBUG);
        
        m_logger.debug(String.join(",", params));
        
        for (int i = 0; i < params.length; )
        {
            String command = params[i++];

            switch (command)
            {
                case "Drive":
                	double distance = safeParseDouble(params[i++]);
                    addSequential(new DriveStraightCommand(drivetrain, distance));
                    break;
                case "Drive Timeout":
                    distance = safeParseDouble(params[i++]);
                    double timeout = safeParseDouble(params[i++]);
                    addSequential(new DriveStraightCommand(drivetrain, distance), timeout);
                    break;
                case "Curve":
                	distance = safeParseDouble(params[i++]);
                	double curve = safeParseDouble(params[i++]);
                    addSequential(new DriveCurveCommand(drivetrain, distance, curve * oi.getAllianceScale()));
                    break;
                case "Turn":
                	double angle = safeParseDouble(params[i++]);
                    addSequential(new TurnDegreesIMUCommand(drivetrain, angle * oi.getAllianceScale()));
                    break;
                case "Turn Fast":
                    double value = safeParseDouble(params[i++]);
                    addSequential(new FastTurnDegreesIMUCommand(drivetrain, value * oi.getAllianceScale()));
                    break;
                case "Shoot":
                    addSequential(new LauncherCommand(launcher, LauncherState.ON, true)); // Takes no parameters
                    break;
                case "Stop":
                    addSequential(new StopCommand(drivetrain)); // Takes no parameters
                    break;
                case "Straight and Curve":
                    double totalDistance = safeParseDouble(params[i++]);
                    curve = safeParseDouble(params[i++]);
                    double straightDistance = safeParseDouble(params[i++]);
                    double speed = safeParseDouble(params[i++]);
                    addSequential(new DriveTimedCurveCommand(drivetrain, totalDistance, curve, straightDistance, speed));
                case "Intake": // This command runs in parallel
                    Intake.State intakeState;
                    String currentParam = params[i++];
                    if (currentParam == "ON") {
                        intakeState = Intake.State.ON;
                    } else if (currentParam == "REVERSE") {
                        intakeState = Intake.State.REVERSE;
                    } else {
                        intakeState = Intake.State.OFF;
                    }
                    addParallel(new IntakeSetCommand(intake, intakeState));
                case "Delay":
                    double delay = safeParseDouble(params[i++]);
                    addSequential(new DelayCommand(delay));
                    break;
                default:
                    drivetrain.m_logger.warning("ParameterizedCommandGroup Unrececognized parameter "+command);
                    break;
            }
        }
        addSequential(new StopCommand(drivetrain)); // This is so we stop and don't get yelled at by motor saftey when we're done
    }
    
    private double safeParseDouble(String doubleString) 
    {
        try
        {
            return Double.parseDouble(doubleString);
        }
        catch(Exception e)
        {
            m_logger.exception(e, false);
            return 0.0;
        }
    }
}
