package geogpxparser;

import java.util.List;

/**
 * An interface for classes that can accept a List of Geocache objects and parse
 * them into some useful String, most likely to be saved as a file then.
 * 
 * @author ZeroOne
 */
public interface ICachesToTextParser {

    public String getInfoAsText(final List<Geocache> caches);
}
