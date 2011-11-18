package geogpxparser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is the unit test class for the Geocache class.
 * @author ZeroOne
 */
public class GeocacheTest {

    @Test
    public void testHint() {
        System.out.println("hint");
        boolean decrypted = false;
        Geocache instance = new Geocache();
        instance.setHint("kiven alla");
        String expResult = "xvira nyyn";
        String result = instance.getHint(decrypted);
        assertEquals(expResult, result);
        
        expResult = "kiven alla";
        decrypted = true;
        result = instance.getHint(decrypted);
        assertEquals(expResult, result);
        
        instance.setHint("@[`{ KIVEN YLLÄ 123");
        expResult = "@[`{ XVIRA LYYÄ 123";
        decrypted = false;
        result = instance.getHint(decrypted);
        assertEquals(expResult, result);
        
        expResult = "@[`{ KIVEN YLLÄ 123";
        decrypted = true;
        result = instance.getHint(decrypted);
        assertEquals(expResult, result);
    }
}
