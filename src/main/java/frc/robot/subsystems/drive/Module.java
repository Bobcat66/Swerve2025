package frc.robot.subsystems.drive;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Volts;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.subsystems.Drive.ModuleIOInputsAutoLogged;
import frc.robot.Constants.DriveConstants.ModuleConstants.Common.Drive;

public class Module {
    private final ModuleIO io;
    private final String ID;
    private final ModuleIOInputsAutoLogged inputs = new ModuleIOInputsAutoLogged();
    private final SimpleMotorFeedforward driveFFController;

    public Module(ModuleIO io, String ID){
        this.io = io;
        this.ID = ID;
        driveFFController = new SimpleMotorFeedforward(
            Drive.kS,
            Drive.kV,
            Drive.kA,
            0.2
        );
    }

    /*Sets desired state in closed-loop mode */
    public void setDesiredState(SwerveModuleState state){
        io.updateInputs(inputs); //Fetches latest data from IO layer
        state.optimize(inputs.turnPosition);
        io.setDriveVelocity(
            state.speedMetersPerSecond, 
            driveFFController.calculate(MetersPerSecond.of(state.speedMetersPerSecond)).in(Volts)
        );
        io.setTurnPosition(
            state.angle.getRotations(), 
            0
        );
    }

    /*Must be manually called by DriveSubsystem */
    public void periodic(){
        io.updateInputs(inputs);
        Logger.processInputs("Drive/" + ID, inputs);
    }
}
