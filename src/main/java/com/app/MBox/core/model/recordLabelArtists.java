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
