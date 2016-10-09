# Cube Mergability


This research prototype implements several mergability measures for [RDF Data Cubes] (https://www.w3.org/TR/vocab-data-cube/). The measures are bases on the structure definitions of the cubes and consider syntactic, hierarchical, and semantic information in the mergability computation. The mergability value describes how easy it is to merge two cubes. It is computed by aggreging the pairwise comparison of the cubes's components or dimensions. In addition a bijective mapping is found.

### Implementation

- Similarity measures
  - Label similarity (String distance)
  - Concept equality (considering sameAs relationships)
  - Concept similarity 
    - Based on paths in the DBpedia category and DBpedia page-link graph
    - Word2Vec
- Aggregation approaches
  - Sum with boosting weights
  - Hungarian algorithm
- Datahub Crawler to collect all available strucuture definitons in the linked data cloud
- Evaluation of the implemented approaches

### Datasets

Two dataset are created for the evaluation of this approach

- Set 1: Based on the German Reich statistics, fully disambiguated with DBpedia concepts
- Set 2: Crawl of the structure definition in the linked data cloud


### License

[MIT License](../../blob/master/LICENSE)
