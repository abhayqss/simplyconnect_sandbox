package com.scnsoft.eldermark.phr.chat.dto;

import java.util.List;

import com.scnsoft.eldermark.web.entity.VideoCallLogDto;

public class VideoCallLogResponseDto {

	private Long totalCount; 
	
	private List<VideoCallLogDto> videoCallLogDto;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public List<VideoCallLogDto> getVideoCallLogDto() {
		return videoCallLogDto;
	}

	public void setVideoCallLogDto(List<VideoCallLogDto> videoCallLogDto) {
		this.videoCallLogDto = videoCallLogDto;
	}
}
