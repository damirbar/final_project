package com.ariel.wizer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import java.util.Collection;
import java.util.List;

/**
 * Created by PC on 14-Nov-17.
 */




public class common {

//    private static String DB_NAME =  "wizer";
//    private static String COLLECTION_NAME= "students";
//    public static String API_KEY = "cluster0-shard-00-00-00hhm.mongodb.net";

//     MongoClientURI uri = new MongoClientURI("mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
//     MongoClient mongoClient = new MongoClient(uri);
//     MongoDatabase db = mongoClient.getDatabase("test");

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     * //* @throws Exception
     */
    public MongoClient createClient() throws Exception {
        MongoClientURI uri = new MongoClientURI("mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin");
        MongoClient mongo = new MongoClient(uri);
        DB db = new DB(mongo, uri.getDatabase());

        // Set write concern if configured
        // String defaultWriteConcern = config.getString("playjongo.defaultWriteConcern");
        //if(defaultWriteConcern != null) {
        //   db.setWriteConcern(WriteConcern.valueOf(defaultWriteConcern));
        //}

        return mongo;
    }

    public void getStudent(student student) {

        DB db;
        //MongoCollection<Document> coll = db.getCollection("students");

        //  System.out.println(coll.find(eq("first_name", student.getFirst_name())));

//        Document doc = (Document) coll.find(eq("first_name", student.getFirst_name()));
//        coll.find(eq("first_name", student.getFirst_name()));

    }

    public static void main(String args[]) {
        student s = new student();
        System.out.println("test");
        System.out.println(s.);
    }
}
