package com.thetechwarriors.cidrutils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubnetTest {

	private Subnet large = new Subnet("10.10.0.0", 16); 
	private Subnet medium = new Subnet("10.10.64.0", 24); 
	private Subnet small = new Subnet("10.10.64.128", 26); 

	@Test
	public void testFirstIPAddress() {
		assertEquals("10.10.0.0", large.getFirstIPAddress());
		assertEquals("10.10.64.0", medium.getFirstIPAddress());
		assertEquals("10.10.64.128", small.getFirstIPAddress());
	}
	
	@Test
	public void testLastIPAddress() {
		assertEquals("10.10.255.255", large.getLastIPAddress());
		assertEquals("10.10.64.255", medium.getLastIPAddress());
		assertEquals("10.10.64.191", small.getLastIPAddress());
	}

	@Test
	public void testNext() {
		assertEquals("10.11.0.0/16", large.getNext().toString());
		assertEquals("10.10.65.0/24", medium.getNext().toString());
		assertEquals("10.10.64.192/26", small.getNext().toString());
	}

	@Test
	public void testNextWithIndex() {
		assertEquals("10.12.0.0/16", large.getNext(2).toString());
		assertEquals("10.10.66.0/24", medium.getNext(2).toString());
		assertEquals("10.10.65.0/26", small.getNext(2).toString());
	}

	@Test
	public void testInnerSubnet() {
		assertEquals("10.10.0.0/18", large.createInnerSubnet(18).toString());
		assertEquals("10.10.64.0/26", medium.createInnerSubnet(26).toString());
		assertEquals("10.10.64.128/28", small.createInnerSubnet(28).toString());
	}

	@Test
	public void testNextSubnetAfter() {
		
		Subnet after = new Subnet("10.10.64.160", 27);
		
		assertEquals("10.10.64.192/26", large.getNextAvailableSubnet(after, 26).toString());
		assertEquals("10.10.65.0/24", medium.getNextAvailableSubnet(after, 24).toString());
		assertEquals("10.10.65.0/25", small.getNextAvailableSubnet(after, 25).toString());
	}
}
