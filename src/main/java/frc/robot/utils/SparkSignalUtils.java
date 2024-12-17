package frc.robot.utils;

import java.util.OptionalDouble;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.SparkMax;

public class SparkSignalUtils {
    /**Returns a spark max error signal. WARNING: NOT THREAD-SAFE, LOCKING MUST BE IMPLEMENTED IN THE CALLING THREAD TO PREVENT RACE CONDITIONS*/
    public BooleanSupplier getSparkMaxErrorSignal(SparkMax motor){
        return () -> (motor.getLastError() != REVLibError.kOk);
    }
}
