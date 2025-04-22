package org.example;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class App2 {
    public static void main(String[] args) {
        // 1. Configuration Spark
        SparkConf conf = new SparkConf().setAppName("VentesParVilleEtAnnee").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // 2. Lire le fichier
        JavaRDD<String> lignes = sc.textFile("src/main/resources/ventes.txtS");

        // 3. Extraction (ville, année) -> prix
        JavaPairRDD<Tuple2<String, String>, Double> villeAnneePrix = lignes
                .mapToPair(ligne -> {
                    String[] parts = ligne.split(" ");
                    String date = parts[0];
                    String ville = parts[1];
                    String annee = date.split("-")[0];
                    double prix = Double.parseDouble(parts[3]);
                    return new Tuple2<>(new Tuple2<>(ville, annee), prix);
                });

        // 4. Agrégation
        JavaPairRDD<Tuple2<String, String>, Double> totalParVilleEtAnnee = villeAnneePrix
                .reduceByKey(Double::sum);

        // 5. Affichage
        totalParVilleEtAnnee.foreach(result -> {
            String ville = result._1._1;
            String annee = result._1._2;
            double total = result._2;
            System.out.println("Ville: " + ville + ", Année: " + annee + ", Total: " + total);
        });

        // 6. Fermer Spark
        sc.close();
    }
}