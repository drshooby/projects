Hello! This folder contains Assignment 03 for Data Structures and Algorithms (CS245).

_____

My implementation contains a custom object type that is utilized as vertices for the Graph.

| Object | Description |
| --- | --- |
| GraphNode | Custom object that has a String name for the associated email and a HashSet of GraphNode neighbors as components |

_____

This implementation has multiple important methods from each of the various project class files.

| Method | Description | Time | Space |
| --- | --- | --- | --- |
| HandlerHelper() | Handles user input. | O(n^2) | O(1) |
| FindConnectors() | Finds connector vertices in the graph using a custom DFS. | O(V + E) | O(1) |
| GetReceivedEmails() | Graph function that returns the number of email adresses from which an email received emails from. | O(n^2) | O(1) |
| Size() | Disjoint set function that finds the size of graph clusters. | O(1) | O(1) |
| ParseFiles() | Parses the Enron file dataset and creates a graph based on valid email addresses. | O(n^2) | O(n) |


[back to main page](https://github.com/shooby-d/projects) 

