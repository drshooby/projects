package project06

import (
	"encoding/json"
	"io"
	"log"
	"net/http"
)

// CreateStopWordsSet creates a set of stop words from the stop words JSON file
func CreateStopWordsSet() map[string]struct{} {

	url := "https://raw.githubusercontent.com/stopwords-iso/stopwords-en/master/stopwords-en.json" // url for the stop words

	rsp, err := http.Get(url)
	if err != nil {
		log.Printf("error retreiving stop words JSON data: %v", err)
	}
	defer func(Body io.ReadCloser) {
		err := Body.Close()
		if err != nil {
			log.Printf("error closing body.")
		}
	}(rsp.Body)

	body, err := io.ReadAll(rsp.Body)
	if err != nil {
		log.Printf("error reading response: %v", err)
	}

	var stopWords []string

	err = json.Unmarshal(body, &stopWords) // unload the contents of the json into stopWords slice
	if err != nil {
		log.Printf("error unmarshalling JSON: %v", err)
	}

	stopWordsSet := make(map[string]struct{})

	for _, word := range stopWords { // populate the set
		stopWordsSet[word] = struct{}{}
	}

	return stopWordsSet
}
