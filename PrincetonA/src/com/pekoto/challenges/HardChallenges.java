package com.pekoto.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class HardChallenges {
    
    /*
     * Adds two numbers without the plus operator.
     * 
     * Point: Normally when we add two numbers, we add the digits and the carry.
     *        We can split these steps up, adding the digits ignoring the carry,
     *        and then adding the digits taking only the carry.
     *        Then we add these two together using the same process until
     *        there is nothing left to carry.
     *        
     *  759                759            323
     * +674               +674    -->    1110
     *  323 (no carry)    1110 (carry)   1433 (nothing left to carry, so we're done)
     *  
     * In binary we can replicate this process:
     * 1. Add step: We have a 0 when both bits are 0 or 1 (XOR) (Both being 1 would result in a carry)
     * 2. Carry step: We have a 1 if i-1 of both a and b are 1 (Take bits where both are 1, then carry to left)
     */
    public static int addWithoutPlus(int a, int b) {
        while(b != 0) {
            int sum = a ^ b;    // Add without carry
            int carry = (a & b) << 1;     // Carry without add
            a = sum;
            b = carry;
        }
        
        return a;
    }
    
    /*
     * Returns a random set of m integers from an array
     * Essentially the same as Knuth's shuffle algorithm.
     */
    public static int [] getRandomSet(int [] arr, int m) {
        int [] randomSet = new int[m];
        
        for(int i = 0; i < m; i++) {
            randomSet[i] = arr[i];
        }
        
        Random rand = new Random();
        
        for(int i = m; i < arr.length; i++) {
            int randomIndex = rand.nextInt(i+1);
            
            if(randomIndex < m) {
                randomSet[randomIndex] = arr[i];
            }
        }
        
        return randomSet;
    }
    
    /*
     * Gets all subarrays. This is O(n^2), so rarely optimal,
     * but it can be a useful brute force solution if you can't
     * think of any other solution.
     * 
     * It starts checking from the longest subarray, which is generally
     * what is asked for.
     * 
     * Note: This assumes you want at least 2 elements in the subarray
     */
    public static List<int[]> getAllSubarrays(int [] arr) {
        
        List<int[]> subarrays = new ArrayList<int[]>();
        
        for(int subarrayLength = arr.length-1; subarrayLength > 0; subarrayLength--) {
            
            // I.e., for a length 5 array, will be 0 on first iteration (1 length 5 array)
            // then 1 for second iteration (2 length 4 arrays), etc.
            int startOffsetCount = arr.length-1-subarrayLength;
            
            for(int start = 0; start <= startOffsetCount; start++) {
                // Do some checking in this subarray
                // E.g., equal number of letters and numbers
                subarrays.add(getSubArray(arr, start, start + subarrayLength));
            }
        }
        
        return subarrays;
    }
    
    private static int[] getSubArray(int [] arr, int start, int end) {
        int [] subarray = new int[end-start+1];
        
        for(int i = start; i <= end; i++) {
            int subarrayIndex = i-start;
            subarray[subarrayIndex] = arr[i];
        }
        
        return subarray;
    }
    
    /* 
     * Counts the number of 2s in the range 0...n
     */
    public static int getNumOf2s(int n) {
        int count = 0;
        
        int len = String.valueOf(n).length();
        
        for(int digit = 0; digit < len; digit++) {
            count += count2sInRangeAtDigit(n, digit);
        }
        
        return count;
    }
    
    private static int count2sInRangeAtDigit(int n, int digit) {
        int powerOf10 = (int) Math.pow(10, digit);
        int nextPowerOf10 = powerOf10 * 10;
        int right = n % powerOf10;
        
        int roundDown = n - n % nextPowerOf10;
        int roundUp = roundDown + nextPowerOf10;
        
        int d = (n / powerOf10) % 10;
    
        if(d < 2) {
            return roundDown / 10;
        } else if (d == 2) {
            return roundDown / 10 + right + 1;
        } else {
            return roundUp / 10;
        }
    }
    
    /*
     * Gets the list of name frequencies, grouped by name synonym
     * 
     * 1. Construct a graph where each node contains a frequency count
     * 2. Connected the graph nodes based on synonym
     * 3. Perform a DFS on each connected component to get the true count
     * 
     * Performance: O(N+P), where N = number of names and P = pairs of synonyms
     * 
     */
    public static HashMap<String, Integer> getTrueFrequencies(HashMap<String, Integer> nameFrequencies, String[][] synonyms) {
        NameGraph graph = constructGraph(nameFrequencies);
        connectEdges(graph, synonyms);
        HashMap<String, Integer> trueFrequencies = getTrueFrequencies(graph);
        
        return trueFrequencies;
    }
    
    // Create graph nodes keys on name, with value of frequency
    private static NameGraph constructGraph(HashMap<String, Integer> nameFrequencies) {
        NameGraph graph = new NameGraph();
        
        for(Entry<String, Integer> entry: nameFrequencies.entrySet()) {
            graph.addNode(entry.getKey(), entry.getValue());
        }
        
        return graph;
    }
    
    // Connect edges based on synonym list
    private static void connectEdges(NameGraph graph, String[][] synonyms) {
        for(String [] entry : synonyms) {
            graph.addEdge(entry[0], entry[1]);
        }
    }
    
    // Do a DFS of each connected component
    private static HashMap<String, Integer> getTrueFrequencies(NameGraph graph) {
        HashMap<String, Integer> trueFrequencies = new HashMap<String, Integer>();
        
        for(GraphNode node: graph.getNodes()) {
            if(!node.visited()) {
                int frequency = getComponentFrequency(node);
                String name = node.getName();
                trueFrequencies.put(name, frequency);
            }
        }
        
        return trueFrequencies;
    }
    
    // Do a DFS of a component to get the total frequency for this component
    private static int getComponentFrequency(GraphNode node) {
        if(node.visited()) {
            return 0;
        }
        
        node.setVisited(true);
        
        int sum = node.getFrequency();
        
        for(GraphNode neighbour : node.getNeighbours()) {
            sum += getComponentFrequency(neighbour);
        }
        
        return sum;
    }
}
