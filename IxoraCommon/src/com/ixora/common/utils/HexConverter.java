package com.ixora.common.utils;

/**
 * A tool to convert data to ASCII code or characters representation.
 */
public final class HexConverter {
    private static byte[] decodeTable;
    private static char[] encodeTable = new char[]{
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F'
    };

    static {
        decodeTable = new byte[256];
        for(int i = '0'; i <= '9'; i++) {
            decodeTable[i] = (byte)(i - '0');
        }
        for(int i = 'A'; i <= 'F'; i++) {
            decodeTable[i] = (byte)(i - 'A' + 10);
        }
    }

    /**
     * Convert all bytes to their ASCII code.
     * @param bytes array of bytes to convert.
     * @return converted bytes.
     */
    public static String encode(byte[] bytes) {
        if(bytes != null) {
            int lengthData = bytes.length;
            StringBuffer result = new StringBuffer(lengthData * 2);
            for(int i = 0; i < lengthData; i++) {
                result.append(encodeTable[(byte)((bytes[i] >> 4) & 0xf)]);
                result.append(encodeTable[(byte)(bytes[i] & 0xf)]);
            }
            return result.toString();
        }
        return "";
    }

    /**
     * Convert all ASCII codes to bytes.
     * @param asciiString The string to convert
     * @return an array of bytes.
     */
    public static byte[] decode(String asciiString) {
        if(asciiString != null) {
            int lengthData = asciiString.length();
            if((lengthData % 2) == 0) {
                int lengthDecode = lengthData / 2;
                byte[] decodedData = new byte[lengthDecode];
                for(int i = 0; i < lengthDecode; i++) {
                    decodedData[i] = (byte)((decodeTable[asciiString.charAt(
                                                                 i * 2)] << 4)
                                     | decodeTable[asciiString.charAt((i * 2) + 1)]);
                }
                return decodedData;
            }
        }
        return null;
    }
}