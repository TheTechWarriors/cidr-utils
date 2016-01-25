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

public class Subnet {

	private int[] octets = new int[4];
	private int maskSize;

	public Subnet(String ipAddress, int maskSize) {
		String[] split = StringUtils.split(ipAddress, '.');
		for (int i = 0; i < 4; i++) {
			this.octets[i] = Integer.parseInt(split[i]);
		}
		this.maskSize = maskSize;
	}

	/**
	 * Gets the maximum number of IP address in the current subnet. For example,
	 * if current subnet is 192.168.1.0/24, then the max is 256.
	 */
	public long getMaxIpAddresses() {
		return (long) Math.pow(2, 32-maskSize);
	}

	private String getFirstIPAddressForComparison() {
		return String.format("%03d.%03d.%03d.%03d", octets[0], octets[1], octets[2], octets[3]);
	}

	/**
	 * Gets the first IP address in the current subnet. For example, if current
	 * subnet is 192.168.1.0/24, then the first IP address would be 192.168.1.0
	 */
	public String getFirstIPAddress() {
		long ipAsLong = ipToLong();
		return longToIp(ipAsLong);
	}

	/**
	 * Gets the last IP address in the current subnet. For example, if current
	 * subnet is 192.168.1.0/24, then the last IP address would be 192.168.1.255
	 */
	public String getLastIPAddress() {
		long ipAsLong = ipToLong() + getMaxIpAddresses() - 1;
		return longToIp(ipAsLong);
	}

	/**
	 * Gets the next subnet with this mask in the enclosing subnet. For example,
	 * if current subnet is 192.168.1.0/24, next subnet would be 192.168.1.0/24.
	 */
	public Subnet getNext() {
		return getNext(1);
	}
	
	/**
	 * Gets the next subnet with this mask in the enclosing subnet. For example,
	 * if current subnet is 192.168.1.0/24, next(0) subnet would be
	 * 192.168.1.0/24, next(1) subnet would be 192.168.2.0/24, next(2) subnet
	 * would be 192.168.3.0/24, and so on.
	 * 
	 * @param idx 0 based index. 0 means this subnet, 1 means the immediate next
	 *        subnet, 2 means next subnet after next, 3 means next subnet after
	 *        next after next ... and so on
	 */
	public Subnet getNext(int idx) {
		long ipAsLong = ipToLong() + (idx * getMaxIpAddresses());
		return new Subnet(longToIp(ipAsLong), maskSize);
	}

	/**
	 * Finds next available inner subnet with the new-mask after the given
	 * subnet. For example, if the current subnet is 192.168.0.0/16, 'after'
	 * subnet is 192.168.5.0/24, and new-mask is 24, then the subnet returned
	 * would be 192.168.6.0/24. Another example, if the current subnet is
	 * 192.168.0.0/16, 'after' subnet is 192.168.5.0/24, and new-mask is 22,
	 * then the subnet returned would be 192.168.64.0/22
	 */
	public Subnet getNextAvailableSubnet(Subnet after, int newMask) {
		Subnet toCompare = createInnerSubnet(newMask);
		
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
	
	/**
	 * Gets the previous subnet with this mask in the enclosing subnet. For
	 * example, if current subnet is 192.168.1.0/24, previous subnet would be
	 * 192.168.0.0/24
	 */
	public Subnet getPrevious() {
		long ipAsLong = ipToLong() - getMaxIpAddresses();
		return new Subnet(longToIp(ipAsLong), maskSize);
	}

	public Subnet createInnerSubnet(int newMask) {
		return new Subnet(getFirstIPAddress(), newMask);
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
		int h = 4 ^ 1;
		for (int i = 3; i >= 0; i--) {
			result |= octets[3 - i] << (i * 8);
		}
		return result;
	  }	
	
	public String toString() {
		return String.format("%d.%d.%d.%d/%d", octets[0], octets[1], octets[2], octets[3], maskSize);
	}
}
