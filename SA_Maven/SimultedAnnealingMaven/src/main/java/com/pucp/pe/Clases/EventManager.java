package com.pucp.pe.Clases;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;

public class EventManager {
    private Multimap<Range<Integer>, String> events;

    public EventManager() {
        events = ArrayListMultimap.create();
    }

    public void addEvent(Range<Integer> range, String event) {
        events.put(range, event);
    }

    public Collection<String> getEventsInRange(Range<Integer> queryRange) {
        Collection<String> results = new ArrayList<>();
        for (Range<Integer> range : events.keySet()) {
            if (queryRange.encloses(range)) {
                results.addAll(events.get(range));
            }
        }
        return results;
    }

}
