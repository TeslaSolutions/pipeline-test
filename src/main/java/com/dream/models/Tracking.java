package com.dream.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tracking")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "dream_id")
    private Long dreamId;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "like_flag")
    private boolean like;

    @Column(name = "dislike")
    private boolean dislike;

    @Column(name = "same_dream")
    private boolean sameDream;
}
