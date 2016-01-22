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

public class Main {

	public static void main(String[] args) throws Exception {
		new Main().calculate();
	}
		
	public void calculate() throws Exception {
		
		EnvironmentConfig small = new EnvironmentConfig()
				.withSubnetGroup("svcs", 27, 4)
				.withSubnetGroup("apps", 28, 4)
				.withSubnetGroup("cass", 28, 3);
		
		EnvironmentConfig large = new EnvironmentConfig()
				.withSubnetGroup("apps", 22, 4)
				.withSubnetGroup("cass", 24, 3)
				.withSubnetGroup("svcs", 25, 4);

		EnvironmentConfig acorn = new EnvironmentConfig()
				.withSubnetGroup("svcs", 24, 4);
		
		Subnet vpc = new Subnet("172.28.0.0", 16);
		Subnet largeEnvSubnets = vpc.createInnerSubnet(18);
		
		Subnet acornSubnets = largeEnvSubnets.getNext(0).createInnerSubnet(20);
		System.out.println(createEnvironment("sequoia", acornSubnets.getNext(0), acorn));
		System.out.println(createEnvironment("acorn", acornSubnets.getNext(1), acorn));
		
		Subnet smallSubnets = acornSubnets.getNext(3).createInnerSubnet(24);
		System.out.println(createEnvironment("smalik", smallSubnets.getNext(0), small));
		System.out.println(createEnvironment("rsutton", smallSubnets.getNext(1), small));
		System.out.println(createEnvironment("bboppana", smallSubnets.getNext(2), small));

		System.out.println(createEnvironment("test", largeEnvSubnets.getNext(2), large));
		System.out.println(createEnvironment("prod", largeEnvSubnets.getNext(3), large));
	}

	private Environment createEnvironment(String name, Subnet startingRange, EnvironmentConfig config) {
		return new Environment(name).withSubnetGroups(startingRange, config.getSubnetGroups()) ;
	}
}
