/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcrminer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
/**
 *
 * @author rainzhang
 */
public class Main 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        //parameters
        FPTree fptree = new FPTree();
        fptree.minsup = 100;
        fptree.minconf = 0.9;
        fptree.option = "3";
        String inputfile = "large";
        String outputfile = "output.txt";
        
        //get all transactions
        List<List<String>> r = fptree.getDB(inputfile);
        
        //get all supports and store into Containers
        fptree.FPGrowth(r, null);
        
        //print rules to output file
        fptree.PrintResult(r, outputfile);
    }
}
