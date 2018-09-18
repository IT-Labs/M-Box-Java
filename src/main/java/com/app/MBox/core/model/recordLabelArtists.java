package com.app.MBox.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class recordLabelArtists extends audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "recordLabelId" , foreignKey = @ForeignKey(name = "FK_recordLabelArtists_recordLabel"))
    private recordLabel recordLabel;

    @OneToOne
    @JoinColumn(name="artistId" , foreignKey = @ForeignKey(name = "FK_recordLabelArtist_artist"))
    private artist artist;


}
