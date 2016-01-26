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

	private SubnetAllocationMonitor monitor;
	private Subnet subnet;
	
	public static void main(String[] args) throws Exception {
		new Main("172.28.0.0", 16).calculate();
	}
		
	public Main(String ipAddress, int mask) {
		subnet = new Subnet(ipAddress, mask);
		monitor = new SubnetAllocationMonitor(subnet);
	}
	
	public void calculate() throws Exception {
		
		EnvironmentConfig small = new EnvironmentConfig(24)
				.withSubnetGroup("svcs", 27, 4)
				.withSubnetGroup("apps", 28, 4)
				.withSubnetGroup("cass", 28, 3);
		
		EnvironmentConfig large = new EnvironmentConfig(18)
				.withSubnetGroup("apps", 22, 4)
				.withSubnetsToSkip(22, 4)
				.withSubnetGroup("cass", 24, 3)
				.withSubnetsToSkip(24, 1)
				.withSubnetGroup("svcs", 25, 4);

		EnvironmentConfig acorn = new EnvironmentConfig(20)
				.withSubnetGroup("svcs", 24, 4)
				.withSubnetsToSkip(24, 12);
		
		System.out.println(createEnvironment("sequoia", acorn));
		System.out.println(createEnvironment("acorn", acorn));
		
		System.out.println(createEnvironment("smalik", small));
		System.out.println(createEnvironment("rsutton", small));
		System.out.println(createEnvironment("bboppana", small));

		monitor.markForSkipping(18);
		System.out.println(createEnvironment("test", large));
		System.out.println(createEnvironment("prod", large));
	}

	private Environment createEnvironment(String name, EnvironmentConfig config) {
		Subnet subnet = monitor.getNextAvailableSubnet(config.getMaskSize());
		return new Environment(monitor, name).withSubnetGroups(subnet, config.getSubnetGroups()) ;
	}
}
