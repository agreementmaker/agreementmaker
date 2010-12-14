/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * The original class is copied from http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037
 * Submitted by "liquidcs" to the Java Bug Database.
 *
 * Improvements are @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________
 * 
 * 
 * @date Aug 4, 2010.  @author Cosmin.
 * @description Initial implementation, added getHostAddress4().          
 * 
 *  
 */

package am.utility;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class LinuxInetAddress {

	/**
	 * Returns an InetAddress representing the IPv4 address of the localhost.  
	 * Every attempt is made to find an address for this host that is not 
	 * the loopback address.  If no other address can be found, the 
	 * loopback will be returned.
	 * 
	 * @return InetAddress - the IPv4 address of localhost
	 * @throws UnknownHostException - if there is a problem determing the address
	 */
	public static InetAddress getLocalHost4() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		if(!localHost.isLoopbackAddress()) return localHost;
		InetAddress[] addrs = getAllLocalUsingNetworkInterface();
		for(int i=0; i<addrs.length; i++) {
			if(!addrs[i].isLoopbackAddress() && addrs[i] instanceof Inet4Address) 
				return addrs[i];
		}
		return localHost;	
	}
	
	/**
	 * Returns an InetAddress representing the address of the localhost.  
	 * Every attempt is made to find an address for this host that is not 
	 * the loopback address.  If no other address can be found, the 
	 * loopback will be returned.
	 * 
	 * @return InetAddress - the address of localhost
	 * @throws UnknownHostException - if there is a problem determing the address
	 */
	public static InetAddress getLocalHost() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		if(!localHost.isLoopbackAddress()) return localHost;
		InetAddress[] addrs = getAllLocalUsingNetworkInterface();
		for(int i=0; i<addrs.length; i++) {
			if(!addrs[i].isLoopbackAddress()) 
				return addrs[i];
		}
		return localHost;	
	}
	
	/**
	 * This method attempts to find all InetAddresses for this machine in a 
	 * conventional way (via InetAddress).  If only one address is found 
	 * and it is the loopback, an attempt is made to determine the addresses 
	 * for this machine using NetworkInterface.
	 * 
	 * @return InetAddress[] - all addresses assigned to the local machine
	 * @throws UnknownHostException - if there is a problem determining addresses
	 */
	public static InetAddress[] getAllLocal() throws UnknownHostException {
		InetAddress[] iAddresses = InetAddress.getAllByName("127.0.0.1");
		if(iAddresses.length != 1) return iAddresses;
		if(!iAddresses[0].isLoopbackAddress()) return iAddresses;
		return getAllLocalUsingNetworkInterface();
	}
	
	/**
	 * Utility method that delegates to the methods of NetworkInterface to 
	 * determine addresses for this machine.
	 * 
	 * @return InetAddress[] - all addresses found from the NetworkInterfaces
	 * @throws UnknownHostException - if there is a problem determining addresses
	 */
	private static InetAddress[] getAllLocalUsingNetworkInterface() throws UnknownHostException {
		ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException ex) {
			throw new UnknownHostException("127.0.0.1");
		}
		while(e.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface)e.nextElement();
			for(Enumeration<InetAddress> e2 = ni.getInetAddresses(); e2.hasMoreElements();) {
				addresses.add(e2.nextElement());
			}	
		}
		InetAddress[] iAddresses = new InetAddress[addresses.size()];
		for(int i=0; i<iAddresses.length; i++) {
			iAddresses[i] = (InetAddress)addresses.get(i);
		}
		return iAddresses;
	}
}

