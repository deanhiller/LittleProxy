package com.alvazan.proxy.test;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDateTime;

import com.alvazan.proxy.TimeNow;

public class MockNow implements TimeNow {

	private List<LocalDateTime> times = new ArrayList<LocalDateTime>();
	
	public LocalDateTime getCurrentTime() {
		return times.remove(0);
	}
	public void addNextNowTime(LocalDateTime time) {
		times.add(time);
	}
	
}
