package it.polito.wa2.g17.csv_creator

import com.codahale.usl4j.Measurement
import com.codahale.usl4j.Model
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path


class CsvCreator {


    fun generateCSV() {

        val testData = mutableListOf<DoubleArray>()
        val expectedThroughputs = mutableListOf<DoubleArray>()

        for(concurrencyLevel in arrayOf(1,2,4,8,16,32)){
            val fileName: Path = Path.of("./benchmark/LoadTestResults/result_$concurrencyLevel")

            val json = Files.readString(fileName)

            val obj = JSONObject(json)

            testData.add(doubleArrayOf(concurrencyLevel.toDouble(), obj.getDouble("rps")))
        }

        // Map the points to measurements of concurrency and throughput, then build a model from them.
        val model = testData
            .stream()
            .map { point: DoubleArray? ->
                Measurement.ofConcurrency().andThroughput(point)
            }
            .collect(Model.toModel())
        var i = 1
        while (i <= 200) {
            expectedThroughputs.add(doubleArrayOf(i.toDouble(),model.throughputAtConcurrency(i.toDouble())))
            i += 1
        }

        val writer = BufferedWriter(FileWriter("./server/src/main/kotlin/it/polito/wa2/g17/csv_creator/expected_throughput.csv"))

        expectedThroughputs.forEach{
            writer.write(""+it[0].toInt()+","+it[1]+"\n")
        }

        writer.close()

    }
}