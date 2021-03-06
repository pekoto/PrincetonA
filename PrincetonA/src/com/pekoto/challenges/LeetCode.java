package com.pekoto.challenges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.pekoto.datastructures.DirectedEdge;
import com.pekoto.datastructures.DirectedGraph;
import com.pekoto.datastructures.EdgeWeightedDirectedGraph;
import com.pekoto.test.challenges.Interval;

public class LeetCode {

    /**
     * Find the length of the longest unique substring in a string
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int start = 0;
        int maxLength = 0;

        HashMap<Character, Integer> charPos = new HashMap<Character, Integer>();

        for (int end = 0; end < s.length(); end++) {
            if (charPos.containsKey(s.charAt(end))) {
                start = Math.max(charPos.get(s.charAt(end)), start);
            }

            maxLength = Math.max(end - start + 1, maxLength);
            charPos.put(s.charAt(end), end + 1);
        }

        return maxLength;
    }

    /**
     * Find the median of 2 sorted arrays
     */
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {

        if (nums2.length < nums1.length) {
            return findMedianSortedArrays(nums2, nums1);
        }

        int left = 0;
        int right = nums1.length;

        while (left <= right) {

            int xPartition = left + (right - left) / 2;
            int yPartition = ((nums1.length + nums2.length + 1) / 2) - xPartition;

            int leftXMax = (xPartition == 0) ? Integer.MIN_VALUE : nums1[xPartition - 1];
            int rightXMin = (xPartition == nums1.length) ? Integer.MAX_VALUE : nums1[xPartition];

            int leftYMax = (yPartition == 0) ? Integer.MIN_VALUE : nums2[yPartition - 1];
            int rightYMin = (yPartition == nums2.length) ? Integer.MAX_VALUE : nums2[yPartition];

            if (leftXMax <= rightYMin && leftYMax <= rightXMin) {
                if ((nums1.length + nums2.length) % 2 == 0) {
                    return (Math.max(leftXMax, leftYMax) + Math.min(rightXMin, rightYMin)) / 2.0;
                } else {
                    return Math.max(leftXMax, leftYMax);
                }
            } else if (leftXMax > rightYMin) {
                right = xPartition - 1;
            } else {
                left = xPartition + 1;
            }
        }

        // Error, arrays were unsorted
        return -1.0;
    }

    /*
     * Returns nearest integer square root
     */
    public static int mySqrt(int x) {
        if (x == 0) {
            return 0;
        }

        int left = 1;
        int right = x;

        while (true) {
            int mid = left + (right - left) / 2;

            if (mid > x / mid) {
                // Sqrt must be <= x/sqrt
                right = mid - 1;
            } else if (mid + 1 > x / (mid + 1)) {
                return mid;
            } else {
                left = mid + 1;
            }
        }
    }

    /*
     * RegEx matcher using bottom-up dynamic programming. * = 0 or move chars . =
     * any single char
     * 
     * Uses a boolean table to check if the string matches the pattern at each
     * point. The pattern is the rows, and the string is the column.
     */
    public static boolean isMatch(String s, String p) {
        boolean[][] matches = new boolean[s.length() + 1][p.length() + 1];

        // An empty string matches and empty pattern
        matches[0][0] = true;

        // Set up the first row. This deals with patterns where
        // we could potentially match the empty string at various points
        // e.g., a* or a*b* or a*b*c*
        for (int i = 1; i < matches[0].length; i++) {
            // Remember we have a 1 offset before the pattern starts
            // a * b *
            // [ ] a * b *
            // T, F, T, F, T
            // 0, 1, 2, 3, 4
            if (p.charAt(i - 1) == '*') {
                matches[0][i] = matches[0][i - 2];
            }
        }

        for (int row = 1; row < matches.length; row++) {
            for (int col = 1; col < matches[0].length; col++) {
                if (p.charAt(col - 1) == '.' || p.charAt(col - 1) == s.charAt(row - 1)) {
                    // If the two chars match, or if the pattern is a .,
                    // then we can imagine just removing these 2 from the string and pattern
                    // -- i.e., use the value in the upper left.
                    matches[row][col] = matches[row - 1][col - 1];
                } else if (p.charAt(col - 1) == '*') {

                    // Can we match by just not taking any of the preceding character
                    matches[row][col] = matches[row][col - 2];

                    if (p.charAt(col - 2) == '.' || p.charAt(col - 2) == s.charAt(row - 1)) {
                        matches[row][col] = matches[row][col] || matches[row - 1][col];
                    }
                } else {
                    matches[row][col] = false;
                }
            }
        }

        return matches[s.length()][p.length()];
    }

    /*
     * Merge overlapping intervals Sort the intervals and then check of the start is
     * after the end.
     * 
     * Input: [[1,3],[2,6],[8,10],[15,18]] Output: [[1,6],[8,10],[15,18]]
     */
    public static List<Interval> merge(List<Interval> intervals) {
        if (intervals == null || intervals.size() == 0) {
            return new ArrayList<Interval>();
        }

        LinkedList<Interval> merged = new LinkedList<Interval>();

        Collections.sort(intervals, new IntervalComparator());

        for (Interval interval : intervals) {
            // If the end comes before the start, add this
            if (merged.isEmpty() || merged.getLast().end < interval.start) {
                merged.add(interval);
            } else {
                // Otherwise it ends after the next element starts, so we have an overlap
                // (remember intervals are sorted y first element)
                // -- set the end of the last element to be the max of the two elements
                merged.getLast().end = Math.max(merged.getLast().end, interval.end);
            }
        }

        return merged;
    }

    private static class IntervalComparator implements Comparator<Interval> {

        @Override
        public int compare(Interval arg0, Interval arg1) {
            if (arg0.start < arg1.start) {
                return -1;
            } else if (arg0.start == arg1.start) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /*
     * Quickly computers the power of x^n using the fast power method
     * 
     * This is based on the power property: X^2n = (X^n)^2 So if we have X^n/2, how
     * do we get X^n?
     * 
     * Well, if n is even, we can use (X^n)^2 = X^2*n If n is odd, we can use
     * (X^n)^2 = X^n-1 = (X^n/2)^2 * x
     * 
     * Since we are halving n each time... Performance: O(log n) Space: O(log n) --
     * for recursive calls (store the result of x^n/2 O(log n) times
     * 
     */
    public static double myPow(double x, int n) {
        long N = n;

        if (N < 0) {
            x = 1 / x;
            N = -N;
        }

        return fastPow(x, N);
    }

    private static double fastPow(double x, long n) {
        if (n == 0) {
            return 1.0;
        }

        double half = fastPow(x, n / 2);

        if (n % 2 == 0) {
            return half * half;
        } else {
            return half * half * x;
        }
    }

    /*
     * Returns the set of unique letters in a string with the lowest lexicographical
     * order
     * 
     * The String S contains all of the unique characters We find the leftmost index
     * of the lowest character, and take the substring of the remaining characters.
     * 
     * To make sure we don't cut off the substring and lose characters, we decrement
     * the count of each character. Once the count for a character hits 0, we break
     * out.
     * 
     * This gives us the position of the smallest character we can take, while
     * leaving a substring the contains the remaining unique characters.
     * 
     * Examples: abbcab > bbcb > c abcabc > bcbc > cc
     * 
     * O(n) -- recurse 26*n, where n is the number of letters in the string
     */
    public static String removeDuplicateLetters(String s) {

        if (s.length() == 0) {
            // base case
            return "";
        }

        int[] counts = new int[26];
        int posOfSmallest = 0; // Position of the smallest char in the string

        // Get the counts for each letter
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
        }

        for (int i = 0; i < s.length(); i++) {

            if (s.charAt(i) < s.charAt(posOfSmallest)) {
                posOfSmallest = i;
            }

            counts[s.charAt(i) - 'a']--;
            if (counts[s.charAt(i) - 'a'] == 0) {
                break;
            }
        }

        String restOfStringWithCharRemoved = s.substring(posOfSmallest + 1).replaceAll("" + s.charAt(posOfSmallest),
                "");
        return s.charAt(posOfSmallest) + removeDuplicateLetters(restOfStringWithCharRemoved);
    }

