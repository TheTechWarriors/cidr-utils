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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Environment {
	
	private SubnetAllocationMonitor monitor;
	private Map<String, List<Subnet>> subnetGroups = new LinkedHashMap<String, List<Subnet>>();
	private String name;
	
	public Environment(SubnetAllocationMonitor monitor, String name) {
		this.monitor = monitor;
		this.name = name;
	}
	
	public Environment withSubnetGroups(Subnet startingRange, SubnetGroup... groups) {
	
		Subnet lastSubnetInGroup = null;
		Subnet nextStartingSubnet = null;
		
		for(SubnetGroup group : groups) {
			
			int mask = group.getMask();
			if (nextStartingSubnet == null) {
				nextStartingSubnet = startingRange.createInnerSubnet(mask);
			} else {
				nextStartingSubnet = startingRange.getNextAvailableSubnet(lastSubnetInGroup, mask);
			}
			
			List<Subnet> nets = group.getSubnets(nextStartingSubnet);
			if (group.isSkip()) {
				for (Subnet net : nets) {
					monitor.markForSkipping(net);
				}
			} else {
				subnetGroups.put(group.getName(), nets);
				for (Subnet net : nets) {
					monitor.markForInUse(net);
				}
			}
			
			lastSubnetInGroup = nets.get(nets.size()-1);
		}
		
		return this;
	}
	
	public Map<String, List<Subnet>> getSubnetGroups() {
		return subnetGroups;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String type : subnetGroups.keySet()) {
			int i = 0;
			for(Subnet subnet : subnetGroups.get(type)) {
				String str = String.format("Env=%-10s Type=%-10s Subnet=%-20s IPs=%d", name, type + "-" + i++, subnet, subnet.getMaxIpAddresses());
				builder.append(str).append("\n");
			}
		}
		return builder.toString();
	}
}
