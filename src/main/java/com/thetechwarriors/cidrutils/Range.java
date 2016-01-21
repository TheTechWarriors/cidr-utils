/*
 * Copyright 2016 Tech Warriors, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thetechwarriors.cidrutils;

import org.apache.commons.lang3.StringUtils;

public class Range {

	private int[] octets = new int[4];
	private int maskSize;

	public Range(String ipAddress, int maskSize) {
		String[] split = StringUtils.split(ipAddress, '.');
		for (int i = 0; i < 4; i++) {
			this.octets[i] = Integer.parseInt(split[i]);
		}
		this.maskSize = maskSize;
	}

	public long getMaxIpAddresses() {
		return (long) Math.pow(2, 32-maskSize);
	}

	private String getFirstIPAddressForComparison() {
		return String.format("%03d.%03d.%03d.%03d", octets[0], octets[1], octets[2], octets[3]);
	}

	public String getFirstIPAddress() {
		long ipAsLong = ipToLong();
		return longToIp(ipAsLong);
	}

	public String getLastIPAddress() {
		long ipAsLong = ipToLong() + getMaxIpAddresses() - 1;
		return longToIp(ipAsLong);
	}

	public Range getNext() {
		return getNext(1);
	}
	
	/**
	 * Gets the next range with this mask
	 * 
	 * @param idx 0 based index. 0 means this range, 1 means the immediate next
	 *        range, 2 means next range after next, 3 means next range after
	 *        next after next ... and so on
	 * @return the next range
	 */
	public Range getNext(int idx) {
		long ipAsLong = ipToLong() + (idx * getMaxIpAddresses());
		return new Range(longToIp(ipAsLong), maskSize);
	}

	/**
	 * Finds next available sub-range with the new-mask after the given range
	 */
	public Range getNextAvailableSubRange(Range after, int newMask) {
		Range toCompare = getSubRange(newMask);
		
		try {
			String afterNext = after.getNext().getFirstIPAddressForComparison();
			while (toCompare.getFirstIPAddressForComparison().compareTo(afterNext) < 0) {
				toCompare = toCompare.getNext();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return toCompare;
	}
	
	public Range getPrevious() {
		long ipAsLong = ipToLong() - getMaxIpAddresses();
		return new Range(longToIp(ipAsLong), maskSize);
	}

	public Range getSubRange(int newMaskSize) {
		return new Range(getFirstIPAddress(), newMaskSize);
	}
	
	private String longToIp(long ip) {
		StringBuilder result = new StringBuilder(15);
		for (int i = 0; i < 4; i++) {
			result.insert(0, Long.toString(ip & 0xff));
			if (i < 3) {
				result.insert(0, '.');
			}
			ip = ip >> 8;
		}
		return result.toString();
	}
	
	private long ipToLong() {
		long result = 0;
		for (int i = 3; i >= 0; i--) {
			result |= octets[3 - i] << (i * 8);
		}
		return result;
	  }	
	
	public String toString() {
		return String.format("%d.%d.%d.%d/%d", octets[0], octets[1], octets[2], octets[3], maskSize);
	}
}
