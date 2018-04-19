package com.adcc.mcdu.simulator.util;

public class MCDUMsgParse {
		
    /**
     * 计算CRC代码
     * @param msg
     * @return
     */
    public static String getCrc(String msg) {
        byte buffer[] = msg.getBytes();
        int count = buffer.length;
        short bitMask = -32768;
        short register = -1;
        for (; count > 0; count--) {
            byte element = buffer[buffer.length - count];
            register ^= (short) element << 8;
            for (int i = 0; i < 8; i++) {
                if ((register & bitMask) != 0) {
                    register = (short) (register << 1 ^ 0x1021);
                } else {
                    register <<= 1;
                }
            }
        }
        register ^= 0xffff;
        String crc = Long.toHexString(register & 0xffff).toUpperCase();

        while (crc.length() < 4) {
            crc = "0".concat(String.valueOf(String.valueOf(crc)));
        }
        return crc;
    }    
    
}
