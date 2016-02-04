# AssociationRulesGeneration
    
####Objective
The purpose of the project is to write a program to generate all association rules whose support 
is greater than a user-supplied minimum support and whose confidence is greater than a user supplied 
minimum confidence. I need to implement the recursive database-projection based algorithm that 
instructor described in class and is included in the lecture slides. This class of algorithms are 
also describe in Charu's textbook in section 4.4.3.2 (though the description there is more general 
than the method that instructor presented in class).

My program take as command line option five parameters: (i) the minimum support, (ii) the minimum confidence, (iii) the name of the input file, (iiii) the name of the output file, and (iv) options. The specific parameter sequence along with the name of the executable are as follows:   
```
java hcrminer minsup minconf inputfile outputfile options   
```


The options parameter will be a numerical value whose meaning is as follows:   
```
options = 1   
  The numbering of the items coming from the input file is used as the lexicographical ordering of the items.   
   
options = 2   
  The lexicographical ordering of the items is determined by sorting the items in increasing frequency order in each projected database.   
   
options = 3   
    The lexicographical ordering of the items is determined by sorting the items in decreasing frequency order in each projected database.   
```
   
####Input file format
The input file consists of a set of lines, each line containing two numbers. The first number is the transaction ID and the second number is the item ID. The lines in the file are ordered in increasing transaction ID order. Note that the set of items that make up the transaction will be derived by combining the item IDs of all the lines that correspond to the same transaction ID.   
   
Two input files are provided. The first is a small one that you can use during initial code development. The second is larger and will be the one on which you need to report performance results.   
   
####Output file format
The output file will contain as many lines as the number of high-confidence frequent rules that you found. The format of each line will be as follows:   
```
LHS|RHS|SUPPORT|CONFIDENCE  
```
Both LHS and RHS will contain the items that make up the left- and right-hand side of the rule in a space delimited fashion.   
   


