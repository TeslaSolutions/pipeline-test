package com.dream.models;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements Serializable {

	private static final long serialVersionUID = -1318296280155480498L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "username", unique = true, nullable = false, length = 100)
	private String username;
	
	@Column(name = "email", unique = true, nullable = false, length = 100)
	@Email
	private String email;

	/**
 	 * password should be encrypted
 	 *
	 */
	@Column(name = "password", unique = false, nullable = false)
	private String password;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "USER_AUTHORITY",
			joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
			inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
	private List<Authority> authorities;
	/**
	 * this field is required for authentication
	 * */
	@Column
	private boolean enabled;

	@Override
	public boolean equals(Object obj) {
		if(obj == null || (!getClass().equals(obj.getClass()))){
			return false;
		}

		final User other = (User) obj;
		if(other.getId() != this.getId()){
			return false;
		}else if ( !other.getEmail().equals(this.getEmail())){
			return false;
		}else{
			return true;
		}
	}
}
