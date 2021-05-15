package edu.anonymous.method;

public enum SensorType {

    TYPE_ACCELEROMETER("TYPE_ACCELEROMETER", 1),
    TYPE_MAGNETIC_FIELD("TYPE_MAGNETIC_FIELD", 2),
    TYPE_ORIENTATION("TYPE_ORIENTATION", 3),
    TYPE_GYROSCOPE("TYPE_GYROSCOPE", 4),
    TYPE_LIGHT("TYPE_LIGHT", 5),
    TYPE_PRESSURE("TYPE_PRESSURE", 6),
    TYPE_TEMPERATURE("TYPE_TEMPERATURE", 7),
    TYPE_PROXIMITY("TYPE_PROXIMITY", 8),
    TYPE_GRAVITY("TYPE_GRAVITY", 9),
    TYPE_LINEAR_ACCELERATION("TYPE_LINEAR_ACCELERATION", 10),
    TYPE_ROTATION_VECTOR("TYPE_ROTATION_VECTOR", 11),
    TYPE_RELATIVE_HUMIDITY("TYPE_MAGNETIC_FIELD", 12),
    TYPE_AMBIENT_TEMPERATURE("TYPE_AMBIENT_TEMPERATURE", 13),
    TYPE_MAGNETIC_FIELD_UNCALIBRATED("TYPE_MAGNETIC_FIELD_UNCALIBRATED", 14),
    TYPE_GAME_ROTATION_VECTOR("TYPE_GAME_ROTATION_VECTOR", 15),
    TYPE_GYROSCOPE_UNCALIBRATED("TYPE_GYROSCOPE_UNCALIBRATED", 16),
    TYPE_SIGNIFICANT_MOTION("TYPE_SIGNIFICANT_MOTION", 17),
    TYPE_STEP_DETECTOR("TYPE_STEP_DETECTOR", 18),
    TYPE_STEP_COUNTER("TYPE_STEP_COUNTER", 19),
    TYPE_GEOMAGNETIC_ROTATION_VECTOR("TYPE_GEOMAGNETIC_ROTATION_VECTOR", 20),
    TYPE_HEART_RATE("TYPE_HEART_RATE", 21),
    TYPE_TILT_DETECTOR("TYPE_TILT_DETECTOR", 22),
    TYPE_WAKE_GESTURE("TYPE_MAGNETIC_FIELD", 23),
    TYPE_GLANCE_GESTURE("TYPE_MAGNETIC_FIELD", 24),
    TYPE_PICK_UP_GESTURE("TYPE_PICK_UP_GESTURE", 25),
    TYPE_WRIST_TILT_GESTURE("TYPE_WRIST_TILT_GESTURE", 26),
    TYPE_DEVICE_ORIENTATION("TYPE_DEVICE_ORIENTATION", 27),
    TYPE_POSE_6DOF("TYPE_POSE_6DOF", 28),
    TYPE_STATIONARY_DETECT("TYPE_STATIONARY_DETECT", 29),
    TYPE_MOTION_DETECT("TYPE_MOTION_DETECT", 30),
    TYPE_HEART_BEAT("TYPE_HEART_BEAT", 31),
    TYPE_DYNAMIC_SENSOR_META("TYPE_DYNAMIC_SENSOR_META", 32),
    TYPE_ALL("TYPE_ALL", -1),
    ;

    private String sensorName;
    private int sensorNo;

    SensorType(String sensorName, int sensorNo) {
        this.sensorName = sensorName;
        this.sensorNo = sensorNo;
    }

    public static SensorType getSensorType(int sensorNo) {
        for (SensorType sensorType : SensorType.values()) {
            if (sensorType.sensorNo == sensorNo) return sensorType;
        }
        throw new IllegalArgumentException("SensorType not found. SensorNo:"+sensorNo);
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public int getSensorNo() {
        return sensorNo;
    }

    public void setSensorNo(int sensorNo) {
        this.sensorNo = sensorNo;
    }
}

