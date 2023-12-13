package project06

import (
	"fmt"
	"log"
	"net/url"
	"regexp"
	"strings"
	"sync"
	"time"

	"github.com/kljensen/snowball"
)

type (
	imageData struct {
		src       string
		alt       string
		alt_parts []string
	}

	extracting struct {
		words   []string
		links   []string
		url     string
		title   string
		im_data []imageData
	}

	downloading struct {
		body []byte
		url  string
	}
)

var delay time.Duration
var re = regexp.MustCompile(`[^\p{L}0-9 ]+`)

// Crawl crawls the web starting from the seed url
func (s *SearchEngine) Crawl(seed string) {

	// only does something when we use the db indexer
	s.OpenDB()
	defer s.CloseDB()

	EC := make(chan extracting, 5000)
	DC := make(chan downloading, 500)
	LC := make(chan string, 20)
	quit := make(chan struct{})

	var wg sync.WaitGroup

	// if our host is not a valid url then we cant Crawl
	u, errHost := url.Parse(seed)

	if errHost != nil {
		log.Println("error parsing seed url in crawl")
		return
	}

	// set the host
	s.host = u // the path will be overwritten in Disallow()
	s.Disallow()

	// check if the go client exists in the robots.txt file
	// otherwise use the wildcard

	func() {
		_, ok := s.robotsTxt.AgentDelays["Go-http-client/1.1"]
		if ok {
			s.ua = "Go-http-client/1.1"
			delay, ok = s.robotsTxt.AgentDelays[s.ua]
			if !ok {
				delay = 100 * time.Millisecond
			}
		} else {
			s.ua = "*"
			delay, ok = s.robotsTxt.AgentDelays[s.ua]
			if !ok {
				delay = 100 * time.Millisecond
			}
		}
	}()

	urls := s.ParseSitemap()
	log.Printf("User-agent set to %s, crawl-delay set to %v, and sitemap url set to %s\n", s.ua, delay, s.sitemap.sm)
	fmt.Println("Number of urls in sitemap:", len(urls))

	for i := 0; i < s.downloaders; i++ {
		wg.Add(1)
		go Download(LC, DC, &wg)
	}

	for i := 0; i < s.extractors; i++ {
		wg.Add(1)
		go Extract(DC, EC, &wg, seed) // pass the seed every time otherwise it'll break (assuming seed comes with a scheme)
	}

	// clean and directly push all links into the link channel
	// use for lab07
	s.CleanAndEnqueue(seed, s.ua, LC, urls)

	go s.HeartbeatRoutine(quit, EC, DC, LC)

	log.Println("Beginning crawl...")

	LC <- seed
	s.vis[seed] = struct{}{}

CrawlLoop:
	for {
		select {
		case <-quit:
			wg.Wait()
			log.Println("Crawl finished.")
			log.Printf("Total docs parsed: %d\n", len(s.vis))
			log.Println("Analyzing database...")
			s.AnalyzeOptimalQuerying()
			s.ClosePreparedStatements()
			log.Println("Database analysis finished.")
			break CrawlLoop
		case d := <-EC:
			s.hbc++
			s.UpdateFreq(d.words, d.url, d.title, d.im_data)
			// commenting out clean and enqueue for now because it will be used directly with the sitemap
			//s.CleanAndEnqueue(s.host.String(), s.ua, LC, d.links)
		}
	}
}

// ActivateDelay activates the delay specified in the robots.txt file
func (s *SearchEngine) ActivateDelay() {
	// default http get delay is 500ms
	time.Sleep(delay)
}

// IsAllowedLink checks if the link is allowed by the robots.txt file
func (s *SearchEngine) IsAllowedLink(link, ua string) bool {

	disallowedPaths, ok := s.robotsTxt.AgentDisallowRules[ua]
	// rules don't exist
	if !ok {
		return true
	}

	// check if link matches any disallowed regex pattern
	for _, path := range disallowedPaths {

		// convert the rule to a regular expression
		regexPattern := strings.ReplaceAll(path, "*", ".*") // Replace wildcard * with .* in regex
		match, err := regexp.MatchString(regexPattern, link)
		if err != nil {
			log.Printf("error with regex: %v\n", err)
			return true // if there's an error in the regex we assume the link is allowed
		}
		if match {
			log.Printf("excluding disallowed link: %s\n", link)
			return false
		}
	}

	return true
}

// CleanWords clean the words, remove random punctuation and stem
func CleanWords(word string) (string, error) {
	// regex idea from https://gosamples.dev/remove-non-alphanumeric/
	// regex defined globally to avoid repetitive calls to regexp.Compile
	clean := re.ReplaceAllString(word, "")

	stemmed, err := snowball.Stem(strings.TrimSpace(clean), "english", true)
	if err != nil {
		return "", err
	}
	return stemmed, nil
}

// CleanAndEnqueue cleans the links and enqueues them
func (s *SearchEngine) CleanAndEnqueue(host, ua string, LC chan<- string, links []string) {
	for _, link := range links {
		cleanLink, ok := CleanHref(host, link)
		if !ok {
			continue // skip invalid links
		}
		if !s.IsAllowedLink(cleanLink, ua) {
			continue
		}
		if _, seen := s.vis[cleanLink]; !seen {
			// more accurate way to check for visited links is by checking the database
			// but this works well for caching results
			s.vis[cleanLink] = struct{}{}
			s.ActivateDelay()
			LC <- cleanLink
		}
	}
}
