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

public class VipcCidrRanges {

	public static void main(String[] args) throws Exception {
		new VipcCidrRanges().calculate();
	}
		
	public void calculate() throws Exception {
		
		EnvironmentBluePrint small = new EnvironmentBluePrint()
				.withRangeGroup("svcs", 27, 4)
				.withRangeGroup("apps", 28, 4)
				.withRangeGroup("cass", 28, 3);
		
		EnvironmentBluePrint large = new EnvironmentBluePrint()
				.withRangeGroup("apps", 22, 4)
				.withRangeGroup("cass", 24, 3)
				.withRangeGroup("svcs", 25, 4);

		EnvironmentBluePrint acorn = new EnvironmentBluePrint()
				.withRangeGroup("svcs", 24, 4);
		
		Range vpc = new Range("172.28.0.0", 16);
		Range largeEnvRange = vpc.getSubRange(18);
		
		Range acornRange = largeEnvRange.getNext(0).getSubRange(20);
		System.out.println(createEnvironment("sequoia", acornRange.getNext(0), acorn));
		System.out.println(createEnvironment("acorn", acornRange.getNext(1), acorn));
		
		Range smallRange = acornRange.getNext(3).getSubRange(24);
		System.out.println(createEnvironment("smalik", smallRange.getNext(0), small));
		System.out.println(createEnvironment("rsutton", smallRange.getNext(1), small));
		System.out.println(createEnvironment("bboppana", smallRange.getNext(2), small));

		System.out.println(createEnvironment("test", largeEnvRange.getNext(2), large));
		System.out.println(createEnvironment("prod", largeEnvRange.getNext(3), large));
	}

	private Environment createEnvironment(String name, Range startingRange, EnvironmentBluePrint bluePrint) {
		return new Environment(name).withRangeGroups(startingRange, bluePrint.getRangeGroups()) ;
	}
}
