package com.app.MBox.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class userRoles extends audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="userId" , foreignKey = @ForeignKey(name = "FK_user_userRoles") )
    private users user;

    @ManyToOne
    @JoinColumn(name="roleId" , foreignKey = @ForeignKey(name = "FK_role_userRoles"))
    private role role;



}
