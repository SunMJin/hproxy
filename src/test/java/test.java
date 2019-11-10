import java.io.UnsupportedEncodingException;

public class test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        byte x[]=new byte[]{-97, -78, -2, -96, -25, -58, 85, 40, 124, -75, -73, -39, -120, 95, 120, -94};
        System.out.println(new String(x,"utf-8"));
    }
}
