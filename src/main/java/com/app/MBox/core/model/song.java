package com.app.MBox.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints =@UniqueConstraint(name = "UC_song_image", columnNames = "image") )
@EntityListeners(AuditingEntityListener.class)
public class song extends audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(length = 50)
    private String name;

    @Column(nullable = true,length = 50)
    private String albumName;

    private String lyrics;

    @Column(length = 50)
    private String image;

    private Date dateOfRelease;

    @Column(length = 100)
    private String youtubeLink;

    @Column(length = 100)
    private String vimeoLink;

    private String genre;

    @ManyToOne
    @JoinColumn(name = "artistId",foreignKey = @ForeignKey(name = "FK_song_artist"))
    private artist artist;



}
