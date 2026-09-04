package com.nestify.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "houses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class House {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "title", nullable = false)
	private String title;
	@Column(name = "address")
	private String address;
	@Column(name = "city")
	private String city;
	@Column(name = "invite_code", nullable = false)
	private String inviteCode;
	@Column(name = "createdAt")
	private LocalDateTime createdAt;
	@Column(name = "updatedAt")
	private LocalDateTime updatedAt;
	@OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<HouseMember> members = new HashSet<>();
	@OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EventCategory> eventCategories = new HashSet<>();
	@OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Event> events = new HashSet<>();
	@OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<HouseNeed> houseNeeds = new HashSet<>();

	/**
	 * Sets the creation timestamp before the house entity is persisted.
	 */
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	/**
	 * Updates the modification timestamp before the house entity is updated.
	 */
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * Adds a user as a member to this house with the designated role.
	 *
	 * @param user The user joining the house
	 * @param role The role assigned to the member
	 */
	public void addMember(User user, MemberRole role) {
		HouseMember houseMember = new HouseMember();
		houseMember.setHouse(this);
		houseMember.setUser(user);
		houseMember.setMemberRole(role);

		this.members.add(houseMember);
	}

	/**
	 * Removes a user from this house's membership list by matching user ID.
	 *
	 * @param user The user to be removed
	 */
	public void removeMember(User user) {
		if (user == null || user.getId() == null) {
			return;
		}

		HouseMember houseMember = this.members.stream()
				.filter(member -> member.getUser() != null && user.getId().equals(member.getUser().getId()))
				.findFirst()
				.orElse(null);

		if (houseMember != null) {
			this.members.remove(houseMember);
			houseMember.setHouse(null);
			houseMember.setUser(null);
		}
	}

}
