package ap.project.model.App;

import java.security.MessageDigest;
import java.util.Random;

public class SHA256Hasher
{
    public static String hash(String input)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

            // Convert the byte array to hex format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes)
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // pad with leading zero if needed
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e)
        {
            throw new RuntimeException("Error while hashing", e);
        }
    }

    public static int randomizedHashInt(String input, int digits)
    {
        try
        {
            long timeComponent = System.currentTimeMillis();
            int randomComponent = new Random().nextInt(100000);

            String combined = input + timeComponent + randomComponent;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes("UTF-8"));

            // Use the first few bytes to create a positive number
            int result = 0;
            for (int i = 0; i < 4; i++) {
                result = (result << 8) | (hashBytes[i] & 0xFF);
            }

            result = Math.abs(result);
            int max = (int) Math.pow(10, digits);
            return result % max;

        } catch (Exception e)
        {
            throw new RuntimeException("Error while generating randomized hash int", e);
        }
    }
}
