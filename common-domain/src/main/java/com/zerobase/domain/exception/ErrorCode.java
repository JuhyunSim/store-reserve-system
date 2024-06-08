package com.zerobase.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_BOOKED_CUSTOMER(HttpStatus.BAD_REQUEST, "이미 예약한 고객입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "존재하지 않는 매장입니다."),
    NOT_FOUND_BOOK(HttpStatus.BAD_REQUEST, "예약내역을 찾을 수 없습니다."),
    CHECK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 확인해주세요.")
    ;

    private HttpStatus httpStatus;
    private String message;
}
