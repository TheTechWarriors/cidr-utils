package com.thetechwarriors.cidrutils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubnetAllocator {
	
	private Map<String, EnvironmentConfig> configs = new HashMap<>();
	private Map<String, Environment> environments = new LinkedHashMap<>();
	private SubnetAllocationMonitor monitor;
	private Subnet subnet;

	public SubnetAllocator(String ipAddress, int mask) {
		subnet = new Subnet(ipAddress, mask);
		monitor = new SubnetAllocationMonitor(subnet);
	}
	
	public SubnetAllocator withEnvironmentConfig(String name, EnvironmentConfig config) {
		this.configs.put(name, config);
		return this;
	}
	
	public void skip(int mask) {
		monitor.markForSkipping(mask);
	}
	
	public Environment addEnvironment(String name, String configName) {
		EnvironmentConfig config = configs.get(configName);		
		Subnet subnet = monitor.getNextAvailableSubnet(config.getMaskSize());
		Environment environment = new Environment(monitor, name).withSubnetGroups(subnet, config.getSubnetGroups()) ;
		environments.put(name, environment);
		return environment;
	}
	
	public Map<String, Environment> getEnvironments() {
		return environments;
	}
	
	public Subnet getSubnet() {
		return subnet;
	}
}
