package com.nestify.entities;

import java.time.LocalDateTime;

import com.nestify.entities.enums.MemberRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "house_members", uniqueConstraints = {
	@UniqueConstraint(name = "uk_house_user", columnNames = { "house_id", "user_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "house_id", nullable = false)
	private House house;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MemberRole memberRole;
	@Column(name = "joined_at")
	private LocalDateTime joinedAt;

	/**
	 * Sets the join timestamp before the membership entity is persisted.
	 */
	@PrePersist
	protected void onJoin() {
		this.joinedAt = LocalDateTime.now();
	}

	/**
	 * Checks whether this member has the ADMIN role.
	 *
	 * @return true if member role is ADMIN, false otherwise
	 */
	public boolean isAdmin() {
		return this.memberRole == MemberRole.ADMIN;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HouseMember that = (HouseMember) o;
		if (id != null && that.id != null) {
			return id.equals(that.id);
		}
		Long thisHouseId = (house != null) ? house.getId() : null;
		Long thatHouseId = (that.house != null) ? that.house.getId() : null;
		Long thisUserId = (user != null) ? user.getId() : null;
		Long thatUserId = (that.user != null) ? that.user.getId() : null;
		return java.util.Objects.equals(thisHouseId, thatHouseId) && java.util.Objects.equals(thisUserId, thatUserId);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
