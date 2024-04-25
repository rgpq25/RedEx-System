import java.util.Date;
import java.util.HashMap;

public class CapacityTracker {
    private HashMap<DateRange, Integer> capacityMap;

    private static class DateRange {
        private final Date start;
        private final Date end;

        public DateRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        public boolean overlaps(DateRange other) {
            return this.start.before(other.end) && other.start.before(this.end);
        }
    }

    public CapacityTracker() {
        capacityMap = new HashMap<>();
    }

    // Register capacity for a specific time period
    public void registerCapacity(Date startDate, Date endDate, int capacity) {
        DateRange range = new DateRange(startDate, endDate);
        capacityMap.put(range, capacity);
    }

    // Retrieve used capacity for a specific time period
    public int getUsedCapacity(Date startDate, Date endDate) {
        int usedCapacity = 0;
        DateRange targetRange = new DateRange(startDate, endDate);
        
        for (HashMap.Entry<DateRange, Integer> entry : capacityMap.entrySet()) {
            DateRange range = entry.getKey();
            if (range.overlaps(targetRange)) {
                usedCapacity += entry.getValue();
            }
        }
        return usedCapacity;
    }
}