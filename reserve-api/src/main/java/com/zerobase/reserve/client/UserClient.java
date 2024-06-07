package com.zerobase.reserve.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-api", url = "${user-api.feign.client.url}")
public interface UserClient {
    @GetMapping("/info/partner")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> getPartnerInfo(
            @RequestHeader(name = "Authorization") String token
    );

    @PutMapping("/info/customer")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<Integer> getCustomerInfo(
            @RequestHeader(name = "Authorization") String token
    );
}
