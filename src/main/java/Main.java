import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 17:18 2020/5/7
 */
public class Main {

    public String  protocol(String bytes) throws Exception {
        StringBuffer result = new StringBuffer();
        // 先转换成byte数组
        byte[] destByte = new byte[bytes.length()/2];
        int j=0;
        for(int i=0;i<destByte.length;i++) {
            byte high = (byte) (Character.digit(bytes.charAt(j), 16) & 0xff);
            byte low = (byte) (Character.digit(bytes.charAt(j + 1), 16) & 0xff);
            destByte[i] = (byte) (high << 4 | low);
            j+=2;
//            System.out.println(byteToHex(destByte[i]));
        }
        // 1 判断头和尾 1位
        if(destByte[0] != 0x7e || destByte[destByte.length-1] != 0x7e ){
            return "error message";
        }
        // 2 获取时间 4位
        byte[] time = new byte[4];
        System.arraycopy(destByte,1,time,0,4);
        // System.out.println(bytestoHex(time)); // 秒数*1000
        long timestamp = (long) byteArrayToInt(time) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        result.append(sdf.format(new Date(timestamp)));
        result.append(" ");
        // 3 判断长度 2位
        int length = (destByte[6] & 0xff) << 0 |(destByte[5] & 0xff) << 8;
//        System.out.println(length);
        if( length != destByte.length){
            return "error message";
        }
        // 4 020b
        int stringLength = (int)destByte[8];
        byte[] stringByte = new byte[stringLength];
        System.arraycopy(destByte,9,stringByte,0,stringLength);
//        System.out.println(new String(stringByte));
        result.append(new String(stringByte,Charset.forName("US-ASCII")));
        result.append(" ");
        // 05
        int l1 = 9 + stringLength;
        byte[] num = new byte[4];
        System.arraycopy(destByte,21,num,0,4);
//        System.out.println(bytestoHex(num));
        result.append(byteArrayToInt(num));
        // 校验
        byte[] check = new byte[7+stringLength];
        System.arraycopy(destByte,7,check,0,check.length);
        int l2 = 9 + stringLength + 5;
        int checks = (destByte[l2+1] & 0xff) << 0 |(destByte[l2] & 0xff) << 8;

        int right = 0;
        for (int i = 0; i < check.length; i++) {
            right += check[i];
        }
//        System.out.println(checks);
//        System.out.println(right);
        if(checks != right){
            return "error message";
        }

//        byteArrayToInt()

        return result.toString();
    }


    public static void main(String[] args) throws Exception {
        String bytes = "7E5E1803F9001C020B68656C6C6F20776F726C640510000001047F7E";
        System.out.println(new Main().protocol(bytes));
    }


    public  String byteToHex(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex;
    }

    public String bytestoHex(byte[] b){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            stringBuffer.append(byteToHex(b[i]));
        }
        return stringBuffer.toString();
    }

    public  int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }



}
