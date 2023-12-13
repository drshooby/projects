package project06

import (
	"log"
	"net/http"
)

func (s *SearchEngine) Serve() {

	http.HandleFunc("/", func(writer http.ResponseWriter, request *http.Request) {
		log.Println("Request received:", request.Method, request.URL.Path)
		if request.Method == http.MethodGet {
			http.ServeFile(writer, request, "../static/index.html")
		}
	})

	// serve CSS files
	http.Handle("/styles/", http.StripPrefix("/styles/", http.FileServer(http.Dir("../static/styles"))))

	// serve files from the "top10" directory
	http.Handle("/top10/", http.StripPrefix("/top10/", http.FileServer(http.Dir("../top10"))))

	// handle search
	http.HandleFunc("/search/", s.HandleSearch)
}
