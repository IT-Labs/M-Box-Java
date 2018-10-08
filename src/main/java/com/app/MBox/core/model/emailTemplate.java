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
@Table(uniqueConstraints =@UniqueConstraint(name = "UC_emailTemplate_name", columnNames = "name") )
@EntityListeners(AuditingEntityListener.class)
public class emailTemplate extends audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(length = 50,nullable = false)
    private String name;

    @Column(length = 50)
    private String subject;

    private String body;

}
