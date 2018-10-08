package com.app.MBox.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints =@UniqueConstraint(name = "UC_role_name", columnNames = "name") )
@EntityListeners(AuditingEntityListener.class)
public class role extends audit  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false,length = 20)
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<userRoles> userRoles;


}
