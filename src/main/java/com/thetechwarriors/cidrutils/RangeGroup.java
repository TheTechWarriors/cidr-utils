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

import java.util.ArrayList;
import java.util.List;

public class RangeGroup {
	
	private String name;
	private int mask;
	private int count;
	
	public RangeGroup(String name, int mask, int count) {
		this.name = name;
		this.mask = mask;
		this.count = count;
	}

	public List<Range> getRanges(Range startingRange) {
		List<Range> ranges = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			ranges.add(startingRange.getNext(i));
		}
		return ranges;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMask() {
		return mask;
	}
}
