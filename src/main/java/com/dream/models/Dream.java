package com.dream.models;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "dream")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Dream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "dream_description", nullable = false, columnDefinition = "VARCHAR(2048)")
    private String dreamDescription;

    @CreatedDate
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "likes_no")
    private int likesNo;

    @Column(name = "dislikes_no")
    private int dislikesNo;

    @Column(name = "same_dream_no")
    private int sameDreamNo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dream_tag",
            joinColumns = @JoinColumn(name = "dream_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Tag> tags;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @OneToMany(mappedBy = "dream", cascade = CascadeType.ALL)
    private Set<Comment> comments;
}
