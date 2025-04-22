package org.example;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class Main {
    public static void main(String[] args) {
                SparkConf conf = new SparkConf().setAppName("VentesParVille").setMaster("local[*]");
                JavaSparkContext sc = new JavaSparkContext(conf);

                JavaRDD<String> lignes = sc.textFile("src/main/resources/ventes.txt");

                JavaPairRDD<String, Double> villePrix = lignes
                        .mapToPair(ligne -> {
                            String[] parts = ligne.split(" ");
                            String ville = parts[1];
                            double prix = Double.parseDouble(parts[3]);
                            return new Tuple2<>(ville, prix);
                        });

                JavaPairRDD<String, Double> totalParVille = villePrix
                        .reduceByKey(Double::sum);

                totalParVille.foreach(result -> {
                    System.out.println("Ville: " + result._1 + ", Total: " + result._2);
                });

                sc.close();
            }
        }
