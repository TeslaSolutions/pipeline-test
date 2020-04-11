package com.dream.models;

import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "tag")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class Tag {
	
	@Id
	@NonNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
	
	@Column(name = "name", nullable = false)
    private String name;
    
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Dream> dreams;
}
