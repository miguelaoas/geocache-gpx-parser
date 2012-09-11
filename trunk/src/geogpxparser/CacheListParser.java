package geogpxparser;

import java.util.List;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A parser for transforming a List of Geocache objects into table format,
 * represented as a long String object.
 *
 * @author Ville Saalo (http://coord.info/PR32K8V)
 */
public class CacheListParser implements ICachesToTextParser {

    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String separator = "\t";

    @Override
    public String getInfoAsText(final List<Geocache> caches) {

        StringBuilder sb = new StringBuilder();
        sb.append("gccode").append(separator);
        sb.append("type").append(separator);
        sb.append("name").append(separator);
        sb.append("longitude").append(separator);
        sb.append("latitude").append(separator);
        sb.append("size").append(separator);
        sb.append("difficulty").append(separator);
        sb.append("terrain").append(separator);
        sb.append("published").append(separator);
        sb.append("owner").append(separator);
        sb.append("\n");

        for (Geocache cache : caches) {
            sb.append(cache.getGcCode()).append(separator);
            sb.append(cache.getType()).append(separator);
            sb.append(cache.getName().replace(separator, "")).append(separator);
            sb.append(cache.getLongitude()).append(separator);
            sb.append(cache.getLatitude()).append(separator);
            sb.append(cache.getSize()).append(separator);
            sb.append(cache.getDifficulty()).append(separator);
            sb.append(cache.getTerrain()).append(separator);
            sb.append(OUTPUT_DATE_TIME_FORMAT.print(cache.getPublished())).append(separator);
            sb.append(cache.getOwner().replace(separator, "")).append(separator);
            sb.append("\n");
        }

        return sb.toString();
    }
}
