package geogpxparser;

import geogpxparser.Geocache.CacheType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Parses owner statistics from the given list of caches: the number of caches
 * and different cache types each owner has.
 *
 * @author Ville Saalo (http://coord.info/PR32K8V)
 */
public class OwnerStatsParser implements ICachesToTextParser {

    private Map<String, Owner> owners = new LinkedHashMap<>();
    private static final String SEPARATOR = "\t";

    /**
     * Returns a String of the tabular format with data about cache owners.
     *
     * @param caches A List of Geocache objects
     * @return A String that can be saved into a file and opened in a
     * spreadsheet program.
     */
    @Override
    public String getInfoAsText(List<Geocache> caches) {

        // Parse cache owner info into a map:
        for (Geocache cache : caches) {
            addCacheForOwner(cache.getOwner(), cache.getType());
        }

        // Create titles:
        StringBuilder sb = new StringBuilder();
        sb.append("Owner").append(SEPARATOR);
        sb.append("Number of caches").append(SEPARATOR);
        sb.append("Number of cache types").append(SEPARATOR);
        for (CacheType cacheType : CacheType.values()) {
            sb.append(cacheType).append(SEPARATOR);
        }
        sb.append("\n");

        // Create data rows:
        for (Owner owner : owners.values()) {
            sb.append(owner.getName().replace(SEPARATOR, "")).append(SEPARATOR);
            sb.append(owner.getTotalNumberOfCaches()).append(SEPARATOR);
            sb.append(owner.getNumberOfCacheTypes()).append(SEPARATOR);
            Map<CacheType, Integer> cacheMap = owner.getCaches();

            for (Entry<CacheType, Integer> entry : cacheMap.entrySet()) {
                sb.append(entry.getValue()).append(SEPARATOR);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Initializes a new owner in the map if required, then adds +1 to the
     * amount of the caches of the given type.
     *
     * @param owner Name of the cache owner
     * @param cacheType Type of the cache
     */
    private void addCacheForOwner(String ownerName, CacheType cacheType) {
        if (!owners.containsKey(ownerName)) {
            owners.put(ownerName, new Owner(ownerName));
        }

        owners.get(ownerName).addCache(cacheType);
    }

    /**
     * Represents someone who owns some geocaches. Keeps track of the name of
     * the owner and the amount of different cache types they have.
     */
    private class Owner {

        private final String name;
        private Map<CacheType, Integer> caches;

        public Owner(String ownerName) {
            name = ownerName;
            caches = new LinkedHashMap<>();
            for (CacheType cacheType : CacheType.values()) {
                caches.put(cacheType, 0);
            }
        }

        public void addCache(CacheType cacheType) {
            int amount = caches.get(cacheType);
            amount++;
            caches.put(cacheType, amount);
        }

        public int getTotalNumberOfCaches() {
            int result = 0;
            for (Entry<CacheType, Integer> cacheEntry : caches.entrySet()) {
                result += cacheEntry.getValue();
            }
            return result;
        }

        public int getNumberOfCacheTypes() {
            int result = 0;
            for (Entry<CacheType, Integer> cacheEntry : caches.entrySet()) {
                if (cacheEntry.getValue() > 0) {
                    result++;
                }
            }
            return result;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        public String getName() {
            return name;
        }

        public Map<CacheType, Integer> getCaches() {
            return caches;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && !(o instanceof Owner)) {
                return false;
            }

            return name != null && name.equals(((Owner) o).getName());
        }
    }
}
