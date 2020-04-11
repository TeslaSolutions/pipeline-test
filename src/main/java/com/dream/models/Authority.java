package com.dream.models;

import com.dream.utils.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "authority")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Authority implements Serializable{

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;


    public Authority(Role role){
        this.role = role;
    }

}
