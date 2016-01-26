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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Main {

	public static void main(String[] args) throws Exception {
		
		SubnetAllocator allocator = new SubnetAllocator("172.28.0.0", 16)
				
			.withEnvironmentConfig("small",
				new EnvironmentConfig(24)
					.withSubnetGroup("svcs", 27, 4)
					.withSubnetGroup("apps", 28, 4)
					.withSubnetGroup("cass", 28, 3))
			
			.withEnvironmentConfig("large",
				new EnvironmentConfig(18)
					.withSubnetGroup("apps", 22, 4)
					.withSubnetsToSkip(22, 4)
					.withSubnetGroup("cass", 24, 3)
					.withSubnetsToSkip(24, 1)
					.withSubnetGroup("svcs", 25, 4))
			
			.withEnvironmentConfig("utility",
				new EnvironmentConfig(20)
					.withSubnetGroup("svcs", 24, 4)
					.withSubnetsToSkip(24, 12));
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		ObjectNode rootNode = mapper.createObjectNode();
		ObjectNode cidrsNode = rootNode.putObject("Mappings").putObject("VpcSubnetCidrs");

		printUtilEnvironment(cidrsNode, allocator.createEnvironment("sequoia", "utility"));
		printEnvironment(cidrsNode, allocator.createEnvironment("acorn", "utility"));
		
		printEnvironment(cidrsNode, allocator.createEnvironment("acowan", "small"));
		printEnvironment(cidrsNode, allocator.createEnvironment("bboppana", "small"));
		printEnvironment(cidrsNode, allocator.createEnvironment("jrazgunas", "small"));
		printEnvironment(cidrsNode, allocator.createEnvironment("kalexander", "small"));
		printEnvironment(cidrsNode, allocator.createEnvironment("rsutton", "small"));
		printEnvironment(cidrsNode, allocator.createEnvironment("sellers", "small"));
		printEnvironment(cidrsNode, allocator.createEnvironment("smalik", "small"));

		allocator.skip(18);
		printEnvironment(cidrsNode, allocator.createEnvironment("test", "large"));
		printEnvironment(cidrsNode, allocator.createEnvironment("prod", "large"));
		
		System.out.println(mapper.writeValueAsString(rootNode));
	}

	static public void printUtilEnvironment(ObjectNode root, Environment env) {
		ObjectNode main = root.putObject(env.getName());
		doSubnetGroup(env.getSubnetGroups().get("svcs"), main);
	}
	
	static public void printEnvironment(ObjectNode root, Environment env) {
		ObjectNode main = root.putObject(env.getName());
		doSubnetGroup(env.getSubnetGroups().get("apps"), "Apps", main);
		doSubnetGroup(env.getSubnetGroups().get("cass"), "Cass", main);
		doSubnetGroup(env.getSubnetGroups().get("svcs"), "Svcs", main);
	}

	static private void doSubnetGroup(List<Subnet> subnets, String prefix, ObjectNode main) {
		doSubnetGroup(subnets, prefix, false, main);
	}

	static private void doSubnetGroup(List<Subnet> subnets, ObjectNode main) {
		doSubnetGroup(subnets, null, true, main);
	}

	static private void doSubnetGroup(List<Subnet> subnets, String prefix, boolean noPrefix, ObjectNode main) {
		String[] az = { "us-east-1a", "us-east-1c", "us-east-1d", "us-east-1e" };
		if (subnets != null) {
			int i = 0;
			for (Subnet s : subnets) {
				main.put((noPrefix ? "Az" : prefix + "Az") + (i+1), az[i]);
				main.put((noPrefix ? "Cidr" : prefix + "Cidr") + (i+1), s.toString());
				i++;
			}
		}
	}
}
