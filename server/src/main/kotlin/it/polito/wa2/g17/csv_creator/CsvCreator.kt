package it.polito.wa2.g17.csv_creator

import com.codahale.usl4j.Measurement
import com.codahale.usl4j.Model
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


fun main() {
    val csvCreator : Example = Example()
    csvCreator.buildModel()
}

internal class Example {
    fun buildModel() {

        val testData = mutableListOf<DoubleArray>()

        for(concurrencyLevel in arrayOf(1,2,4,8,16,32)){
            val fileName: Path = Path.of("../../../../benchmark/LoadTestResults/result_$concurrencyLevel")
            val json = Files.readString(fileName)

            val obj = JSONObject(json)

            testData.add(doubleArrayOf(concurrencyLevel.toDouble(), obj.getDouble("rps")))
        }






        val points = arrayOf(
            doubleArrayOf(1.0, 955.16),
            doubleArrayOf(2.0, 1878.91),
            doubleArrayOf(3.0, 2688.01),
            doubleArrayOf(1.0, 955.16),
            doubleArrayOf(2.0, 1878.91),
            doubleArrayOf(3.0, 2688.01)
        ) // etc.

        // Map the points to measurements of concurrency and throughput, then build a model from them.
        val model = Arrays.stream(testData)
            .map { point: DoubleArray? ->
                Measurement.ofConcurrency().andThroughput(point)
            }
            .collect(Model.toModel())
        var i = 10
        while (i < 200) {
            System.out.printf("At %d workers, expect %f req/sec\n", i, model.throughputAtConcurrency(i.toDouble()))
            i += 10
        }
    }
}