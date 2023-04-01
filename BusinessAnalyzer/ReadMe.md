Hello! This folder contains Assignment 02 for Data Structures and Algorithms (CS245).

_____

My implementation contains a custom object type that utilized in the desired custom ArrayList or LinkedList implemenation.

| Object | Description |
| --- | --- |
| DataObject | Custom object that has String Zipcode, NAICS, Business Account Number, and Neighborhood components |

_____

This implementation uses two primary methods the first being ReadData() which initially parses the Registered SF Businesses CSV and the second being InputHandler() which has 4 helper functions.

| Method | Description | Time | Space |
| --- | --- | --- | --- |
| ReadData() | Reads Registered_Business_Locations_-_San_Francisco.csv and populates the list (arraylist or linked list depending on user input). | O(n) | O(n) |
| InputHandler() | Handles user input with helper functions. | O(n) | O(n) |
| HistoryHandler() | Helper functinon that prints out the user's command/input history. | O(n) | O(1) |
| SummaryHandler() | Helper functinon that prints the total amount of businesses and businesses closed. | O(1) | O(1) |
| NaicsHandler() | Helper function that handles and prints information related to the naics code input | O(n) | O(n) |
| ZipHandler() | Helper function that handles and prints information related to the zip code input | O(n) | O(n) |


[back to main page](https://github.com/shooby-d/projects) 

