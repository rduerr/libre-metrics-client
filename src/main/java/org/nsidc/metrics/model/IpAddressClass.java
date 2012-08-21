/*National Snow & Ice Data Center, University of Colorado, Boulder
  Copyright 2010 Regents of the University of Colorado*/
package org.nsidc.metrics.model;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.sf.javainetlocator.InetAddressLocator;
import net.sf.javainetlocator.InetAddressLocatorException;

/**
 * @author glewis
 *
 */
public enum IpAddressClass {

	NASA_GOV("nasa.gov"),
	USGS_GOV("usgs.gov"),
	NOAA_GOV("noaa.gov"),
	EPA_GOV("epa.gov"),
	NGA_GOV("nga.gov"),
	USDA_GOV("usda.gov"),
	DOT_GOV("dot.gov"),
	GOV(".gov"),
	MIL(".mil"),
	US(".us"),
	EDU(".edu"),
	ORG(".org"),
	COM(".com"),
	NET(".net"),
	NON_US("non-US", true),
	UNRESOLVED("unresolved", true);
	
	private String domain;
	private boolean special = false;
	
	private IpAddressClass(String domain) {
		this(domain, false);
	}
	
	private IpAddressClass(String domain, boolean special) {
		this.domain = domain;
		this.special = special;
	}
	
	public boolean isSpecial() {
		return special;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String toString() {
		return domain;
	}
	
	public static Set<IpAddressClass> getMatchingDomains(String host) {
		Set<IpAddressClass> doms = new HashSet<IpAddressClass>();
		
		// Find all matching domains
		for (IpAddressClass c : IpAddressClass.values()) {
			// Special ones are handled individually
			if (c.isSpecial()) 
				continue;
			
			// If the host ends with this domain, add it
			if (host.endsWith(c.getDomain()))
				doms.add(c);
		}
		
		try {
			Locale l = InetAddressLocator.getLocale(host);
			if (!l.getCountry().equals("US")) {
				doms.add(IpAddressClass.NON_US);
			}
		} catch (InetAddressLocatorException e) {
			// Do nothing, DB is corrupt and we can't get locale info
		}
		
		// If no domains were found that match, add the unresolved domain
		if (doms.isEmpty()) 
			doms.add(IpAddressClass.UNRESOLVED);
		
		return doms;
	}
}
