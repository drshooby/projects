# Python Bubble Sort
# By David S
# This file sorts 10,000 integers using the bubble sorting method

import random
import time

class Bubble:

    def __init__(self) -> None: # "-> None" tells the constructor to not return anything
        self.Length = 10000 #class variable, 10k different numbers

    def SetValues(self, values):
        L = len(values)
        for i in range(L):
            values[i] = random.randint(0,4*L) 

    def doSort(self, values):
        for i in range(len(values) - 1, 1, -1):
            j = 1
            while j <= i:
                if values[j-1] > values[j]:
                    tmp = values[j-1]
                    values[j-1] = values[j]
                    values[j] = tmp
                j = j+1

def main():
    mySort = Bubble() #creating Bubble object in Python
    values = [None] * mySort.Length #creating empty list of Length length

    mySort.SetValues(values)
    startTime = round(time.time() * 1000)
    mySort.doSort(values)
    endTime = round(time.time() * 1000)

    timeToPrint = (endTime - startTime) / 1000.0

    print(f"Time to sort: {timeToPrint} seconds")
    print("Sort ready for testing in TestBubble.java")

    with open('sort.txt', 'w') as myfile:
        i = 0
        while i < len(values):
            myfile.write(str(values[i]) + " ")
            i = i + 1
     
if __name__ == "__main__": 
    main()