package com.thetechwarriors.cidrutils;

import java.util.HashMap;
import java.util.Map;

public class SubnetAllocator {

	private Map<String, EnvironmentConfig> configs = new HashMap<>();
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
		monitor.markForSkipping(18);
	}
	
	public Environment createEnvironment(String name, String configName) {
		EnvironmentConfig config = configs.get(configName);		
		Subnet subnet = monitor.getNextAvailableSubnet(config.getMaskSize());
		return new Environment(monitor, name).withSubnetGroups(subnet, config.getSubnetGroups()) ;
	}
	
	
}
