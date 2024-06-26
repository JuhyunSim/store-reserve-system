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
    NOT_VERIFIED(HttpStatus.BAD_REQUEST, "인증되지 않은 아이디입니다."),
    CHECK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 확인해주세요."),
    FAIL_TO_WITHDRAWAL(HttpStatus.BAD_REQUEST,
            "회원탈퇴가 실패하였습니다. 아이디와 비밀번호를 확인해주세요."),
    NOT_FOUND_RESERVATION(HttpStatus.BAD_REQUEST,
            "입력하신 정보와 일치하는 예약 정보가 없습니다."),

    //search
    NO_STORE_MATCH(HttpStatus.BAD_REQUEST, "검색어와 일치하는 매장이 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "검색어를 입력하세요."),

    //reserve
    EXPIRED_RESERVATION(HttpStatus.BAD_REQUEST,
            "예약 유효시간이 만료되었습니다. 다시 예약을 진행해주세요."),
    RESERVE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "현재 예약이 불가능한 매장입니다."),
    NOT_FOUND_RESERVATION_HISTORY(HttpStatus.BAD_REQUEST, "예약내역을 찾을 수 없습니다."),
    NOT_AUTH_UPDATE_RESERVATION(HttpStatus.BAD_REQUEST, "예약내역 수정 권한이 없습니다."),
    DENIED_RESERVATION(HttpStatus.BAD_REQUEST, "예약 승인이 거부되었습니다."),

    //review
    NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 찾을 수 없습니다."),
    NO_TEXT(HttpStatus.BAD_REQUEST, "리뷰 내용을 입력하세요."),
    NO_RATING(HttpStatus.BAD_REQUEST, "평점을 입력하세요."),
    NOT_AUTH_REVIEW(HttpStatus.BAD_REQUEST, "매장을 이용한 고객만 리뷰를 작성할 수 있습니다."),
    NOT_AUTH_UPDATE_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 작성한 고객만 리뷰를 작성할 수 있습니다."),

    //store
    ALREADY_EXIST_STORE(HttpStatus.BAD_REQUEST, "이미 존재하는 매장입니다."),
    NOT_AUTH_UPDATE_STORE(HttpStatus.BAD_REQUEST, "매장 수정 권한이 없습니다."),
    NOT_AUTH_DELETE_STORE(HttpStatus.BAD_REQUEST, "매장 수정 권한이 없습니다."),
    FAIL_TO_DELETE_STORE(HttpStatus.BAD_REQUEST, "매장 삭제에 실패하였습니다.");

    private HttpStatus httpStatus;
    private String message;
}
