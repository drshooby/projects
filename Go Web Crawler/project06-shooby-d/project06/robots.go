package project06

import (
	"bufio"
	"bytes"
	"log"
	"regexp"
	"strconv"
	"time"
)

func (s *SearchEngine) Disallow() {

	s.host.Path = "/robots.txt"
	body := s.DownloadRobots(s.host.String())
	if body == nil {
		return
	}

	s.sitemap.sm = s.ParseRobots(body)
}

// ParseRobots parses the robots.txt file and stores the records in the robotsTxt struct of the search engine
func (s *SearchEngine) ParseRobots(body []byte) string {
	scanner := bufio.NewScanner(bytes.NewReader(body))

	userAgentRegex := regexp.MustCompile(`User-agent:\s*(.+)`)
	disallowRegex := regexp.MustCompile(`Disallow:\s*(.+)`)
	delayRegex := regexp.MustCompile(`Crawl-delay:\s*(.+)`)
	mapRegex := regexp.MustCompile(`Sitemap:\s*(.+)`)

	userAgent := ""
	disallowedPaths := make([]string, 0)

	var sitemap string

	for scanner.Scan() {
		line := scanner.Text()

		if matches := userAgentRegex.FindStringSubmatch(line); len(matches) > 0 {
			s.HandleUserAgent(userAgent, disallowedPaths)
			userAgent = matches[1]
		} else if matches := disallowRegex.FindStringSubmatch(line); len(matches) > 0 {
			disallowedPaths = append(disallowedPaths, matches[1])
		} else if matches := delayRegex.FindStringSubmatch(line); len(matches) > 0 {
			s.HandleCrawlDelay(userAgent, matches[1])
		} else if matches := mapRegex.FindStringSubmatch(line); len(matches) > 0 {
			sitemap = matches[1]
		}
	}

	s.HandleUserAgent(userAgent, disallowedPaths)
	log.Println("robots.txt parsed successfully")
	return sitemap
}

func (s *SearchEngine) HandleUserAgent(userAgent string, disallowedPaths []string) {
	if len(disallowedPaths) > 0 {
		s.robotsTxt.AgentDisallowRules[userAgent] = disallowedPaths
	}
}

func (s *SearchEngine) HandleCrawlDelay(userAgent, crawlDelay string) {
	if delay, err := strconv.ParseFloat(crawlDelay, 64); err == nil {
		delay = delay * 1000 // convert to milliseconds
		s.robotsTxt.AgentDelays[userAgent] = time.Duration(delay) * time.Millisecond
	} else {
		log.Printf("unable to parse crawl delay: %v\n", err)
	}
}
