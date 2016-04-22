package com.hh.common.utils;

import java.util.ArrayList;
import java.util.List;

public class BitUtils {
	public interface BitFlag {
		/**
		 * 获取bit位序号
		 * 
		 * @return
		 */
		public int getBitIndex();
	}

	public static boolean isBitIndexSetToOne(BitFlag bitFlag, int value) {
		return 0 != (value & (1 << bitFlag.getBitIndex()));
	}
	
	public static boolean isBitIndexSetToOne(BitFlag bitFlag, long value) {
		return 0 != (value & (1 << bitFlag.getBitIndex()));
	}

	public static int setBitIndexToOne(BitFlag bitFlag, int value) {
		return (1 << bitFlag.getBitIndex()) | value;
	}
	
	public static long setBitIndexToOne(BitFlag bitFlag, long value) {
		return (1 << bitFlag.getBitIndex()) | value;
	}

	public static int setBitIndexToZero(BitFlag bitFlag, int value) {
		return (1 << bitFlag.getBitIndex()) ^ value;
	}
	
	public static long setBitIndexToZero(BitFlag bitFlag, long value) {
		return (1 << bitFlag.getBitIndex()) ^ value;
	}
	
	/**
	 * 
	 * @param bitIndex 从0开始
	 *      eg：isBitIndexSetToOne(0,1) == true
	 * @param value
	 * @return
	 */
	public static boolean isBitIndexSetToOne(int bitIndex, int value) {
		return 0 != (value & (1 << bitIndex));
	}
	
	public static boolean isBitIndexSetToOne(int bitIndex, long value) {
		return 0 != (value & (1 << bitIndex));
	}
	
	/**
	 * 
	 * @param bitIndex 从0开始
	 * 		setBitIndexToOne(0,0) == 1
	 * @param value
	 * @return
	 */
	public static int setBitIndexToOne(int bitIndex, int value) {
		return (1 << bitIndex) | value;
	}
	
	public static long setBitIndexToOne(int bitIndex, long value) {
		return (1 << bitIndex) | value;
	}
	
	/**
	 * 
	 * @param bitIndex 从0开始
	 * @param value
	 * @return
	 */
	public static int setBitIndexToZero(int bitIndex, int value) {
		return (1 << bitIndex) ^ value;
	}
	
	public static long setBitIndexToZero(int bitIndex, long value) {
		return (1 << bitIndex) ^ value;
	}

	/**
	 * 
	 * @param value
	 * @return eg:一个被置顶、被锁帖、被加精华的投票表示为 0;1;2;3 splitIntBit(15) = [0,1,2,3]
	 */
	public static List<Integer> splitIntBit(int value) {
		List<Integer> ints = new ArrayList<Integer>();

		for (int i = 0; value > 0; ++i) {
			if ((value & 1) == 1) {
				ints.add(i);
			}
			value >>= 1;
		}
		return ints;
	}
	
	public static List<Integer> splitIntBit(long value) {
		List<Integer> ints = new ArrayList<Integer>();

		for (int i = 0; value > 0; ++i) {
			if ((value & 1) == 1) {
				ints.add(i);
			}
			value >>= 1;
		}
		return ints;
	}
	


}
