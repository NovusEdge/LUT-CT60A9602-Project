package model

/**
 * Provides statistical functions for analyzing energy data.
 * 
 * This object contains various statistical methods that can be applied to
 * lists of numerical data to calculate common statistical measures.
 */
object Statistics {
    /**
     * Calculates the arithmetic mean (average) of a list of values.
     * 
     * @param values The list of values to calculate the mean for
     * @return The arithmetic mean of the values, or 0.0 if the list is empty
     */
    def mean(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else values.sum / values.size
    }

    /**
     * Calculates the median (middle value) of a list of values.
     * 
     * For lists with an even number of elements, returns the average of the two middle values.
     * For lists with an odd number of elements, returns the middle value.
     * 
     * @param values The list of values to calculate the median for
     * @return The median of the values, or 0.0 if the list is empty
     */
    def median(values: List[Double]): Double = {
        val sorted = values.sorted
        val size = sorted.size
        if (size % 2 == 0) {
            (sorted(size / 2 - 1) + sorted(size / 2)) / 2.0
        } else {
            sorted(size / 2)
        }
    }

    /**
     * Calculates the mode (most frequently occurring value) of a list of values.
     * 
     * If multiple values have the same highest frequency, returns the first one encountered.
     * 
     * @param values The list of values to calculate the mode for
     * @return The mode of the values, or 0.0 if the list is empty
     */
    def mode(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else {
            val frequencyMap = values.groupBy(identity).mapValues(_.size)
            val maxFrequency = frequencyMap.values.max
            frequencyMap.filter(_._2 == maxFrequency).keys.head
        }
    }

    /**
     * Calculates the range (difference between maximum and minimum values) of a list of values.
     * 
     * @param values The list of values to calculate the range for
     * @return The range of the values, or 0.0 if the list is empty
     */
    def range(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else values.max - values.min
    }

    /**
     * Calculates the midrange (average of maximum and minimum values) of a list of values.
     * 
     * @param values The list of values to calculate the midrange for
     * @return The midrange of the values, or 0.0 if the list is empty
     */
    def midrange(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else (values.max + values.min) / 2.0
    }
}