package project06

import (
	"log"
	"net/url"
	"strings"
)

// CleanHref cleans the href and returns the cleaned href and a bool indicating whether the href is valid
func CleanHref(hostname, href string) (string, bool) {
	if strings.HasPrefix(href, "mailto:") {
		// ignore mail addresses
		log.Printf("ignored mail address in cleanhref: %s\n", href)
		return "", false
	}

	parsedHref, err := url.Parse(href)
	if err != nil {
		log.Println(err)
		return "", false
	}
	hn := parsedHref.Hostname()
	parsedHost, err := url.Parse(hostname)
	if len(hn) == 0 {
		if err != nil {
			log.Println(err)
			return "", false
		}
		parsedHref.Scheme = parsedHost.Scheme
		parsedHref.Host = parsedHost.Host
	} else if hn != parsedHost.Hostname() {
		// stay within the host
		log.Println("href is not within the host: ", href)
		return "", false
	}

	if len(parsedHref.Path) == 0 && len(parsedHref.Fragment) != 0 {
		parsedHref.Path = parsedHost.Path
		parsedHref.Fragment = ""
	}

	return parsedHref.String(), true
}

func IsImage(url string) bool {
	extensions := []string{".jpg", ".jpeg", ".png", ".gif", ".svg", ".JPG", ".JPEG", ".PNG", ".GIF", ".SVG"} // add more if necessary
	for _, e := range extensions {
		if strings.HasSuffix(url, e) {
			return true
		}
	}
	return false
}

func IsHTML(url string) bool {
	return strings.HasSuffix(url, ".html")
}
