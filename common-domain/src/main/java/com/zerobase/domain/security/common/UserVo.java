package com.zerobase.domain.security.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {
    private Long id;
    private String email;
}
