package com.nestify.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.nestify.entities.enums.MemberRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="houses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class House {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name="title", nullable = false)
	private String title;
	@Column(name="address")
	private String address;
	@Column(name="city")
	private String city;
	@Column(name="invateCode", nullable = false, unique = true)
	private String invateCode;
	@CreatedDate
	@Column(name="createdAt")
	private LocalDateTime createdAt;
	@LastModifiedDate
	@Column(name="updatedAt")
	private LocalDateTime updatedAt;
	@OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<HouseMember> members = new HashSet<>();
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
	public void AddMember(User user, MemberRole role) {
		HouseMember houseMember = new HouseMember();
		houseMember.setHouse(this);
		houseMember.setUser(user);
		houseMember.setMemberRole(role);
		
		this.members.add(houseMember);
		user.getHouseMemberships().add(houseMember);
	}
	
	public void RemoveMember(User user) {
		
		HouseMember houseMember = this.members.stream()
				.filter(member -> member.getUser().equals(user))
				.findFirst()
				.orElse(null);
		
		if(houseMember != null) {
			this.members.remove(houseMember);
			user.getHouseMemberships().remove(houseMember);
			
			houseMember.setHouse(null);
			houseMember.setUser(null);
		}
		
	}
	
}
