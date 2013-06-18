package org.simoes.common;

import org.simoes.util.ConfigResources;

/**
 * You can rely on this always being available to our pages because we will keep it in the session.
 * So try to keep this object lean on memory okay....
 *
 * Also a very useful helper for allowing us to get to the mothods in ConfigResources
 * 
 * @author csimoes
 *
 */
public class Context {

	/**
	 * Always returns http://www.localpages.com or http://localhost:8080
	 * @return
	 */
	public String getRootHost() {
		return ConfigResources.getRootHost();
	}
	
}
