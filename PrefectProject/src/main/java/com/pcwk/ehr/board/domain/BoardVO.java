package com.pcwk.ehr.board.domain;

import com.pcwk.ehr.cmn.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // getter
@Setter // setter
@NoArgsConstructor // default 생성자
@AllArgsConstructor // 모든인자 생성자
public class BoardVO extends DTO {

	private int seq;// 순번
	private String div;// 게시구분
	private String title;// 제목
	private String contents;// 내용
	private int readCnt;// 조회수
	private String regDt;// 등록일
	private String regId;// 등록자
	private String modDt;// 수정일
	private String modId;// 수정자
	private String uuid;
	
	@Override
	public String toString() {
		return "BoardVO [seq=" + seq + ", div=" + div + ", title=" + title + ", contents=" + contents + ", readCnt="
				+ readCnt + ", regDt=" + regDt + ", regId=" + regId + ", modDt=" + modDt + ", modId=" + modId
				+ ", uuid=" + uuid + ", toString()=" + super.toString() + "]";
	}
	
}
