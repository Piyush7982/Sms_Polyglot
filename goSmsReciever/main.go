package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strings"
	"time"
	"goSmsReciever/utils"

	"github.com/IBM/sarama"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"

)

// SmsRecord represents the document in MongoDB
type SmsRecord struct {
	PhoneNumber string    `json:"phoneNumber" bson:"phoneNumber"`
	Message     string    `json:"message"     bson:"message"`
	CreatedAt   time.Time `json:"createdAt"   bson:"createdAt"`
}

// Global variables for connections
var mongoCollection *mongo.Collection


func startKafkaConsumer() {
	config := sarama.NewConfig()
	config.Consumer.Return.Errors = true

	// Connect to Kafka Container
	brokers := []string{"localhost:9092"}
	master, err := sarama.NewConsumer(brokers, config)
	if err != nil {
		log.Fatal("Could not create Kafka consumer:", err)
	}

	consumer, err := master.ConsumePartition("sms_send_events", 0, sarama.OffsetNewest)
	if err != nil {
		log.Fatal("Could not start consumer partition:", err)
	}

	fmt.Println("Kafka Consumer started...")

	// Loop forever to read messages
	for {
		select {
		case err := <-consumer.Errors():
			log.Println("Kafka Error:", err)
		case msg := <-consumer.Messages():
			// Deserialize JSON from Java
			var record SmsRecord
			json.Unmarshal(msg.Value, &record)
			record.CreatedAt = time.Now()

			// Insert into MongoDB
			_, insertErr := mongoCollection.InsertOne(context.Background(), record)
			if insertErr != nil {
				log.Println("Error inserting into Mongo:", insertErr)
			} else {
				log.Printf("Saved SMS for %s: \n", record.PhoneNumber)
			}
		}
	}
}

func getMessagesHandler(w http.ResponseWriter, r *http.Request) {
	// URL Pattern: /v1/user/{phoneNumber}/messages
	parts := strings.Split(r.URL.Path, "/")
	if len(parts) < 4 || parts[4] != "messages" {
		http.Error(w, "Invalid URL format", http.StatusBadRequest)
		return
	}
	userPhone := parts[3]

	// Query MongoDB
	filter := bson.M{"phoneNumber": userPhone}
	cursor, err := mongoCollection.Find(context.Background(), filter)
	if err != nil {
		http.Error(w, "Error fetching data", http.StatusInternalServerError)
		return
	}
	defer cursor.Close(context.Background())

	var messages []SmsRecord
	if err = cursor.All(context.Background(), &messages); err != nil {
		http.Error(w, "Error parsing data", http.StatusInternalServerError)
		return
	}

	// Return JSON response
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(messages)
}

func main() {
	// 1. Connect to DB
	mongoCollection = utils.ConnectMongo()

	// 2. Start Kafka Consumer in a separate Goroutine (Background)
	go startKafkaConsumer()

	// 3. Start HTTP Server (Foreground)
	http.HandleFunc("/v1/user/", getMessagesHandler)

	fmt.Println("Go SMS Store Service running on port 8081...")
	log.Fatal(http.ListenAndServe(":8081", nil))
}