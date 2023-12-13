package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"project06-shooby-d/project06"
	"syscall"
)

func main() {

	os.Remove("logfile.txt")

	// logging
	f, err := os.OpenFile("logfile.txt", os.O_WRONLY|os.O_CREATE|os.O_APPEND, 0666)
	if err != nil {
		log.Fatalf("fatal error opening/creating log file (this file is necessary): %v\n", err)
	}
	defer f.Close()

	log.SetOutput(f)

	exit := make(chan os.Signal, 1)
	signal.Notify(exit, os.Interrupt, syscall.SIGTERM)

	// search engine
	searchEngine := project06.NewSearchEngine(4, 7)

	fmt.Printf("Server is listening on port 8080...\n")

	searchEngine.Serve()

	go func() {
		err := http.ListenAndServe("localhost:8080", nil)
		if err != nil {
			log.Println("ListenAndServe:", err)
		}
	}()

	if GetCommandLineFlag() {
		log.Println("Crawling enabled")
		//os.Remove(project05.DBNAME)
		go searchEngine.Crawl("https://www.dvc.edu") // called in a goroutine so I can still use ctrl+c to exit
	} else {
		fmt.Println("Crawling disabled, search only at http://localhost:8080/")
	}

	<-exit
	log.Println("Shutting down server...")
}

func GetCommandLineFlag() bool {
	method := flag.Bool("crawl", false, "Set to true to crawl, false for search only")
	flag.Parse()
	return *method
}
