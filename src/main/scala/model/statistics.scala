package model

object Statistics {
    def mean(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else values.sum / values.size
    }

    def median(values: List[Double]): Double = {
        val sorted = values.sorted
        val size = sorted.size
        if (size % 2 == 0) {
            (sorted(size / 2 - 1) + sorted(size / 2)) / 2.0
        } else {
            sorted(size / 2)
        }
    }

    def mode(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else {
            val frequencyMap = values.groupBy(identity).mapValues(_.size)
            val maxFrequency = frequencyMap.values.max
            frequencyMap.filter(_._2 == maxFrequency).keys.head
        }
    }

    def range(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else values.max - values.min
    }

    def midrange(values: List[Double]): Double = {
        if (values.isEmpty) 0.0
        else (values.max + values.min) / 2.0
    }
}