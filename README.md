# store-reserve-system
상점 예약 시스템 프로젝트(partner, customer 기능 구현)


## 공통 기능
회원 가입
---
- [ v ] 인증 이메일 발송 (mailgun open api 활용) 

- [ v ] 회원 인증

- [   ] 회원 탈퇴

로그인
---
- [ v ] jwt 토큰 발행 (customer / partner 구분)
- [   ] 로그아웃
- [   ] 비밀번호, 휴대폰번호, 주민등록 번호 암호화 

파트너 기능
---
- [ v ] 매장 추가 (매장 위치 정보 어떻게 처리할지 추가 정리)
- [ v ] 매장 조회 
- [ v ] 매장 수정 (예약 가능 여부 정보 제공)
- [ v ] 매장 삭제
- [ v ] 예약 승인
- [ v ] 리뷰 삭제

고객 기능
---
- [ v ] : 예약 
    - [ v ] 예약 이후 10 분 후 키오스크에서 등록 
    - [ v ] 예약 승인 후 키오스크 등록 가능 
- [ v ] : 리뷰 작성 (예약 및 사용 이후에 가능)
- [ v ] : 리뷰 수정
- [ v ] : 리뷰 조회
- [ v ] : 리뷰 조회

비회원 사용 가능 기능
---
- [ v ] 매장 검색(검색어 자동완성 기능 추가)
- [ v ] 매장 상세정보 조회
- [ v ] 리뷰 조회

서비스 흐름도
---
![상점예약 프로그램](./store-reserve-system-diagram.jpg)

ERD
---
![상점예약 프로그램](./store-reserve-system-erd.jpg)
