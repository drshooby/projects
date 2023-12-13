package project06

import (
	"log"
	"net/http"
	"strings"
	"text/template"

	"github.com/kljensen/snowball"
)

type SearchResults struct {
	HitsResults  Hits
	ImageResults ImageHits
	NoHits       bool
	NoImages     bool
	Term         string
	Wildcard     bool
}

func (s *SearchEngine) HandleSearch(writer http.ResponseWriter, request *http.Request) {
	if request.Method == "GET" {
		query := request.URL.Query().Get("search")

		useWildcard := false

		if wildcard := request.URL.Query().Get("wildcard"); wildcard == "true" {
			log.Println("Wildcard search requested.")
			useWildcard = true
		}

		queries := strings.Split(query, " ")

		if len(queries) > 2 {
			err := RenderError(writer, "Too many query terms, only supports up to two words.")
			if err != nil {
				return
			}
		}

		if len(queries) == 2 {
			s.HandleBigrams(writer, request, queries, useWildcard)
			return
		}

		stemmed, err := snowball.Stem(query, "english", true)
		if err != nil {
			return
		}
		log.Println("Request received:", request.Method, request.URL.Path, "with stemmed query term:", stemmed)
		hits := s.GetHits(stemmed, useWildcard)

		images := s.GetImageHits(stemmed, useWildcard)

		searchResults := SearchResults{
			HitsResults:  hits,
			ImageResults: images,
			NoHits:       len(hits) == 0,
			NoImages:     len(images) == 0,
			Term:         stemmed,
			Wildcard:     useWildcard,
		}

		err = Render(writer, "../static/search.html", searchResults)
		if err != nil {
			log.Println("Error rendering search.html:", err)
			return
		}
	}
}

func (s *SearchEngine) HandleBigrams(writer http.ResponseWriter, request *http.Request, queries []string, useWildcard bool) {

	stemmed1, err := snowball.Stem(queries[0], "english", true)
	if err != nil {
		return
	}
	stemmed2, err := snowball.Stem(queries[1], "english", true)
	if err != nil {
		return
	}

	log.Println("Request received for bigram:", request.Method, request.URL.Path, "with stemmed query terms:", stemmed1, stemmed2)
	hits := s.GetBigramHits(stemmed1, stemmed2, useWildcard)

	searchResults := SearchResults{
		HitsResults:  hits,
		ImageResults: ImageHits{}, // Not supporting bigram images
		NoHits:       len(hits) == 0,
		NoImages:     true,
		Term:         stemmed1 + " " + stemmed2,
		Wildcard:     useWildcard,
	}

	err = Render(writer, "../static/search.html", searchResults)
	if err != nil {
		log.Println("Error rendering search.html:", err)
		return
	}
}

func Render(writer http.ResponseWriter, templateName string, data interface{}) error {
	return template.Must(template.ParseFiles(templateName)).Execute(writer, data)
}

func RenderError(writer http.ResponseWriter, name string) error {
	return template.Must(template.New("error").Parse(`<html><body><h1>Error</h1><p>{{.}}</p></body></html>`)).Execute(writer, name)
}
