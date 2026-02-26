from pyspark.sql import SparkSession
from pyspark.sql.functions import udf
from pyspark.sql.types import *
from faker import Faker
import random

def generate_row(_):
    f = Faker()
    return (
        f.uuid4(),
        f.name(),
        f.city(),
        f.country(),
        f.job(),
        random.randint(18, 100),
        random.uniform(1000, 100000),
        f.date_time_this_decade()
    )

spark = SparkSession.builder \
                    .appName("DataGen") \
                    .master("local[8]") \
                    .config("spark.driver.memory", "8g") \
                    .config("spark.executor.memory", "8g") \
                    .getOrCreate()
faker = Faker()

N= 60_000_000

df = spark.range(0, N)

schema = StructType([
    StructField("id", StringType()),
    StructField("name", StringType()),
    StructField("city", StringType()),
    StructField("country", StringType()),
    StructField("job", StringType()),
    StructField("age", IntegerType()),
    StructField("salary", DoubleType()),
    StructField("timestamp", TimestampType())
])

generate_udf = udf(generate_row, schema)

df2 = df.select(generate_udf("id").alias("data")).select("data.*")
df2.write.mode("overwrite").csv("./data/result")