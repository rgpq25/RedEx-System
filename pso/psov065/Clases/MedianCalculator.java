package Clases;
import java.util.ArrayList;
import java.util.Collections;

public class MedianCalculator {
    public static double calculateMedian(ArrayList<Double> numbers) {
        // Sort the numbers
        Collections.sort(numbers);
        
        int size = numbers.size();
        
        if (size % 2 == 0) {
            // If even number of elements, average of two middle elements
            int middleIndex = size / 2;
            double median1 = numbers.get(middleIndex - 1);
            double median2 = numbers.get(middleIndex);
            return (median1 + median2) / 2.0;
        } else {
            // If odd number of elements, middle element is median
            return numbers.get(size / 2);
        }
    }
}