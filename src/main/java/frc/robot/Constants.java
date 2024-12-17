// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static class OIConstants {
        public static class Driver {
            public static final int kDriverControllerPort = 0;
        }
    }

    public static class Akit {
        public static final int currentMode = 0;
    }

    public static class DriveConstants {
        public static final int odometryFrequencyHz = 250;
        public static class GyroConstants {
            public static final int kGyroPort = -1;
        }
        public static class ModuleConstants {
            public static class Common {
                public static class Drive {
                    public static final int CurrentLimit = 60;
                    public static final double VoltageCompensation = 12;
                    public static final double PositionConversionFactor = 1.0;
                    public static final double VelocityConversionFactor = 1.0;

                    //PID constants
                    public static final double kP = 0.1;
                    public static final double kI = 0.0;
                    public static final double kD = 0.0;

                    //Feedforward constants
                    public static final double kV = 0.0;
                    public static final double kS = 0.0;
                    public static final double kA = 0.0;
                }
    
                public static class Turn {
                    public static final int CurrentLimit = 60;
                    public static final double VoltageCompensation = 12;
                    public static final double PositionConversionFactor = 1.0;
                    public static final double VelocityConversionFactor = 1.0;

                    //PID constants
                    public static double kP = 0.1;
                    public static final double kI = 0.0;
                    public static final double kD = 0.0;
                }
            }
            public static enum ModuleConfig {
    
                FrontLeft(1,11,21,0.0,"FrontLeftModule"),
                FrontRight(2,12,22,0.0,"FrontRightModule"),
                RearLeft(3,13,23,0.0,"RearLeftModule"),
                RearRight(4,14,24,0.0,"RearRightModule");
    
                public final int DrivePort;
                public final int TurnPort;
                public final int EncoderPort;
                public final double EncoderOffsetRots;
                public final String ID;
    
                private ModuleConfig(int DrivePort, int TurnPort,int EncoderPort,double EncoderOffsetRots,String ID) {
                    this.DrivePort = DrivePort;
                    this.TurnPort = TurnPort;
                    this.EncoderPort = EncoderPort;
                    this.EncoderOffsetRots = EncoderOffsetRots;
                    this.ID = ID;
                }
            }
        }
    }
}