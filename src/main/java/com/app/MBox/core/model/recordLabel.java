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
@EntityListeners(AuditingEntityListener.class)
public class recordLabel  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(length = 500)
    private String aboutInfo;

    @OneToMany(mappedBy = "recordLabel",cascade = CascadeType.ALL)
    private Set<recordLabelArtists> recordLabelArtists;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="userId" , foreignKey = @ForeignKey(name = "FK_recodLabel_User"))
    private users user;

}
