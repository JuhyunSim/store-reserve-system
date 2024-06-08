package com.zerobase.domain.entity;

import com.zerobase.domain.requestForm.SignUpForm;
import com.zerobase.domain.security.common.UserType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class CustomerEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String phone;
    private String registerNumber;
    private UserType userType;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;


    public static CustomerEntity from(SignUpForm signUpForm) {
        return CustomerEntity.builder()
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
                .name(signUpForm.getName())
                .phone(signUpForm.getPhone())
                .registerNumber(signUpForm.getRegisterNumber())
                .userType(UserType.CUSTOMER)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of
                (new SimpleGrantedAuthority(
                                "ROLE_" + this.getUserType()
                        )
                );
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
