package com.syf.imgurapp.imgurapp.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGEID")
    private Long imageId;

    @Column(name = "IMGUR_ID")
    private String imgurId;

    @Column(name = "DELETE_HASH")
    private String deleteHash;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "URL")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonBackReference
    private User user;
}