    /*
     * Returns the length of the longest consecutive sequence Runtime: O(n)
     * 
     * Note: This looks like quadratic time, but look carefully at the for loop. We
     * only start iterating our while loop from the lowest number in a sequence, so
     * each element should only get checked once, giving us an O(2N) runtime. *
     */
    public static int longestConsecutive(int[] nums) {

        if (nums.length == 0) {
            return 0;
        }

        Set<Integer> numSet = new HashSet<Integer>();

        for (int num : nums) {
            numSet.add(num);
        }

        int maxSequence = 1;

        for (int num : numSet) {
            // This ensures we only examine every
            // number once, starting from the lowest
            // element in the sequence
            if (!numSet.contains(num - 1)) {
                int sequenceLength = 1;
                int currentNum = num;

                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    sequenceLength++;
                }

                maxSequence = Math.max(maxSequence, sequenceLength);
            }
        }

        return maxSequence;
    }

    /*
     * Iterates through a matrix in spiral order
     */
    public static List<Integer> spiralOrder(int[][] matrix) {
        if (matrix.length == 0) {
            return new ArrayList<Integer>();
        }

        List<Integer> spiralOrder = new ArrayList<Integer>();

        int startRow = 0;
        int endRow = matrix.length - 1;
        int startCol = 0;
        int endCol = matrix[0].length - 1;

        while (startRow <= endRow && startCol <= endCol) {

            // Add the top row
            for (int col = startCol; col <= endCol; col++) {
                spiralOrder.add(matrix[startRow][col]);
            }

            // Add the right col
            for (int row = startRow + 1; row <= endRow; row++) {
                spiralOrder.add(matrix[row][endCol]);

            }

            // If the start and end rows are the same,
            // or start and end columns are the same, then we've already
            // added these elements.
            if (startRow < endRow && startCol < endCol) {

                // Add the bottom row
                for (int col = endCol - 1; col > startCol; col--) {
                    spiralOrder.add(matrix[endRow][col]);
                }

                // Add left col
                for (int row = endRow; row > startRow; row--) {
                    spiralOrder.add(matrix[row][startCol]);
                }
            }

            startRow++;
            endRow--;
            startCol++;
            endCol--;
        }

        return spiralOrder;
    }

    /*
     * Finds the next permutation that is the next lexagraphical ordering of
     * numbers.
     * 
     * 1. Find the smallest element starting from left (e.g., 1231 -- first smallest
     * is 2) 2. Go from the right, and find the first element larger than this
     * (e.g., 1231 -- next largest is 3) 3. Swap these 2 elements (e.g., 1321) 4.
     * Now to make it so it's the NEXT smallest permutation, sort everything after
     * the swap position (e.g., 1312)
     */
    public void nextPermutation(int[] nums) {
        if (nums.length == 0) {
            return;
        }

        int firstSmallerNum = nums.length - 2;

        while (firstSmallerNum >= 0 && nums[firstSmallerNum + 1] <= nums[firstSmallerNum]) {
            firstSmallerNum--;
        }

        if (firstSmallerNum >= 0) {
            int nextLargerNum = nums.length - 1;
            while (nextLargerNum >= 0 && nums[nextLargerNum] <= nums[firstSmallerNum]) {
                nextLargerNum--;
            }

            swap(nums, firstSmallerNum, nextLargerNum);
        }
        // else nums are reverse sorted, there is
        // no next permutation, so we return the sorted array

        reverse(nums, firstSmallerNum + 1);
    }

    private void reverse(int[] nums, int start) {
        int i = start;
        int j = nums.length - 1;

        while (i < j) {
            swap(nums, i, j);
            i++;
            j--;
        }
    }

    private void swap(int[] nums, int a, int b) {
        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp;
    }

    /*
     * Finds the minimum number of perfect squares that can be used to make n.
     * 
     * Build up an array of the min perfect squares that can be used to build up
     * every element in the array.
     * 
     * Take away the number squared, and add the minimum number of ways we can make
     * the remainder, which we already calculated dynamically.
     * 
     * Dynamic programming approach.
     */
    public static int numSquares(int n) {
        int[] numOfSquares = new int[n + 1];

        Arrays.fill(numOfSquares, Integer.MAX_VALUE);
        numOfSquares[0] = 0;

        for (int target = 1; target <= n; target++) {
            for (int square = 1; square * square <= target; square++) {
                // Think about it: +1 for square^2, and then + the number of ways we can make
                // whatever is left over. Hence we derive target-(square*square)-1
                numOfSquares[target] = Math.min(numOfSquares[target], numOfSquares[target - square * square] + 1);
            }
        }

        return numOfSquares[n];
    }

