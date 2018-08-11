package com.pekoto.test.challenges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.pekoto.challenges.LeetCode;
import com.pekoto.challenges.TreeCodec;
import com.pekoto.challenges.TreeNode;

/**
 *  Unit tests for the LeetCode class 
 */
public class LeetCodeTests {
    
    @Test
    public void testLengthOfLongestSubstring() {
        String s = "abcbadcab";
        
        assertEquals(4, LeetCode.lengthOfLongestSubstring(s));
    }
    
    @Test
    public void testMedianSortedArrays() {
        int [] arr1 = {1, 2};
        int [] arr2 = {3, 4};
        
        assertEquals(2.5, LeetCode.findMedianSortedArrays(arr1, arr2), 0.1);
    }
    
    @Test
    public void testMySqrt() {
        assertEquals(2, LeetCode.mySqrt(8));
        assertEquals(3, LeetCode.mySqrt(9));
    }
    
    @Test
    public void testIsMatch() {
        assertTrue(LeetCode.isMatch("aaa", "a*"));
    }
    
    @Test
    public void testMerge() {
        
        List<Interval> intervals = new ArrayList<Interval>();
        
        intervals.add(new Interval(1, 4));
        intervals.add(new Interval(0, 2));
        intervals.add(new Interval(3, 5));
        
        List<Interval> merged = LeetCode.merge(intervals);
        
        assertTrue(true);
    }
    
    @Test
    public void testFastPow() {
        assertEquals(16.0, LeetCode.myPow(2.0, 4), 0.1);
        assertEquals(0.25, LeetCode.myPow(2.0, -2), 0.1);
    }
    
    @Test
    public void testRemoveDuplicateLetters() {
        assertEquals("bac", LeetCode.removeDuplicateLetters("bbacac"));
    }
    
    public void testLongestConsecutive() {
    	assertEquals(4, LeetCode.longestConsecutive(new int[] {100, 2, 200, 4, 1, 3}));
    }
    
    @Test
    public void testNumSquares() {
    	assertEquals(3, LeetCode.numSquares(12));
    	assertEquals(2, LeetCode.numSquares(13));
    }
    
    @Test
    public void testCalculator() {
        assertEquals(23, LeetCode.calculator("(1+(4+5+2)-3)+(6+8)"));
    }
    
    @Test
    public void testTreeCode() {
        
        TreeNode root = new TreeNode(1);
        TreeNode two = new TreeNode(2);
        TreeNode three = new TreeNode(3);
        TreeNode four = new TreeNode(4);
        TreeNode five = new TreeNode(5);
        
        three.setLeftChild(four);
        three.setRightChild(five);
        root.setLeftChild(two);
        root.setRightChild(three);
        
        TreeCodec treeCodec = new TreeCodec();
        String serializedTree = treeCodec.serialize(root);
        TreeNode deserialized = treeCodec.deserialize(serializedTree);
        
        assertEquals(1, deserialized.val);
        assertEquals(2, deserialized.left.val);
        assertEquals(3, deserialized.right.val);
        assertEquals(4, deserialized.right.left.val);
        assertEquals(5, deserialized.right.right.val);
    }
}
