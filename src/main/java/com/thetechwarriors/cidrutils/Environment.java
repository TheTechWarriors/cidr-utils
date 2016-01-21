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
	
	private Map<String, List<Range>> subnets = new LinkedHashMap<String, List<Range>>();
	private String name;
	
	public Environment(String name) {
		this.name = name;
	}
	
	public Environment withRangeGroups(Range startingRange, RangeGroup... groups) {
	
		Range lastRangeInGroup = null;
		Range nextStartingRange = null;
		
		for(RangeGroup group : groups) {
			
			if (nextStartingRange == null) {
				nextStartingRange = startingRange.getSubRange(group.getMask());
			} else {
				nextStartingRange = startingRange.getNextAvailableSubRange(lastRangeInGroup, group.getMask());
			}
			
			List<Range> ranges = group.getRanges(nextStartingRange);
			subnets.put(group.getName(), ranges);
			
			lastRangeInGroup = ranges.get(ranges.size()-1);
		}
		
		return this;
	}
	
	public Map<String, List<Range>> getSubnets() {
		return subnets;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String type : subnets.keySet()) {
			int i = 0;
			for(Range range : subnets.get(type)) {
				String str = String.format("Env=%-10s Type=%-10s Subnet=%-20s IPs=%d", name, type + "-" + i++, range, range.getMaxIpAddresses());
				builder.append(str).append("\n");
			}
		}
		return builder.toString();
	}
}
