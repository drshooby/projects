package project06

import (
	"bytes"
	"log"
	"strings"
	"sync"
	"unicode"

	"golang.org/x/net/html"
)

// Extract extracts words and links from the body of a url and sends them back to Crawl in the main goroutine
func Extract(DC <-chan downloading, EC chan<- extracting, wg *sync.WaitGroup, hn string) {

	defer wg.Done()

	for body := range DC {

		reader := bytes.NewReader(body.body)
		doc, err := html.Parse(reader)
		if err != nil {
			log.Printf("error with parsing in extract: %v", err)
		}

		wordsToReturn := make([]string, 0)
		linksToReturn := make([]string, 0)
		imageDataToReturn := make([]imageData, 0)
		title := ""

		var f func(*html.Node)
		f = func(n *html.Node) {
			switch n.Type {
			case html.ElementNode:
				var src, alt string
				var alt_parts []string
				for _, attr := range n.Attr {
					if attr.Key == "href" {
						linksToReturn = append(linksToReturn, attr.Val)
					}
					if n.Data == "img" { // if the node is an image because scripts and styles can have src attributes too
						if attr.Key == "src" {
							cleanedSrc, ok := CleanHref(hn, attr.Val)
							if ok {
								src = cleanedSrc // get the full link to the image
							}
						} else if attr.Key == "alt" {
							alt = attr.Val
							alt_parts = strings.FieldsFunc(alt, func(r rune) bool {
								return !unicode.IsLetter(r) && !unicode.IsNumber(r)
							})
						}
					}
				}
				if len(alt) != 0 { // if the image has an alt attribute its valid
					imageDataToReturn = append(imageDataToReturn, imageData{src, alt, alt_parts})
				}

			case html.TextNode:
				p := n.Parent
				if p.Type == html.ElementNode && (p.Data != "style" && p.Data != "script") {
					if p.Data == "title" {
						title = n.Data
					}
					words := strings.FieldsFunc(n.Data, func(r rune) bool {
						return !unicode.IsLetter(r) && !unicode.IsNumber(r)
					})
					wordsToReturn = append(wordsToReturn, words...)
				}
			}

			for c := n.FirstChild; c != nil; c = c.NextSibling {
				f(c)
			}
		}
		f(doc)
		EC <- extracting{wordsToReturn, linksToReturn, body.url, title, imageDataToReturn}
	}
}
