package com.ixora.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class Main {
	public static void main(String[] args) {
		try {
			StandardMBean mb = new StandardMBean(new SampleMBeanImpl(), SampleMBean.class);
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(mb, new ObjectName("my-domain", "key", "value"));
			
			int maxItems = 100;
			int counterItems = 0;
			while(true) {
				for(int i = 0; i < 10; i++) {
					SampleMBeanImpl.updateStats("itemidx1", "itemidx2-" + (counterItems + i), 
							100 + getRandomLong(100, 200), 200 + getRandomLong(100, 200));
					if(i > 0) {
						SampleMBeanImpl.updateStats("itemidx1", "itemidx2-" + (counterItems + (i-1)), 
							100 + getRandomLong(100, 200), 200 + getRandomLong(100, 200));
					}
				}
				counterItems++;
				counterItems %= maxItems;
				Thread.sleep(5000);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static long getRandomLong(long l1, long l2) {
		return (long)(l1 + (l2-l1)*Math.random());
	}
}
