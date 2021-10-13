package org.kin.main

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.kin.rdmsUtils.JDBCUtils.upsertPerson
import org.kin.rdmsUtils.traits.jdbcCurd
import org.kin.utils.TimeUtils.getcaltime

import scala.collection.mutable.ListBuffer

case class Person(name: String, age: Int)

object shcReadHbase {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName("sparksql")
      .master("local[2]")
      .getOrCreate()

    import spark.implicits._
    import spark.sql

    //处理day_id
    var day_start = ""
    var day_end = ""
    //如果没有传参，则默认昨天;如果有传参，则使用该传参日期之前的一天（传参例子：20210927）
    if (args.length < 1) {          //无参
      val t = getcaltime().split(",")
      day_start = t(0)
      day_end = t(1)
      System.out.println("无参"+day_start +" "+day_end)
    } else {                        //手动传参
      val t = getcaltime(args(0)).split(",")
      day_start = t(0)
      day_end = t(1)
      System.out.println("手动传参"+day_start +" "+day_end)
    }


    val rk_dm_SQL:String =
      s"""
         |
         |(SELECT
         |  b.co1,
         |  b.co2,
         |  c.co3,
         |  b.ID AS co4,
         |  b.co5
         |FROM
         | tba b,tbb a,tbc c
         |WHERE
         |  a.ID = b.id)t1
         |
   """.stripMargin


    val rk_dm = spark.read.format("jdbc")
      .option("url","jdbc:oracle:thin:@0.0.0.11:1521:orcl")
      .option("dbtable",rk_dm_SQL)
      .option("user","name")
      .option("password","pwd")
      .option("driver", "oracle.jdbc.driver.OracleDriver")
      .load()

    rk_dm.createOrReplaceTempView("rk_dm")   //oracle生成的读数辅助表
    rk_dm.show(200)

    //hbase rk 条件拼接 ，oracle执行限制，故此执行。
    val wheres = "start".concat(spark.sql(
      """
        |
        |select  concat_ws(' ',collect_set(wl)) as ws  from rk_dm
        |
        |""".stripMargin
    ).collect()(0)
      .getString(0)
      .replaceAll("BETWEEN ","BETWEEN '")
      .replaceAll(" and ","' and '")
      .replaceAll("  or","'  or")
      .concat("'")).replaceAll("start or","where ")



    val data_df   = spark.read
      .options(Map(HBaseTableCatalog.tableCatalog -> Catalog.schema))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()

    data_df.createOrReplaceTempView("data_df")

    val data_df2 = spark.sql(
      s"""
        |
        |select
        |a,
        |b,
        |c,
        |d
        |from data_df
        | $wheres
        |
        |""".stripMargin)

    data_df2.createOrReplaceTempView("hbase_data_df2") //hbase数据明细表



    val MONTH_REPORT =spark.sql(
      """
        |
        |select
        |dm.co1,
        |dm.co2 as co2,
        |t1.one_value,
        |t1.two_value,
        |t1.three_value,
        |t1.four_value,
        |t1.total_value
        |from
        |rk_dm dm
        |left join
        |(select
        |co1,
        |co2,
        |concat(date_format(read_time, 'y-MM-d h'),":00:00") AS collect_time,
        |max(case when substr(read_time,15,16)=='00' then a end ) one_value,
        |max(case when substr(read_time,15,16)=='15' then a else 0 end) two_value,
        |max(case when substr(read_time,15,16)=='30' then a else 0 end) three_value,
        |max(case when substr(read_time,15,16)=='45' then a else 0 end) four_value,
        |max(case when substr(read_time,15,16)=='45' then a else 0 end) total_value
        |from
        |hbase_data_df2
        |group by
        |co1,
        |co2,
        |concat(date_format(read_time, 'y-MM-d h'),":00:00")) t1
        |on dm.id=t1.id
        |
        |""".stripMargin)  //.show()


    // 批量写入RDMS
    // 此处最好对处理的结果进行一次重分区
    // 由于数据量特别大，会造成每个分区数据特别多
    MONTH_REPORT.as[Person].repartition(500).foreachPartition(record => {

      val list = new ListBuffer[Person]
      record.foreach(person => {
        val name = person.name
        val age = person.age
        list.append(Person(name,age))
      })

      upsertPerson(list) //执行批量插入数据
    })



    spark.stop()

  }

}


object Catalog {
  val schema = s"""{
                  |   "table":{"namespace":"default", "name":"hbase_data", "tableCoder":"PrimitiveType"},
                  |   "rowkey":"key",
                  |   "columns":{
                  |       "row_key":{"cf":"rowkey", "col":"key", "type":"String"},
                  |       "id":{"cf":"f1", "col":"id", "type":"String"},
                  |       "name":{"cf":"f1", "col":"name", "type":"String"},
                  |       "age":{"cf":"f1", "col":"age", "type":"String"},
                  |   }
                  |}""".stripMargin
}



