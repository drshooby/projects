Hello! This folder contains Assignment 01 for Data Structures and Algorithms (CS245).

My implementation contains two primary variables that utilize the ArrayList data structure.

| ArrayList | Description |
| --- | --- |
| movieList | Contains all custom "Movie" objects that are sorted by tags/genres |
| copy | Contains the exact same elements as movieList but is sorted by tag/genre counts |

This implementation also utilizes two sorting algorithms as well as binary and linear search

| Method | Description | Time | Space |
| --- | --- | --- | --- |
| main() | Reads tags.csv and handles user input. | O(n) | O(n) |
| leaderboard() | Prints most and least popular tags. | O(1) | O(1) |
| sort() | Modified insertion sort which sorts alphabetically by tags as tags.csv is read. Utlizes a linear search to insert Movie objects into movieList. | O(n) | O(n) |
| mergesort() | Sorts tags by count in copy. | O(nlgn) | O(n) |
| BinarySearchTags() | searches movieList for a tag count given a tag/genre. | O(lgn) | O(1) |
| BinarySearchCount() | searches copy for tag(s) given a tag count. | O(lgn) | O(1) |


[back to main page](https://github.com/shooby-d/projects) 