    /*
     * Evaluates a string as an arithmetic expression
     * 
     * We can get away with using a single stack if we evaluate the expression at
     * each point, and just keep track of the sign instead of a stack of operators.
     * 
     * 1 + 3 = 1 + (1*3) = 4 (sign = 1) 1 - 3 = 1 + (-1*3) = -2 (sign = -1)
     */
    public static int calculator(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int result = 0;
        int sumSoFar = 0;
        int sign = 1;
        Stack<Integer> stack = new Stack<Integer>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                // If we have a number, add it on to our current number
                sumSoFar = sumSoFar * 10 + (int) (c - '0');
            } else if (c == '+') {
                // If we have a plus, add on our sum so far * sign, then reset them
                result += sumSoFar * sign;

                sign = 1;
                sumSoFar = 0;
            } else if (c == '-') {
                // If we have a minus, add on our sum so far * sign, then set sign to -1
                // so next number will be subtracted
                result += sumSoFar * sign;

                sign = -1;
                sumSoFar = 0;
            } else if (c == '(') {
                // If we have a left bracket, push on the result and sign (saving sum we got
                // before
                // this expression, then reset the result and sign)

                stack.push(result);
                stack.push(sign);

                result = 0;
                sign = 1;
            } else if (c == ')') {
                // Once we get to the end of the expression,
                // add on the last value
                // reset the sum so far and sign
                // multiply it by the sign before this expression
                // add the result to the sign before this expression
                result += sumSoFar * sign;

                sign = 1;
                sumSoFar = 0;

                result *= stack.pop();
                result += stack.pop();
            }
        }

        // Deal with the last digits, if we have any
        if (sumSoFar != 0) {
            result += sumSoFar * sign;
        }

        return result;
    }

    /*
     * Finds the first missing positive integer
     * 
     * This solution is very similar to the "mark using negatives" solution pattern.
     * Except since we already have negatives in the array, we shift each element
     * into its "correct" (i-1) place. So 1 goes into element 0, 2 goes into element
     * 1, etc.
     * 
     * Then we check through the array. The first element not to equal i+1 is the
     * missing element.
     * 
     * Time: O(N) 1, 2, 0 Space: O(1)
     */
    public int firstMissingPositive(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            while (arr[i] > 0 && arr[i] <= arr.length && arr[arr[i] - 1] != arr[i]) {
                // arr[i] > 0 : can we shift this element into 0+ index (remember it will be
                // swapped with i-1)
                // arr[i] <= arr.length : can we shift this element into an index < length of
                // the array
                // arr[arr[i]-1] != arr[i] : is this number already in the correct place?
                // Because of this last condition, we will swap at most n times.
                swap(arr, i, arr[i] - 1);
            }
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != i + 1) {
                return i + 1;
            }
        }

        // Everything is sorted -- missing is off the end of the array
        return arr.length + 1;

    }

    /*
     * Returns a string representing a decimal number given two integers
     * representing a fraction
     * 
     * 1. If either number (not both) are -ve, append '-' to the StringBuilder 2.
     * Convert both numbers to the absolute long value 3. Append the number before
     * the decimal part 4. If the remainder is 0, just return 5. Otherwise set up a
     * map of remainder to position in fractional part -- if we get a repeating
     * remainder, surround it by () and return 6. While the remainder != 0 -- Put it
     * in the map -- Multiply it by 10 -- Append the new remainder / denominator --
     * Set the remainder to be the new remainder
     */
    public static String fractionToDecimal(int numerator, int denominator) {
        if (numerator == 0) {
            return "0";
        }

        StringBuilder stringBuilder = new StringBuilder();

        // If either one is negative (not both)
        // then the result will be -ve
        if (numerator < 0 ^ denominator < 0) {
            stringBuilder.append("-");
        }

        // Convert to Long or else abs(-2147483648) overflows
        // Also, since we already checked for -ve, make them positive.
        long longNumerator = Math.abs(Long.valueOf(numerator));
        long longDenominator = Math.abs(Long.valueOf(denominator));

        // Append the part before the decimal point
        stringBuilder.append(String.valueOf(longNumerator / longDenominator));

        long remainder = longNumerator % longDenominator;

        if (remainder == 0) {
            return stringBuilder.toString();
        }

        stringBuilder.append(".");

        // Map of remainder to position in fractional part
        Map<Long, Integer> map = new HashMap<>();

        while (remainder != 0) {

            if (map.containsKey(remainder)) {
                // We have a repeating fraction -- this already exists
                stringBuilder.insert(map.get(remainder), "(");
                stringBuilder.append(")");
                break;
            }

            map.put(remainder, stringBuilder.length());
            remainder *= 10;
            stringBuilder.append(String.valueOf(remainder / longDenominator));
            remainder %= longDenominator;
        }

        return stringBuilder.toString();
    }

    /*
     * Counts the number of prime numbers less than n
     */
    public static int countPrimes(int n) {
        boolean[] notPrime = new boolean[n];

        int count = 0;

        for (int i = 2; i < n; i++) {
            if (!notPrime[i]) {
                // I.e., is prime...
                count++;
            }

            for (int j = 2; i * j < n; j++) {
                // Since we make make this index by multiplying i*j
                // it can't be prime. This results in some duplicate
                // work but is quicker than checking from scratch.
                notPrime[i * j] = true;
            }
        }

        return count;
    }

    /*
     * Returns the number of possible mappings for a numerical string representing a
     * series of letters mapped to numbers. (A > 1, B > 2, etc.)
     * 
     * Input: "12" Output: 2 Explanation: It could be decoded as "AB" (1 2) or "L"
     * (12).
     * 
     * Basically we will use a dynamic programming approach starting from the back
     * of the string.
     */
    public static int numDecodings(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int[] memo = new int[s.length() + 1];

        memo[s.length()] = 1;

        // If the last char is 0, there are 0 ways to make this taking only 1
        // char at a time. Otherwise we can get there taking 1 char at a time.
        memo[s.length() - 1] = s.charAt(s.length() - 1) == '0' ? 0 : 1;

        for (int i = s.length() - 2; i >= 0; i--) {
            if (s.charAt(i) == '0') {
                // We can't start/break from 0
                continue;
            }

            // If the substring between i and i+1 is <= 26, we will be able to make it in 2
            // ways (e.g., 2+2 or 22)
            // Otherwise there is only one way we can make it (e.g., 3 + 3)
            memo[i] = Integer.parseInt(s.substring(i, i + 2)) <= 26 ? memo[i + 1] + memo[i + 2] : memo[i + 1];
        }

        return memo[0];
    }

    /*
     * Conway's Game of Life simulation
     * https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
     * 
     * 1: Live 2: Dead
     * 
     * So we can use 2 bits to represent [next state/current state] 00 (dead < dead)
     * 01 (dead < live) 10 (live < dead) 11 (live < live)
     * 
     * current state & 1 = current state current state >> 1 = next state
     */
    public void gameOfLife(int[][] board) {
        if (board == null || board.length == 0) {
            return;
        }

        // First, iterate through the board
        // and set the second bit
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {

                // Get the number of live neighbours
                int liveNeighbours = getLiveNeighbours(board, row, col);

                // To start, every 2nd bit is 0.
                // We only need to worry worry when it becomes 1.
                if (board[row][col] == 1 && liveNeighbours >= 2 && liveNeighbours <= 3) {
                    board[row][col] = 3; // Second bit 1 (01 > 11)
                }

                if (board[row][col] == 0 && liveNeighbours == 3) {
                    board[row][col] = 2; // Second bit 1 (00 > 10)
                }
            }
        }

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                board[row][col] >>= 1; // Get the next state
            }
        }
    }

    private int getLiveNeighbours(int[][] board, int row, int col) {
        int liveNeighbours = 0;

        // The max/min checks here are to check for out of bounds
        for (int neighbourRow = Math.max(row - 1, 0); neighbourRow <= Math.min(row + 1,
                board.length - 1); neighbourRow++) {
            for (int neighbourCol = Math.max(col - 1, 0); neighbourCol <= Math.min(col + 1,
                    board[0].length - 1); neighbourCol++) {
                liveNeighbours += board[neighbourRow][neighbourCol] & 1; // & 1 = Get current state
            }
        }

        liveNeighbours -= board[row][col] & 1;

        return liveNeighbours;
    }

    /*
     * Returns the permutations for an integer array
     */
    public static List<List<Integer>> getPermutations(int[] nums) {
        List<List<Integer>> results = new ArrayList<List<Integer>>();

        if (nums.length == 0) {
            return results;
        }

        getPermutations(nums, results, new ArrayList<Integer>(), 0);

        return results;
    }

    private static void getPermutations(int[] nums, List<List<Integer>> permutations, List<Integer> permutation,
            int index) {
        if (permutation.size() == nums.length) {
            permutations.add(permutation);
            return;
        }

        // For each position in this permutation
        for (int insertPosition = 0; insertPosition <= permutation.size(); insertPosition++) {
            List<Integer> newPermutation = new ArrayList<Integer>(permutation);
            // Insert the next number in every position
            newPermutation.add(insertPosition, nums[index]);
            // Recurse until we hit the size of the original array
            getPermutations(nums, permutations, newPermutation, index + 1);
        }
    }

    /*
     * Generate String permutations recursively (Non-base case and build method)
     * 
     * We can think of this like, take a prefix (E.g., A) and then append every
     * variant of the suffix (BC/CB) to it. Then move on to the next prefix.
     * 
     * Backtracking -- pick a char, remove it from your string and recurse
     */
    public static List<String> getPermutations(String s) {

        List<String> results = new ArrayList<String>();

        getPermutations("", s, results);

        return results;
    }

    private static void getPermutations(String prefix, String suffix, List<String> results) {
        if (suffix.length() == 0) {
            results.add(prefix);
            return;
        }

        for (int i = 0; i < suffix.length(); i++) {
            getPermutations(prefix + suffix.charAt(i),
                    suffix.substring(0, i) + suffix.substring(i + 1, suffix.length()), results);
        }
    }

    /*
     * Get permutations of array using a simpler backtracking method Like before, we
     * take an element as a prefix, and then generate the permutations of the
     * suffix. Because we are using an array, we have to clone it, and we swap
     * elements.
     * 
     * 1. Swap the start and i... 2. Generate permutations 3. Swap start and i back
     */
    public static List<int[]> permutations(int[] arr) {
        List<int[]> results = new ArrayList<int[]>();

        permutations(arr, 0, results);

        return results;
    }

    private static void permutations(int[] arr, int start, List<int[]> results) {
        if (start == arr.length) {
            // Remember we have to clone the array to stop it being
            // modified after we add it
            results.add(arr.clone());
            return;
        }

        for (int i = start; i < arr.length; i++) {
            swapStatic(arr, start, i);
            permutations(arr, start + 1, results);
            swapStatic(arr, start, i);
        }
    }

    private static void swapStatic(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /*
     * Returns the shortest path from a begin word to end word, using a BFS approach
     * 
     * 1. Set up a queue 2. Add our start word to the queue 3. While the queue is
     * not empty... 4. Remove the top of the queue... 5. If it matches our end word,
     * return the distance 6. Otherwise, generate all permutations of this word 7.
     * If a permutation is in the dictionary, add it to the queue with distance+1,
     * remove it from the dictionary
     *
     * So the shortest path will be found.
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<String>();

        for (String word : wordList) {
            wordSet.add(word);
        }

        return ladderLength(beginWord, endWord, wordSet);
    }

    private int ladderLength(String beginWord, String endWord, Set<String> wordSet) {
        LinkedList<WordDistance> queue = new LinkedList<WordDistance>();

        queue.add(new WordDistance(beginWord, 1));

        while (!queue.isEmpty()) {
            WordDistance top = queue.remove();
            String word = top.word;

            if (word.equals(endWord)) {
                return top.distance;
            }

            char[] arr = word.toCharArray();

            for (int i = 0; i < arr.length; i++) {
                for (char c = 'a'; c <= 'z'; c++) {
                    // Swap in the char to the array
                    char temp = arr[i];
                    arr[i] = c;
                    String newWord = new String(arr);

                    if (wordSet.contains(newWord)) {
                        queue.add(new WordDistance(newWord, top.distance + 1));
                        wordSet.remove(newWord);
                    }

                    arr[i] = temp;
                }
            }
        }

        return 0;
    }

    private class WordDistance {
        String word;
        int distance;

        WordDistance(String word, int distance) {
            this.word = word;
            this.distance = distance;
        }
    }

    /*
     * Finds the max sum where elements cannot be adjacent
     * 
     * This is basically a dynamic programming question, but since we only need to
     * look at i-1 and i-2, we can do it using just two variables.
     * 
     * 1. At nums[i], the max f(i) must be nums[i] (something is better than
     * nothing) 2. At nums[i+1], the max f(i) is either nums[i-1], or nums[i] 3.
     * Therefore, at nums[x], the max f(x) is Math.max(f(i-2) + nums[i], f(i-1))
     * 
     * Ex. [2, 7, 9, 3, 1]
     * 
     */
    public static int maxNonAdjacentSum(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int prevMax = 0;
        int currMax = 0;

        for (int i = 0; i < nums.length; i++) {
            int temp = currMax;
            currMax = Math.max(prevMax + nums[i], currMax);
            prevMax = temp;
        }

        return currMax;
    }

    /*
     * Traverse a tree, returning the list of node values at each level. We could
     * also use a recursive approach, where we create a list for each level, and
     * then index into that list depending on the level (height) we are at.
     * 
     * This is basically like a breadth-first search.
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> results = new ArrayList<List<Integer>>();

        if (root == null) {
            return results;
        }

        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.add(root);

        while (!queue.isEmpty()) {
            List<Integer> result = new ArrayList<Integer>();

            // Have to save this since queue will be modified
            // in the loop
            int nodesAtThisLevel = queue.size();

            for (int i = 0; i < nodesAtThisLevel; i++) {
                TreeNode node = queue.poll();

                if (node.left != null) {
                    queue.add(node.left);
                }

                if (node.right != null) {
                    queue.add(node.right);
                }

                result.add(node.val);
            }

            results.add(result);
        }

        return results;
    }

    /*
     * Returns the common ancestor for 2 binary tree nodes
     * 
     * If the current subtree contains one of the nodes in the left and one of the
     * nodes in the right, then the answer is this subtree. Otherwise the answer
     * must be node that is not null.
     * 
     * (We will return from the first node we find, so that node must contain the
     * other node underneath it, if the other node was not found in the other
     * subtree)
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return root;
        }
    }

    /*
     * DFS a matrix for a string
     */
    public boolean exists(char[][] board, String word) {
        if (word == null || word.length() == 0) {
            return false;
        }

        boolean[][] visited = new boolean[board.length][board[0].length];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == word.charAt(0) && search(board, row, col, visited, word, 0)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean search(char[][] board, int row, int col, boolean[][] visited, String word, int index) {
        if (index == word.length()) {
            return true;
        }

        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || visited[row][col]
                || board[row][col] != word.charAt(index)) {
            return false;
        }

        visited[row][col] = true;

        if (search(board, row - 1, col, visited, word, index + 1)
                || search(board, row, col + 1, visited, word, index + 1)
                || search(board, row, col - 1, visited, word, index + 1)
                || search(board, row + 1, col, visited, word, index + 1)) {
            return true;
        }

        visited[row][col] = false;

        return false;
    }

    /*
     * Rotates a 2D matrix
     * 
     * There are a few ways to do this. Rotating the corners is probably the most
     * efficient, but this might be the easiest way to keep in your head.
     * 
     * Transpose > Flip horizontally
     * 
     */
    public void rotate(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return;
        }

        /*
         * First, transpose: 1 2 3 4 5 6 7 8 9
         * 
         * -->
         * 
         * 1 4 7 2 5 8 3 6 9
         */
        for (int row = 0; row < matrix.length; row++) {
            for (int col = row; col < matrix[0].length; col++) {
                int temp = matrix[row][col];
                matrix[row][col] = matrix[col][row];
                matrix[col][row] = temp;
            }
        }

        /*
         * Next, flip horizontally:
         * 
         * 1 4 7 2 5 8 3 6 9
         * 
         * -->
         * 
         * 7 4 1 8 5 2 9 6 3
         */
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length / 2; col++) {
                int temp = matrix[row][col];
                matrix[row][col] = matrix[row][matrix.length - 1 - col];
                matrix[row][matrix.length - 1 - col] = temp;
            }
        }
    }

    /*
     * Binary tree inorder traversal (iterative)
     */
    public List<Integer> inorderTraversal(TreeNode root) {

        List<Integer> results = new ArrayList<Integer>();
        Stack<TreeNode> stack = new Stack<TreeNode>();

        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {

            // Go down to the left of the current tree
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            results.add(current.val);

            // Now check the right subtree
            current = current.right;
        }

        return results;
    }

    /*
     * Returns the max product from a subarray
     * 
     * We store the maximum and minimum value so far, which can be defined by either
     * the previous max/min * this number, or this number itself. (Similar to
     * Kadane's algorithm)
     * 
     * If a number is less than 0, we swap the maximum and minimum. Why? Well, think
     * about it: multiplying the larger number by a negative will give you a smaller
     * number.
     * 
     * E.g., if you had 2 and 4: 2 * -1 = -2 4 * -1 = -4
     * 
     * So then 2 becomes your max so far.
     * 
     */
    public int maxProduct(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        int max = nums[0];
        int maxSoFar = nums[0];
        int minSoFar = nums[0];

        for (int i = 1; i < nums.length; i++) {

            if (nums[i] < 0) {
                int temp = maxSoFar;
                maxSoFar = minSoFar;
                minSoFar = temp;
            }

            // Max/min product for the current number is either the current number itself
            // or the max/min by the previous number times the current one
            maxSoFar = Math.max(nums[i], maxSoFar * nums[i]);
            minSoFar = Math.min(nums[i], minSoFar * nums[i]);

            max = Math.max(max, maxSoFar);
        }

        return max;
    }

    /*
     * Search a matrix for words
     */
    public List<String> findWords(char[][] board, String[] words) {

        Set<String> results = new HashSet<String>();

        TrieNode root = buildTrie(words);

        boolean[][] visited = new boolean[board.length][board[0].length];

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                dfs(board, row, col, root, visited, results);
            }
        }

        return new ArrayList<String>(results);
    }

    private void dfs(char[][] board, int row, int col, TrieNode node, boolean[][] visited, Set<String> results) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || visited[row][col]) {
            return;
        }

        char c = board[row][col];
        int childIndex = c - 'a';

        // Check to see if the trie contains a node for this char
        if (node.children[childIndex] == null) {
            return;
        }

        TrieNode next = node.children[childIndex];

        if (next.word != null) {
            results.add(next.word);
        }

        visited[row][col] = true;

        dfs(board, row - 1, col, next, visited, results);
        dfs(board, row, col + 1, next, visited, results);
        dfs(board, row + 1, col, next, visited, results);
        dfs(board, row, col - 1, next, visited, results);

        visited[row][col] = false;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;

            for (char c : word.toCharArray()) {

                int index = c - 'a';

                if (node.children[index] == null) {
                    node.children[index] = new TrieNode();
                }

                node = node.children[index];
            }

            node.word = word;
        }

        return root;
    }

    private class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }
    
    /*
     * Returns an array where each element counts the number
     * of smaller elements to the right.
     * 
     * The idea is that we do a merge sort, but we keep track of the indices
     * and order those instead. When we move something from right to left,
     * we increment rightCount -- something is smaller to the right.
     * Then when moving in the left array parts to sorted order,
     * we increment count by this right count.
     * 
     * Example:
     * 5 2 6 1 > 
     * >> Sort 5-2
     * >> Move 2 into sorted position (right count + 1)
     * >> Move 5 into sorted position (increment count by right count (1))
     * >> -- there was 1 element greater than 5 in right half
     * 
     * >> Sort 6-1
     * >> Move 1 into sorted position (right count + 1)
     * >> Move 6 into sorted position (increment count by right count (1))
     * 
     * Now we have:
     * 
     * 2 5 6 1
     * 1 0 1 0 (counts)
     * 
     * >> Sort 2 5 1 6
     * >> Move 1 into sorted position (right count + 1)
     * >> Move 2 into sorted position (increment count by right count (1)), count = 2 0 1 0
     * >> Move 5 into sorted position (increment count by right count (1)), count = 2 1 1 0
     * >> Move 6 into sorted position (right count + 1)
     * 
     * etc.
     *
     */
    public List<Integer> countSmaller(int [] nums) {
        List<Integer> results = new ArrayList<Integer>();
        
        int [] counts = new int[nums.length];
        int [] indices = new int[nums.length];
        
        // Initialise indices
        for(int i = 0; i < nums.length; i++) {
            indices[i] = i;
        }
        
        mergeSort(nums, indices, counts, 0, nums.length-1);
        
        for(int i = 0; i < counts.length; i++) {
            results.add(counts[i]);
        }
        
        return results;
    }
    
    private void mergeSort(int [] nums, int [] indices, int [] counts, int left, int right) {
        if(right <= left) {
            return;
        }
        
        int mid = left + (right-left)/2;
        
        mergeSort(nums, indices, counts, left, mid);
        mergeSort(nums, indices, counts, mid+1, right);
        
        merge(nums, indices, counts, left, right);
    }
    
    private void merge(int [] nums, int [] indices, int [] counts, int left, int right) {
        int mid = left + (right-left)/2;
        
        int leftIndex = left;
        int rightIndex = mid+1;
        
        // This keeps track of the number of elements that will be moved to the
        // left (i.e., elements in the sorted right part that are smaller than
        // the current left part element.)
        int rightCount = 0;
        
        int [] sortedIndices = new int [right-left+1];
        int sortedIndex = 0;
        
        while(leftIndex <= mid && rightIndex <= right) {
            if(nums[indices[rightIndex]] < nums[indices[leftIndex]]) {
                sortedIndices[sortedIndex] = indices[rightIndex];
                rightCount++;
                
                // Increment array indices
                rightIndex++;
                sortedIndex++;
            } else {
                sortedIndices[sortedIndex] = indices[leftIndex];
                
                // The number of elements that were smaller than this index
                counts[indices[leftIndex]] += rightCount;
                
                // Increment array indices
                leftIndex++;
                sortedIndex++;
            }
        }
        
        // Take elements from the untraversed array
        while(leftIndex <= mid) {
            sortedIndices[sortedIndex] = indices[leftIndex];
            counts[indices[leftIndex]] += rightCount;
            
            // Increment array indices
            leftIndex++;
            sortedIndex++;            
        }
        
        while(rightIndex <= right) {
            sortedIndices[sortedIndex] = indices[rightIndex];
            
            // Increment array indices
            rightIndex++;
            sortedIndex++;
        }
        
        // Copy the sorted indices back into the main index array
        // (we could optimise this)
        for(int i = left; i <= right; i++) {
            indices[i] = sortedIndices[i-left];
        }
    }
    
    /*
     * This is essentially an example of the
     * 'Dutch flag' problem -- a 3-way quicksort
     *  partition
     *  
     *  1. Set up, left, right and array pointers
     *  2. If the pointer is < partition,
     *      - Swap it with left
     *      - Increment left and pointer
     *  3. If the pointer is > partition,
     *      - Swap it with right
     *      - Decrement right
     *  4. If the pointer == partition,
     *      - Increment pointer
     */
    public void sortColors(int [] nums) {
        
        int left = 0;
        int right = nums.length-1;
        int pointer = 0;
        
        while(pointer <= right) {
            if(nums[pointer] == 0) {
                // Swap left
                int temp = nums[pointer];
                nums[pointer] = nums[left];
                nums[left] = temp;   // 0
                
                left++;
                pointer++;
            } else if (nums[pointer] == 2) {
                // Swap right
                int temp = nums[pointer];
                nums[pointer] = nums[right];
                nums[right] = temp; // 2
                
                // Note we don't increment pointer --
                // it could still be < partition
                right--;
            } else if(nums[pointer] == 1) {
                // Continue
                pointer++;
            }
        }
    }
    
    /*
     * Returns true if the last element in the array
     * can be reached by jumping from 1-n steps denoted
     * by the value in the array element.
     * 
     * Performance: O(n)
     * Space: O(1)
     * 
     * There are also recursive (bottom-up), memoization,
     * and dynamic programming solutions.
     * 
     * Thinking about this solution, we're essentially saying,
     * can we reach the last element from this element? If so, then
     * set this element to be the element that we need to reach next.
     * 
     */
    public boolean canJump(int[] nums) {
        // The last element we can reach
        int lastElement = nums.length-1;
        
        for(int i = nums.length-2; i >= 0; i--) {
            // If we can reach the last element from this position,
            // set this to be the last element
            if(i + nums[i] >= lastElement) {
                lastElement = i;
            }
        }
        
        return lastElement == 0;
    }
    
    /*
     * Returns the missing number in linear time
     * using constant space.
     * 
     * We could also do this using XORs.
     */
    public int missingNumber(int[] nums) {

        int expected = nums.length * (nums.length+1) / 2;
        int actual = 0;
        
        for(int i = 0; i < nums.length; i++) {
            actual += nums[i];
        }
        
        return expected-actual;
    }
    
    /*
     * Creates the largest possible number from
     * an array of numbers.
     * 
     * To find out which number is larger, we implement a custom comparator.
     * By sticking the numbers together both ways, and then checking which is larger.
     * 
     * E.g., if you had 34 (a) and 3 (b):
     * 343 (a + b)
     * 334 (b + a)
     * 
     * Then 334.compareTo(343) would return -1, since 334 is smaller,
     * so 34 (first argument) would come first in a sort.
     * 
     */
    public String largestNumber(int[] nums) {
    
        String [] numStrs = new String[nums.length];
        
        for(int i = 0; i < nums.length; i++) {
            numStrs[i] = nums[i]+"";
        }
        
        Arrays.sort(numStrs, new largerComparator());
        
        if(numStrs[0].equals("0")) {
            return "0";
        }
        
        StringBuffer sb = new StringBuffer();
        
        for(String s : numStrs) {
            sb.append(s);
        }
        
        return sb.toString();
    }
    
    private class largerComparator implements Comparator<String> {

        @Override
        public int compare(String arg0, String arg1) {
            
           String order1 = arg0 + arg1;
           String order2 = arg1 + arg0;
           
           return order2.compareTo(order1);
        }
    }
    
    /*
     * Essentially a BFS topological sort
     * 
     * Dependencies are given in the form
     * [1, 0], where 0 is a prerequisite of 1.
     * 
     * This isn't particularly efficient. To make it better
     * we could hold a set of element that haven't been processed yet
     * and only process them instead of checking every prerequisite
     * every time.
     * 
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
    	if(numCourses == 0) {
    		return new int[0];
    	}
    	
    	int [] inDegree = new int[numCourses];
    	int [] order = new int[numCourses];
    	int index = 0;
    	
    	for(int i = 0; i < prerequisites.length; i++) {
    		// For each prerequisite, count the number
    		// of courses you need to complete before
    		// taking this course (the in-degree)
    		inDegree[prerequisites[i][0]]++;
    	}
    	
    	Queue<Integer> queue = new LinkedList<Integer>();
    	
    	// Add the courses with no prerequisites to the queue
    	for(int i = 0; i < numCourses; i++) {
    		if(inDegree[i] == 0) {
    			// Add this to the order
    			order[index] = i;
    			index++;
    			
    			// Then add it to the queue
    			queue.add(i);
    		}
    	}
    	
    	while(!queue.isEmpty()) {
    		int prerequisite = queue.poll();
    		
    		for(int i = 0; i < prerequisites.length; i++) {
    			
    			// Now decrement the in-degree for all the
    			// courses for which this is a prerequisite
    			if(prerequisites[i][1] == prerequisite) {
    				inDegree[prerequisites[i][0]]--;
    				
    				if(inDegree[prerequisites[i][0]] == 0) {
    					order[index] = prerequisites[i][0];
    					index++;
    					queue.add(prerequisites[i][0]);
    				}
    			}
    		}
    	}
    	
    	if(index == numCourses) {
    		return order;
    	} else {
    		return new int[0];
    	}
    }
    
    /*
     * Returns the inorder successor of a BST
     * 
     *     2
     *    /  \
     *   1    3 
     *  
     * Searching for 1 will return 2, etc.
     *  
     * Time: O(n) if the tree is not balanced,
     * but O(h) if the tree is balanced, since it basically
     * works like a binary search and cuts the tree in half
     * each time.
     *    
     */  
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
    	TreeNode successor = null;
    	
    	TreeNode node = root;
    	
    	while(node != null) {
    		if(p.val < node.val) {
    			successor = node;
    			node = node.left;
    		} else {
    			// In an in-order traversal, the successor will be
    			// when we move back up to the right. So we only
    			// update the successor when we move to the left.
    			node = node.right;
    		}
    	}
    	
    	return successor;
    }
    
    /*
     * Returns the inorder predecessor of a BST
     * 
     *     2
     *    /  \
     *   1    3 
     *  
     * Searching for 2 will return 1, etc.
     *  
     * Time: O(n) if the tree is not balanced,
     * but O(h) if the tree is balanced, since it basically
     * works like a binary search and cuts the tree in half
     * each time.
     *    
     */  
    public TreeNode inorderPredecessor(TreeNode root, TreeNode p) {
    	TreeNode predecessor = null;
    	TreeNode node = root;
    	
    	while(node != null) {
    		if(p.val <= node.val) {
    			node = node.left;
    		} else {
    			predecessor = node;
    			node = node.right;
    		}
    	}
    	
    	return predecessor;
    }
    
    /*
     * Finds the kth smallest number in an array
     * sorted horizontally and vertically.
     * 
     * Think of it like this: we can count the number
     * of numbers less than k, then we can find k.
     * We can find this number using a binary search.
     * 
     * 1. Take a mid point.
     * 2. Start in the top left.
     * 3. Move left until the number is <= the mid point
     * 4. Count the number of this column + 1 (numbers less than k)
     * 5. Move to the next row and repeat
     * 6. If the count is less than k, move mid up (we need more numbers -- numbers are in a lower range)
     * 7. Otherwise move high down (we need fewer numbers, numbers lie in a higher range)
     * 
     * Think about the number in a range:
     * 1, 5, 9, 10, 11, 12, 13, 13, 15
     * 
     * We start of with 8 (1-16 midpoint)
     * 2 numbers are <= 8, so move low up
     * 9-16, mid is 12, 6 are <= 8, so move low up
     * 13-16, mid is 14, 8 are <= 14, so move low down
     * 13-14, mid is 13, 8 are <= 13, so move low up
     * low = high, so break, and we have our answer
     * 
     */
    public int kthSmallest(int[][] matrix, int k) {
    	int low = matrix[0][0];
    	int high = matrix[matrix.length-1][matrix[0].length-1]+1;
    	
    	while(low < high) {
    		int mid = low + (high-low)/2;
    		
    		int count = 0;
    		int col = matrix[0].length-1;
    		
    		for(int row = 0; row < matrix.length; row++) {
    			while(col >= 0 && matrix[row][col] > mid) {
    				col--;
    			}
    			
    			count += col+1;
    		}

    		if(count < k) {
    			low = mid + 1;
    		} else {
    			high = mid;
    		}
    	}
    	
    	return low;
    }
    
    /*
     * Find and return 4 elements that sum to the target value s,
     * or return an empty array if no target exists.
     * 
     * Similar to 3sum.
     * 1. Sort the array.
     * 2. Take two values at the start
     * 3. Work out the target we need by subtracting the two values from 2)
     * 4. Set up low and pointers, and move them to try to find our target
     * 
     * Time: O(n^3)
     */
    public int [] findArrayQuadruplet(int [] nums, int s) {
    	if(nums.length < 4) {
    		return new int[0];
    	}
    	
    	Arrays.sort(nums);
    	
    	for(int i = 0; i < nums.length-3; i++) {
    		for(int j = i+1; j < nums.length-2; j++) {
    			
    			int target = s-(nums[i] + nums[j]);
    			int low = j+1;
    			int high = nums.length-1;
    			
    			while(low < high) {
    				
    				if(nums[low] + nums[high] < target) {
    					low++;
    				} else if (nums[low] + nums[high] > target) {
    					high--;
    				} else {
    					return new int[] {nums[i], nums[j], nums[low], nums[high]};
    				}
    				
    			}
    		}
    	}
    	
    	return new int[0];	
    }
    
    /*
     * Returns the number of possible unlock patterns
     * on a typical handset
     * 
     * m = min pattern length
     * n = max pattern length
     * 
     * 1 2 3
     * 4 5 6
     * 7 8 9
     * 
     * Time: O(n!) where n is the max pattern length
     * Space: O(n) where n is the max pattern length (recursive stack depth)
     */
    public int getNumPatterns(int m, int n) {
    	boolean [] visited = new boolean[10];
    	int [][] requiredToJump = new int[10][10];

    	// horizontal jumps
    	requiredToJump[1][3] = requiredToJump[3][1] = 2;
    	requiredToJump[4][6] = requiredToJump[6][4] = 5;
    	requiredToJump[7][9] = requiredToJump[9][7] = 8;

    	// vertical jumps
    	requiredToJump[1][7] = requiredToJump[7][1] = 4;
    	requiredToJump[2][8] = requiredToJump[8][2] = 5;
    	requiredToJump[3][9] = requiredToJump[9][3] = 6;

    	// diagonal jumps
    	requiredToJump[1][9] = requiredToJump[9][1] = 5;
    	requiredToJump[7][3] = requiredToJump[3][7] = 5;

    	int count = 0;
    	
    	// Count from corners
    	// *4 = 1 3 7 9 are symmetrical
    	count += jumpDfs(1, 1, 0, m, n, requiredToJump, visited) * 4;
    	
    	// Count from middle elements
    	// *4 = 2 4 6 8 are symmetrical
    	count += jumpDfs(2, 1, 0, m, n, requiredToJump, visited) * 4;
    	
    	// Count from centre
    	count += jumpDfs(5, 1, 0, m, n, requiredToJump, visited);
    	
    	return count;
    }
    
    // 1. If the current pattern >= min, exceed count
    // 2. Increment length, and check if we go over max (if we do return)
    // 		(this is a kind of lookahead -- we're getting the length of the next pattern)
    // 3. Mark the node as visited
    // 4. Try to jump to every other node
    // 5. Unvisit this node
    // In terms of count, it gets stored at every call node -- so every call from 2 will have count 2, coming from 1, etc.
    private int jumpDfs(int currentNum, int length, int count, int min, int max, int[][] requiredToJump, boolean[] visited) {
    	if(length >= min) {
    		count++;
    	}
    	
    	length++;
    	
    	if(length > max) {
    		return count;
    	}
    	
    	visited[currentNum] = true;
    	
    	// Try visiting all the other keys
    	for(int nextNum = 1; nextNum <= 9; nextNum++) {
    		int jump = requiredToJump[currentNum][nextNum];
    		
    		if(!visited[nextNum] && (jump == 0 || visited[jump])) {
    			count = jumpDfs(nextNum, length, count, min, max, requiredToJump, visited);
    		}
    	}
    	
    	// backtrack
    	visited[currentNum] = false;
    
    	return count;
    }
    
    /*
     * The general idea is to move all variables to one side and all numbers to the other.
     * E.g.,
     * "x+5-3+x=6+x-2"
     * > x + x - x = 6-2-5+3
     * > x = 6-2-2 (addition first)
     * > x = 2
     * 
     * If both sides turn to 0, there are infinite solutions.
     * x+1=x+1
     * x-x=1-1
     * 0=0
     * 
     * If there is no solution, the x side turns to 0, but the rhs has a number
     * x=x+2
     * x-x=2
     * 0=2
     * 
     * 1. Split the equation into LHS and RHS
     * 2. Read the parts of each side into a list
     * 3. 
     * 
     */
    public String solveEquation(String equation) {
        String[] leftAndRightParts = equation.split("=");
        
        List<String> leftParts = getParts(leftAndRightParts[0]);
        List<String> rightParts = getParts(leftAndRightParts[1]);
        
        int lhs = 0;
        int rhs = 0;
        
        // If we have an x, we increment how many we have on the left
        // If we have a number, we add the negative of it to the right
        for(String part : leftParts) {
        	if(part.contains("x")) {
        		lhs += Integer.parseInt(getCoefficient(part));
        	} else {
        		rhs -= Integer.parseInt(part);
        	}
        }
        
        // Now we do the inverse with the RHS
        for(String part : rightParts) {
        	if(part.contains("x")) {
        		lhs -= Integer.parseInt(getCoefficient(part));
        	} else {
        		rhs += Integer.parseInt(part);
        	}
        }
        
        if(lhs == 0 && rhs == 0) {
        	return "Infinite solutions";
        } else if (lhs == 0 && rhs != 0) {
        	return "No solution";
        } else {
        	return "x=" + rhs/lhs;
        }
    }
    
    private String getCoefficient(String part) {
    	
    	// E.g. 6x = 2-2 = 0 (first numerical index)
    	// -6x = 3-2 = 1 (first numerical index)
    	int coefficientIndex = part.length()-2;
    	
    	// If there is a coefficient, remove x
    	if(part.length() > 1 && part.charAt(coefficientIndex) >= '0' && part.charAt(coefficientIndex) <= '9') {
    		return part.replace("x", "");
    	}
    	
    	// Otherwise just replace with 1 (we need to replace so we can keep -)
    	return part.replace("x", "1");
    }
    
    private List<String> getParts(String equationSide){
    	List<String> parts = new ArrayList<>();
    
    	String part = "";
    	
    	for(int i = 0; i < equationSide.length(); i++) {
    		// When we hit a side, add the part we parsed so far and add the sign
    		if(equationSide.charAt(i) == '+' || equationSide.charAt(i) == '-') {
    			if(part.length() > 0) {
    				parts.add(part);
    			}
    			
    			part = "" + equationSide.charAt(i);
    		} else {
    			part += equationSide.charAt(i);
    		}
    	}
    	
    	parts.add(part);
    	
    	return parts;
    }
    
    /*
     * Finds the longest path in a directed, weighted, acyclic graph
     * (I.e., like finding the critical path)
     * 
     * 1. First, get a topological sorting
     * 2. Next, do a kind of inverse Dijkstra:
     * 	2.1 Set distances to all vertices to -inf, set source to 0
     * 	2.2 Then go through our topostack
     * 	2.3 For each neighbouring vertex, if the distance to our current vertex
     * 		+ distance to neighbour > distance to neighbour, then update the neighbour's
     * 		distance.
     * 3. Finally just return the vertex with the maximum distance
     * 
     * Time: O(V+E)
     * Space: O(V)
     * 
     * Sample graph:
     *     5
     *   0 ---> 1
     * 4 |    |1
     *   |    |
     *   2 ---> 4
     *      10
     * 
     * Longest path = 0 > 2 > 4 (4+10 = 14)
     * 
     */
    public double getLongestPathInWeightedDAG(EdgeWeightedDirectedGraph graph) {
    	
    	boolean[] visited = new boolean[graph.numOfVertices()];
    	Stack<Integer> stack = new Stack<Integer>();
    	
    	for(int vertex = 0; vertex < graph.numOfVertices(); vertex++) {
    		if(!visited[vertex]) {
    	    	topologicalSort(graph, vertex, visited, stack);    			
    		}
    	}
    	
    	// Initialize distance to all vertices as -inf
    	double[] distances = new double[graph.numOfVertices()];
    	
    	for(int i = 0; i < distances.length; i++) {
    		distances[i] = Integer.MIN_VALUE;
    	}
    	
    	// Initialize distance to source as 0
    	distances[0] = 0;
    	
    	double maxDistance = Double.MIN_VALUE;
    	
    	// Similar to Dijkstra: check all the neighbours for each vertex
    	// If their distance is < the distance to this vertex + weight
    	// set their distance to be this distance
    	while(!stack.isEmpty()) {
    		int vertex = stack.pop();
    		
    		if(distances[vertex] != Integer.MIN_VALUE) {
    			for(DirectedEdge neighbour : graph.adjacentVertices(vertex)) {
    				if(distances[neighbour.to()] < distances[neighbour.from()] + neighbour.weight()) {
    					distances[neighbour.to()] = distances[neighbour.from()] + neighbour.weight();
    					
    					maxDistance = Math.max(maxDistance, distances[neighbour.to()]);
    				}
    			}
    		}
    	}
    	
    	return maxDistance;
    }
    
    private void topologicalSort(EdgeWeightedDirectedGraph graph, int vertex, boolean[] visited, Stack<Integer> stack) {
    	visited[vertex] = true;
    	
    	for(DirectedEdge neighbour : graph.adjacentVertices(vertex)) {
    		if(!visited[neighbour.to()]) {
    			topologicalSort(graph, neighbour.to(), visited, stack);
    		}
    	}
    	
    	stack.push(vertex);
    }
    
    /*
     * Returns the longest path in a directed, unweighted, acyclic graph
     * Do a DFS from each vertex.
     * (We could optimize this by checking the max distance already during the DFS)
     * 
     *	1. Set up a distances array
     *	2. DFS from each vertex if we haven't met it already
     *		(if we did meet it already -- it must have a longer distance)
     *	3. Once we get to a terminal vertex, set the distance to max of the distance using
     *		this path or previous distance
     * 
     * Sample graph:
     * 0 -> 1
     * |
     * 2 -> 3 -> 4
     * ^
     * |
     * 6 <- 5
     * 
     * (then longest path is 5, 6, 2, 3, 4)
     * 
     */
    
    public int getLongestPathInUnweightedDAG(DirectedGraph graph) {
    		
    	int [] distances = new int[graph.numOfVertices()];
    	
    	for(int vertex = 0; vertex < graph.numOfVertices(); vertex++) {
    		if(distances[vertex] == 0) {
        		dfs(graph, vertex, 1, distances);    			
    		}
    	}
    	
    	int max = 0;
    	
    	for(int i = 0; i < distances.length; i++) {
    		max = Math.max(max, distances[i]);
    	}
    	
    	return max;
    }
    
    private void dfs(DirectedGraph graph, int vertex, int distance, int [] distances) {
    	
    	for(int neighbour : graph.adjacentVertices(vertex)) {
    		dfs(graph, neighbour, distance+1, distances);
    	}
    	
    	distances[vertex] = Math.max(distances[vertex], distance);
    }
}