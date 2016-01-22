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

public class EnvironmentConfig {

	private List<SubnetGroup> groups = new ArrayList<SubnetGroup>();
	
	public EnvironmentConfig withSubnetGroup(String name, int maskSize, int numSubnets) {
		groups.add(new SubnetGroup(name, maskSize, numSubnets));
		return this;
	}

	public SubnetGroup[] getSubnetGroups() {
		return groups.toArray(new SubnetGroup[groups.size()]);
	}
}
