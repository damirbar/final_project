package com.ariel.wizer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;


public class databaseAccess {

    private final String DB_HOST = "localhost";
    private final int DB_PORT = 3000;
    private final String DB_NAME = "tests";
    private final String DB_COLLECTION = "students";
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public databaseAccess(){
        mongoClient = new MongoClient(DB_HOST, DB_PORT);
        database = mongoClient.getDatabase(DB_NAME);
        collection = database.getCollection(DB_COLLECTION);
    }


    public String readFromDB(String id){
        collection.find(eq("id", id)).first().getString(fields(include("priceInfo"), excludeId()));
        return null;
    }

}