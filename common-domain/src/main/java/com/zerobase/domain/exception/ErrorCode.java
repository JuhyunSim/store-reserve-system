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
    CHECK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 확인해주세요."),
    FAIL_TO_WITHDRAWAL(HttpStatus.BAD_REQUEST,
            "회원탈퇴가 실패하였습니다. 아이디와 비밀번호를 확인해주세요."),
    NOT_FOUND_RESERVATION(HttpStatus.BAD_REQUEST,
            "입력하신 정보와 일치하는 예약 정보가 없습니다."),
    VALID_TIME_EXPIRED(HttpStatus.BAD_REQUEST,
            "예약 유효시간이 만료되었습니다. 다시 예약을 진행해주세요."),

    //review
    NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 찾을 수 없습니다."),
    NO_TEXT(HttpStatus.BAD_REQUEST, "리뷰 내용을 입력하세요."),
    NO_RATING(HttpStatus.BAD_REQUEST, "평점을 입력하세요."),
    NOT_AUTH_REVIEW(HttpStatus.BAD_REQUEST, "매장을 이용한 고객만 리뷰를 작성할 수 있습니다."),
    NOT_AUTH_UPDATE_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 작성한 고객만 리뷰를 작성할 수 있습니다.")
    ;


    private HttpStatus httpStatus;
    private String message;
}
