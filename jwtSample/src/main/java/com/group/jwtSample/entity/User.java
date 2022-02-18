package com.group.jwtSample.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity // 데이터베이스 테이블과 1대1 매핑되는 객체
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonIgnore
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @JsonIgnore
    @Column(name = "activated")
    private boolean activated;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "users_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authoritys_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities;
    /*
    Set -> HashSet, TreeSet 등
    Collection 의 종류 증 하나

    특징
    - 요소의 저장 순서를 유지하지 않음
    - 같은 요소의 중복 저장을 허용하지않음

    * */
}
