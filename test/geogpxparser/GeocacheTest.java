package geogpxparser;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This is the unit test class for the Geocache class.
 *
 * @author Ville Saalo (http://coord.info/PR32K8V)
 */
public class GeocacheTest {

    @Test
    public void testHint() {
        System.out.println("testHint");
        
        // Test the basic case of getting the hint as encrypted:
        boolean decrypted = false;
        Geocache instance = new Geocache();
        instance.setHint("kiven alla");
        String expResult = "xvira nyyn";
        String result = instance.getHint(decrypted);
        assertEquals(expResult, result);

        // Test the basic case of getting the hint as decrypted:
        expResult = "kiven alla";
        decrypted = true;
        result = instance.getHint(decrypted);
        assertEquals(expResult, result);

        // Test getting the encrypted hint with some non-alphabetic characters:
        instance.setHint("@[`{ KIVEN YLLÄ 123");
        expResult = "@[`{ XVIRA LYYÄ 123";
        decrypted = false;
        result = instance.getHint(decrypted);
        assertEquals(expResult, result);

        // Test getting the decrypted hint with some non-alphabetic characters:
        expResult = "@[`{ KIVEN YLLÄ 123";
        decrypted = true;
        result = instance.getHint(decrypted);
        assertEquals(expResult, result);
    }
}
