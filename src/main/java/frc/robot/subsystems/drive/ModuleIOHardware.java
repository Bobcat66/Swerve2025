package frc.robot.subsystems.drive;
import frc.robot.Constants.DriveConstants.ModuleConstants.ModuleConfig;
import frc.robot.Constants.DriveConstants.ModuleConstants.Common.Drive;
import frc.robot.Constants.DriveConstants.ModuleConstants.Common.Turn;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import frc.robot.utils.SparkSignalUtils;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import java.util.Queue;
import static frc.robot.Constants.DriveConstants.odometryFrequencyHz;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANcoderConfiguration;

/*Designed for Bebop (Revrobotics SparkMAX + CTRE CANcoder)*/
public class ModuleIOHardware implements ModuleIO {
    
    private final SparkMax m_turnMotor;
    private final SparkClosedLoopController TurnPID;
    private final RelativeEncoder TurnRelEncoder;

    private final SparkMax m_driveMotor;
    private final SparkClosedLoopController DrivePID;
    private final RelativeEncoder DriveRelEncoder;

    private final Queue<Long> timestampQueue;
    private final Queue<Double> drivePositionQueue;
    private final Queue<Double> turnPositionQueue;

    private final CANcoder m_turnEncoder;
    private final StatusSignal<Angle> turnAbsolutePosition;

    public ModuleIOHardware(ModuleConfig config){

        m_turnMotor = new SparkMax(config.TurnPort,MotorType.kBrushless);
        TurnPID = m_turnMotor.getClosedLoopController();
        TurnRelEncoder = m_turnMotor.getEncoder();

        m_driveMotor = new SparkMax(config.DrivePort,MotorType.kBrushless);
        DrivePID = m_driveMotor.getClosedLoopController();
        DriveRelEncoder = m_driveMotor.getEncoder();
    
        m_turnEncoder = new CANcoder(config.EncoderPort);

        //Config turn absolute encoder here
        CANcoderConfiguration encoderConfig = new CANcoderConfiguration();
        encoderConfig.FutureProofConfigs = false;
        encoderConfig.MagnetSensor.AbsoluteSensorDiscontinuityPoint = 1.0;
        encoderConfig.MagnetSensor.MagnetOffset = config.EncoderOffsetRots;
        m_turnEncoder.getConfigurator().apply(encoderConfig);
        turnAbsolutePosition = m_turnEncoder.getAbsolutePosition();

        SparkMaxConfig turnConfig = new SparkMaxConfig();
        turnConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(Turn.CurrentLimit)
            .voltageCompensation(Turn.VoltageCompensation);
        turnConfig
            .encoder
            .positionConversionFactor(Turn.PositionConversionFactor)
            .velocityConversionFactor(Turn.VelocityConversionFactor);
        turnConfig
            .closedLoop
            .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
            .p(Turn.kP)
            .i(Turn.kI)
            .d(Turn.kD)
            .positionWrappingEnabled(true)
            .positionWrappingInputRange(0,1);
        turnConfig
            .signals
            .primaryEncoderPositionAlwaysOn(true)
            .primaryEncoderPositionPeriodMs((int) (1000/odometryFrequencyHz))
            .primaryEncoderVelocityAlwaysOn(true)
            .primaryEncoderVelocityPeriodMs(20)
            .appliedOutputPeriodMs(20)
            .busVoltagePeriodMs(20)
            .outputCurrentPeriodMs(20);
        
        m_turnMotor.configure(turnConfig,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
        TurnRelEncoder.setPosition(turnAbsolutePosition.getValueAsDouble());
        SparkMaxConfig driveConfig = new SparkMaxConfig();
        driveConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(Drive.CurrentLimit)
            .voltageCompensation(Drive.VoltageCompensation);
        driveConfig
            .encoder
            .positionConversionFactor(Drive.PositionConversionFactor)
            .velocityConversionFactor(Drive.VelocityConversionFactor);
        driveConfig
            .closedLoop
            .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
            .p(Drive.kP)
            .i(Drive.kI)
            .d(Drive.kD);
        driveConfig
            .signals
            .primaryEncoderPositionAlwaysOn(true)
            .primaryEncoderPositionPeriodMs((int) (1000/odometryFrequencyHz))
            .primaryEncoderVelocityAlwaysOn(true)
            .primaryEncoderVelocityPeriodMs(20)
            .appliedOutputPeriodMs(20)
            .busVoltagePeriodMs(20)
            .outputCurrentPeriodMs(20);
        
        m_driveMotor.configure(driveConfig,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
        DriveRelEncoder.setPosition(0.0);
        
        timestampQueue = OdometryThread.getInstance().makeTimestampQueue();
        drivePositionQueue = OdometryThread.getInstance().registerSignal(SparkSignalUtils.getSparkMaxPositionSignal(m_driveMotor));
        OdometryThread.getInstance().registerErrorSignal(SparkSignalUtils.getSparkMaxErrorSignal(m_driveMotor));
        turnPositionQueue = OdometryThread.getInstance().registerSignal(SparkSignalUtils.getSparkMaxPositionSignal(m_turnMotor));
        OdometryThread.getInstance().registerErrorSignal(SparkSignalUtils.getSparkMaxErrorSignal(m_turnMotor));
    }

    @Override
    public void updateInputs(ModuleIOInputs inputs){
        DriveSubsystem.odometryLock.lock();
        try {
            inputs.drivePositionMeters = DriveRelEncoder.getPosition();
            inputs.driveVelocityMetersPerSec = DriveRelEncoder.getVelocity();
            inputs.driveAppliedVolts = m_driveMotor.getBusVoltage() * m_driveMotor.getAppliedOutput();
            inputs.driveCurrentAmps = m_driveMotor.getOutputCurrent();
        
            inputs.turnAbsolutePosition = new Rotation2d(turnAbsolutePosition.getValue());
            inputs.turnPosition = Rotation2d.fromRotations(TurnRelEncoder.getPosition());
            inputs.turnVelocityRPM = TurnRelEncoder.getVelocity();
            inputs.turnAppliedVolts = m_turnMotor.getBusVoltage() * m_turnMotor.getAppliedOutput();
            inputs.driveCurrentAmps = m_turnMotor.getOutputCurrent();

            inputs.odometryTimestamps = timestampQueue.stream().mapToDouble((Long value) -> value/1e6).toArray();
            inputs.odometryDrivePositionsMeters = drivePositionQueue.stream().mapToDouble((Double value) -> value).toArray();
            inputs.odometryTurnPositions = turnPositionQueue.stream().map((Double value) -> Rotation2d.fromRotations(value)).toArray(Rotation2d[]::new);
            timestampQueue.clear();
            drivePositionQueue.clear();
            turnPositionQueue.clear();
        } finally {
            DriveSubsystem.odometryLock.unlock();
        }
        

    }
    
}
