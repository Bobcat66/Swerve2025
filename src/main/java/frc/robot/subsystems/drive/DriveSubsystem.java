package frc.robot.subsystems.drive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class DriveSubsystem extends SubsystemBase {
    static final Lock odometryLock = new ReentrantLock();
}
