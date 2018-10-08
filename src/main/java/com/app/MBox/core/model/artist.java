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
public class artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @Column(length = 500)
    private String bio;
    @Column(nullable = false)
    private boolean isDeleted;
    @OneToMany(mappedBy = "artist",cascade = CascadeType.ALL)
    private Set<song> songs;
    @OneToOne
    @JoinColumn(name="userId" , foreignKey = @ForeignKey(name = "FK_artist_user"))
    private users user;

}


