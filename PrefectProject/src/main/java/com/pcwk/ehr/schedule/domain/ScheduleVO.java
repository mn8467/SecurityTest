package com.pcwk.ehr.schedule.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // getter
@Setter // setter
@NoArgsConstructor // default 생성자
@AllArgsConstructor // 모든인자 생성자
public class ScheduleVO {

	private int scheduleID;// 일정ID
	private int calID;// 날짜ID
	private String title;// 제목
	private String explanation;// 내용
	
	@Override
	public String toString() {
		return "ScheduleVO [scheduleID=" + scheduleID + ", calID=" + calID + ", title=" + title + ", explanation="
				+ explanation + "]";
	}
	
	
}
