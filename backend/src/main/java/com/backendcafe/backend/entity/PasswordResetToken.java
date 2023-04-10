package com.backendcafe.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@NamedQuery(name = "PasswordResetToken.findByUserId", query = "select p from  PasswordResetToken  p where p.user =:user")
@NamedQuery(name = "PasswordResetToken.findByToken", query = "select p from PasswordResetToken p where p.token=:token")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PasswordResetToken")
public class PasswordResetToken  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;


    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;


    private Boolean status;
    @Column(nullable = false)
    private Date expiryDate;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.status = false;
        expiryDate = new Date(System.currentTimeMillis() + 1 * 86400000);
    }
}
//2*60000