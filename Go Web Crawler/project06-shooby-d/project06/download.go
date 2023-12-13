package project06

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"sync"
)

// Download downloads the body of a url and sends it to the Extract goroutine
func Download(LC <-chan string, DC chan<- downloading, wg *sync.WaitGroup) {
	defer wg.Done()

	for url := range LC {

		fmt.Println("download: url=" + url)
		if rsp, err := http.Get(url); err == nil {
			if rsp.StatusCode == 200 {
				if bts, err := io.ReadAll(rsp.Body); err == nil {
					DC <- downloading{bts, url}
				} else {
					log.Printf("unable to read body of url %s: %v", url, err)
				}
			} else {
				log.Printf("download: result=failed...received status code %s for url: %s.\n", rsp.Status, url)
			}
		} else {
			log.Printf("download: result=failed...unable to get url %s: %v", url, err)
		}
	}
}

// DownloadRobots downloads the robots.txt file and returns the body as a byte slice
func (s *SearchEngine) DownloadRobots(u string) []byte {
	if rsp, err := http.Get(u); err == nil {
		if rsp.StatusCode == 200 {
			if bts, err := io.ReadAll(rsp.Body); err == nil {
				return bts
			} else {
				log.Printf("unable to read body of robots.txt: %v.\n", err)
			}
		} else {
			log.Printf("download of robots.txt failed...received status code %s for url: %s.\n", rsp.Status, u)
		}
	} else {
		log.Printf("download of robots.txt failed...unable to get url %s: %v", u, err)
	}

	log.Println("using defaults for robots.txt (500ms crawl delay, no disallow rules)")
	return nil
}
