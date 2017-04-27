package net.muratov.mercury200;

import jssc.SerialPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

@Component()
@Scope(value = "singleton")
public class Mercury200Connector {

    int MASK = 0xFF;

    Logger logger = Logger.getLogger("connector");

    @Value("${Mercury200.address}")
    private Integer mAddress;

    @Value("${Mercury200.port}")
    private String mPort;

    @Value("${Mercury200.timeout}")
    private Integer mTimeout;

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte [] request(Integer cmd, Integer response_len) throws Exception {
        ByteBuffer cmd_buffer = ByteBuffer.allocate(7);
        cmd_buffer.put((byte)0x00);
        cmd_buffer.put((byte)(mAddress >> 16));
        cmd_buffer.put((byte)(mAddress >> 8));
        cmd_buffer.put((byte)(mAddress >>0));
        cmd_buffer.put(cmd.byteValue());
        int cmd_buffer_crc = Mercury200CRC16.calc(Mercury200CRC16.INITIAL_VALUE, cmd_buffer.array(), 5);
        cmd_buffer.put((byte)(cmd_buffer_crc >> 0));
        cmd_buffer.put((byte)(cmd_buffer_crc >> 8));

        SerialPort port = new SerialPort(mPort);

        port.openPort();

        port.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        logger.info("> " + bytesToHex(cmd_buffer.array()));

        port.writeBytes(cmd_buffer.array());

        logger.info("waiting response");

        byte answer [] = port.readBytes(response_len + 7, mTimeout);

        logger.info("< " + bytesToHex(answer));

        int answer_crc_calculated = Mercury200CRC16.calc(Mercury200CRC16.INITIAL_VALUE, answer, answer.length - 2);

        int answer_crc;

        answer_crc = answer[answer.length - 2] & MASK;
        answer_crc = answer_crc + ((answer[answer.length - 1] & MASK) << 8);

        if ( answer_crc != answer_crc_calculated){
            throw  new Exception("CRC doesn't match");
        }
        port.closePort();

        byte response [] = Arrays.copyOfRange(answer, 5, 5 + response_len);

        logger.info("R " + bytesToHex(response));

        return response;
    }
}
