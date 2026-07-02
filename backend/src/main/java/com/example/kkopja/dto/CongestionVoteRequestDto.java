package com.example.kkopja.dto;

import com.example.kkopja.constant.CongestionLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CongestionVoteRequestDto {

    private CongestionLevel congestionLevel;
}
