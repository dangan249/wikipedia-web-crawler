# wikipedia-web-crawler

## Web-Crawler + Focused Crawler 

#### Crawling the documents:

* Start with the following seed URL:
http://en.wikipedia.org/wiki/Sustainable_energy; a Wikipedia article about
green energy.

* Crawler respect the politeness policy by using a delay of at least
one second between HTTP requests.

* Follow the links with the prefix http://en.wikipedia.org/wiki that lead to
articles only (avoid administrative links containing : ). Non-English articles
and external links must not be followed.

* Crawl to depth 5. The seed page is the first URL in the frontier and thus
counts for depth 1.

* Stop once it’ve crawled 1000 unique URLs. Keep a list of these URLs in a
text file. Also, keep the downloaded documents (raw html, in text format)
with their respective URL for future tasks (transformation and indexing)

#### Focused Crawling:
Crawler should be able to consume two arguments: a URL and a keyword to be
matched against text, anchor text, or text within a URL. Starting with the same seed
in Task 1, crawl to depth 5 at most, using the keyword “solar”. Crawler return at
most 1000 URLS for each of the following:
* Breadth first crawling
* Depth first crawling

## Link Analysis and PageRank Implementation
```
// P is the set of all pages; |P| = N
// S is the set of sink nodes, i.e., pages that have no out links
// M(p) is the set (without duplicates) of pages that link to page p
// L(q) is the number of out-links (without duplicates) from page q
// d is the PageRank damping/teleportation factor; use d = 0.85 as a fairly typical value

foreach page p in P
  PR(p) = 1/N /* initial value */
while PageRank has not converged do
  sinkPR = 0
  foreach page p in S /* calculate total sink PR */
    sinkPR += PR(p)
  foreach page p in P
    newPR(p) = (1-d)/N /* teleportation */
    newPR(p) += d*sinkPR/N /* spread remaining sink PR evenly */
    foreach page q in M(p) /* pages pointing to p */
      newPR(p) += d*PR(q)/L(q) /* add share of PageRank from in-links */
    foreach page p
      PR(p) = newPR(p)
Return and output final PR score.
```
