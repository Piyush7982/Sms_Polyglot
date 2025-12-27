package utils
import (
	"context"
	"fmt"
	"log"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)
func ConnectMongo() *mongo.Collection {
	// Credentials from docker-compose (root/rootpassword)
	clientOptions := options.Client().ApplyURI("mongodb://root:rootpassword@localhost:27017")
	client, err := mongo.Connect(context.Background(), clientOptions)
	if err != nil {
		log.Fatal(err)
	}

	// Check connection
	err = client.Ping(context.Background(), nil)
	if err != nil {
		log.Fatal("Could not connect to MongoDB:", err)
	}

	fmt.Println("Connected to MongoDB!")
	return client.Database("sms_db").Collection("messages")
}