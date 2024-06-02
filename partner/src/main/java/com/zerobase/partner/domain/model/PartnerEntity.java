package com.zerobase.partner.domain.model;

import com.zerobase.partner.domain.SignUpForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride
public class PartnerEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long id;

    private String email;
    private String password;
    private String name;
    private String phone;
    private String registerNumber;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;


    public static PartnerEntity from(SignUpForm signUpForm) {
        return PartnerEntity.builder()
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
                .name(signUpForm.getName())
                .phone(signUpForm.getPhone())
                .registerNumber(signUpForm.getRegisterNumber())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
