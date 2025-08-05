package ap.project.util;

import java.security.SecureRandom;
import java.util.Random;

public class StringToNumber
{
    public static String stringToRandomizedFixedDigitNumber(String input, int digits)
    {
        Random random = new Random();

        // Mix of string hash, time, and a random number
        int hashPart = Math.abs(input.hashCode());
        long timePart = System.currentTimeMillis() % 100000;
        int randomPart = random.nextInt(100000);

        long combined = Math.abs(hashPart + timePart + randomPart);
        String result = Long.toString(combined);

        // Pad with zeros or trim to required digits
        if (result.length() < digits) {
            result = String.format("%0" + digits + "d", combined);
        } else if (result.length() > digits) {
            result = result.substring(0, digits);
        }

        return result;
    }

    public static String generateId(int digits)
    {
        SecureRandom random = new SecureRandom();
        long randomPart = random.nextLong() & Long.MAX_VALUE; // Ensure positive
        long timePart = System.nanoTime() % 1000000000L;

        String base = Long.toString(randomPart + timePart);
        return base.length() > digits ?
            base.substring(0, digits) :
            String.format("%0" + digits + "d", Long.parseLong(base));
    }
}

