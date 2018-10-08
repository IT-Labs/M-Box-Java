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
@Table(uniqueConstraints =@UniqueConstraint(name = "UC_configuration_key", columnNames = "key") )
@EntityListeners(AuditingEntityListener.class)
public class configuration extends audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(length=100)
    private String key;

    private String value;

}
