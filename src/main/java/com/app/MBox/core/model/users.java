package com.app.MBox.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints =@UniqueConstraint(name = "UC_users_picture", columnNames = "picture") )
@EntityListeners(AuditingEntityListener.class)
public class users extends audit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false,length = 320)
    @Email
    private String email;

    @Column(nullable = false,length = 50)
    private String picture;

    @Column(nullable = false,length = 50)
    private String name;

    @Column(nullable = false,length =30)
    private String password;


    private Date dateOfBirth;

    @Column(nullable = false)
    private boolean isActivated;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<verificationToken> tokens;


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<userRoles> userRoles;


}
