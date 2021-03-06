# TF-IDF-Search-Engine


## About App
This is a  TF-IDF document Search Engine in Java 14, built as a RESTful Web Service with Spring Boot/Hibernate H2 in memory database.

The search engine is implemented as an inverted index (http://en.wikipedia.org/wiki/Inverted_index) that runs in memory, and returns a result list that is sorted by TF-IDF (http://en.wikipedia.org/wiki/Tf*idf).

The search engine can:
* take in a list of documents
* support searches for single terms in the document set
(https://en.wikipedia.org/wiki/Lexical_analysis#Tokenization)
* return a list of matching documents together with their TF-IDF score
* support sorting by TF-IDF

## You can
* **Create a new document**
```
POST - http://localhost:8080/documents
```
```JSON
[
    {
        "text":"The brown fox jumped over the brown dog."
        },
           {
        "text": "The lazy brown dog, sat in the other corner"
        },
            {
        "text": "The Red Fox bit the lazy dog!"
        }
]
```
Return the newly saved documents -
```JSON
[
    {
        "documentId": 1,
        "text": "The brown fox jumped over the brown dog."
    },
    {
        "documentId": 2,
        "text": "The lazy brown dog, sat in the other corner"
    },
    {
        "documentId": 3,
        "text": "The Red Fox bit the lazy dog!"
    }
]
```


* **Search for single terms in the document set**
```
GET - http://localhost:8080/documents/{word}
```
i.e.
```
 http://localhost:8080/documents/brown
```
Returns - documents that include the word 'brown' along with the documents TF-IDF, sorted in order of TF-IDF.
```
{
    "document 1": 0.22907269,
    "document 2": 0.10181008
}
```


## Spec
* Accepts JSON
* Response in JSON
* JDK14 or higher
* Build with Maven
* Data storage: (in memory) database
* Lombok has been used to reduce boilerplate code

## Running
* Run as a Spring Boot App

