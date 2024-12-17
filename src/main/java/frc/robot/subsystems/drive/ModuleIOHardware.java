package frc.robot.subsystems.drive;
import frc.robot.Constants.DriveConstants.ModuleConstants.ModuleConfig;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.RelativeEncoder;
import com.ctre.phoenix6.hardware.CANcoder;
/*Designed for Bebop (Revrobotics SparkMAX + CTRE CANcoder)*/
public class ModuleIOHardware implements ModuleIO {
    
    private final SparkMax m_turnMotor;
    private final SparkClosedLoopController TurnPID;
    private final RelativeEncoder TurnRelEncoder;

    private final SparkMax m_driveMotor;
    private final SparkClosedLoopController DrivePID;
    private final RelativeEncoder DriveRelEncoder;

    private final CANcoder m_turnEncoder;

    public ModuleIOHardware(ModuleConfig config){

        m_turnMotor = new SparkMax(config.TurnPort,MotorType.kBrushless);
        TurnPID = m_turnMotor.getClosedLoopController();
        TurnRelEncoder = m_turnMotor.getEncoder();

        m_driveMotor = new SparkMax(config.DrivePort,MotorType.kBrushless);
        DrivePID = m_driveMotor.getClosedLoopController();
        DriveRelEncoder = m_driveMotor.getEncoder();

        m_turnEncoder = new CANcoder(config.EncoderPort);
        
    }
    
}
