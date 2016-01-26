package com.thetechwarriors.cidrutils;

import java.util.LinkedHashSet;
import java.util.Set;

public class SubnetAllocationMonitor {

	private Subnet topLevel ;
	
	private Set<Subnet> used = new LinkedHashSet<>();
	private Set<Subnet> skipped = new LinkedHashSet<>();
	
	public SubnetAllocationMonitor(Subnet topLevel) {
		this.topLevel = topLevel;
	}
	
	public void markForInUse(Subnet mark) {
		used.add(mark);
	}
	
	public void markForSkipping(Subnet mark) {
		skipped.add(mark);
	}

	public void markForSkipping(int mask) {
		Subnet skip = getNextAvailableSubnet(mask);
		skipped.add(skip);
	}
	
	public Subnet getNextAvailableSubnet(int mask) {
		
		Subnet highestInUse = null;
		for (Subnet iu : used) {
			if (highestInUse == null || iu.isAfter(highestInUse)) {
				highestInUse = iu;
			}
		}
		
		for (Subnet skip : skipped) {
			if (highestInUse == null || skip.isAfter(highestInUse)) {
				highestInUse = skip;
			}
		}

		if (highestInUse != null) {
			return topLevel.getNextAvailableSubnet(highestInUse, mask);
		} else {
			return topLevel.createInnerSubnet(mask);
		}
	}
}
