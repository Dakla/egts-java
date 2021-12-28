package com.groupstp;

public class Crc {
    public static int crc16(Byte[] bytes) {
        int crc = 0xFFFF;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)

        // byte[] testBytes = "123456789".getBytes("ASCII");

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }

        crc &= 0xffff;

        return crc;
    }
    public static byte crc8(byte[] data) {
        byte crc = (byte) 0xFF;
        for (Byte b : data) {
            crc ^= b;

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    crc = (byte) ((crc << 1) ^ 0x31);
                } else {
                    crc = (byte) (crc << 1);
                }
            }
        }

        return crc;
    }

}
