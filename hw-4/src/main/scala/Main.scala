import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object Main {

    def main(args: Array[String]): Unit = {

        val spark = SparkSession.builder()
          .appName("HomeWork4")
          .master("local[8]")
          .config("spark.ui.enabled", "true")
          .config("spark.driver.memory", "8g")
          .config("spark.executor.memory", "8g")
          .config("spark.sql.adaptive.enabled", "true")
          .config("spark.ui.port", "4040")
          .getOrCreate()

        import spark.implicits._

        println("Dataset:")

        val events = spark.range(0, 500000000)
          .withColumn("user_id",
            when(rand() < 0.7, 1)
              .otherwise((rand()*5000000).cast("int"))
          )
          .withColumn("postal_id",
            (rand() * 200).cast("int")
          )
          .withColumn("amount", rand() * 100)
          .withColumn("user_type",
            when(rand() > 0.5, "premium").otherwise("basic")
          )
        
        val users = spark.range(0, 5000000)
          .withColumnRenamed("id", "user_id")
          .withColumn("user_type", 
            when(rand() > 0.5, "premium").otherwise("basic")
          )
        
        val postals = spark.range(0, 5000000)
          .withColumnRenamed("id", "postal_id")
          .withColumn("postal_code",
            concat(lit("postal_code_number_"),col("postal_id"))
          )
        
        postals.cache()
        postals.count()
        
        println("Join:")
        
        val baseline = events
          .join(users, "user_id")
          .join(postals, "postal_id")
          .groupBy("user_id")
          .agg(sum("amount").alias("total_amount"),
            count("postal_code").alias("same_postal_code"))
        
        baseline.count()

        println("Broadcast:")

        val broadcastJoin = events
          .join(users, "user_id")
          .join(broadcast(postals), "postal_id")
          .groupBy("user_id")
          .agg(sum("amount").alias("total_amount"),
            count("postal_code").alias("same_postal_code"))

        broadcastJoin.count()

        
        Thread.sleep(600000)
        spark.stop()


    }
}