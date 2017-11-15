package com.ariel.wizer;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import java.util.Collection;

/**
 * Created by PC on 14-Nov-17.
 */

public class common {

//    private static String DB_NAME =  "wizer";
//    private static String COLLECTION_NAME= "students";
//    public static String API_KEY = "cluster0-shard-00-00-00hhm.mongodb.net";

    static MongoClientURI uri = new MongoClientURI("mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
    static MongoClient mongoClient = new MongoClient(uri);
    static MongoDatabase db = mongoClient.getDatabase("test");


    public static Document getStudent(student student){

        MongoCollection<Document> coll = db.getCollection("students");

//        System.out.println("PRINTING THE STUDENTS COLLECTION:\n" + coll.toString());

//        Document doc = (Document) coll.find(eq("first_name", student.getFirst_name()));
        coll.find(eq("first_name", student.getFirst_name()));


        return doc;
    }

    public static void main(String args[]) {
        student student = new student();
        student.setFirst_name("dima");

        Document doc = getStudent(student);

//        System.out.println(doc);
    }

}
