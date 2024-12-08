package gg.mineral.api.math;

public interface MathUtil {
    default int toFixedPointInt(double value) {
        return (int) (value * 32);
    }

    default byte toFixedPointByte(double value) {
        return (byte) (value * 32);
    }

    default int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    default int ceil(final double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
    }

    default int round(double num) {
        return floor(num + 0.5d);
    }

    default double square(double num) {
        return num * num;
    }

    default double sin(double num) {
        return Math.sin(num);
    }

    default double cos(double num) {
        return Math.cos(num);
    }

    default float toRadians(float angle) {
        return (float) Math.toRadians(angle);
    }

    default int toInt(Object object) {
        if (object instanceof Number number)
            return number.intValue();

        if (object == null)
            return 0;

        try {
            return Integer.valueOf(object.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    default float toFloat(Object object) {
        if (object instanceof Number number)
            return number.floatValue();

        if (object == null)
            return 0;

        try {
            return Float.valueOf(object.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    default double toDouble(Object object) {
        if (object instanceof Number number)
            return number.doubleValue();

        if (object == null)
            return 0;

        try {
            return Double.valueOf(object.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    default long toLong(Object object) {
        if (object instanceof Number number)
            return number.longValue();

        if (object == null)
            return 0;

        try {
            return Long.valueOf(object.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    default short toShort(Object object) {
        if (object instanceof Number number)
            return number.shortValue();

        if (object == null)
            return 0;

        try {
            return Short.valueOf(object.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    default byte toByte(Object object) {
        if (object instanceof Number number)
            return number.byteValue();

        if (object == null)
            return 0;
        try {
            return Byte.valueOf(object.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    default byte angleToByte(float angle) {
        return (byte) (angle * 256 / 360);
    }

    default short toVelocityUnits(double value) {
        return (short) (value * 8000D);
    }

    default double fromVelocityUnits(short value) {
        return value / 8000D;
    }

    default int toSoundUnits(double value) {
        return (int) (value * 8);
    }

    default double fromSoundUnits(int value) {
        return value / 8D;
    }

    default short toPitchUnits(double value) {
        return (short) (value * 63);
    }

    default double fromPitchUnits(short value) {
        return value / 63D;
    }

    default int unsigned(short s) {
        return s & 0xFFFF;
    }
}
