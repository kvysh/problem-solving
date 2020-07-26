This program to find the average time per transaction, from series of transaction logs which is in following format:

###### transaction.log

    T1234, 2020 - 03 - 01 , 3:15 pm, start 
    T1235, 2020 - 03 - 01 , 3:16 pm, start 
    T1236, 2020 - 03 - 01 , 3:17 pm, start 
    T1234, 2020 - 03 - 01 , 3:18 pm, End 
    T1235, 2020 - 03 - 01 , 3:18 pm, End

##### Prerequisite:
Java JDK/JRE (8 & above)

##### Steps to run: (given compiled java class)

    java AverageTimePerTransaction 

##### Sample Input:
Console will be printed with "Please give the file path"

    /path/to/transaction.log

##### Sample Output (in minutes):
	
    Average time per transaction: 2


