package com.university.club.membership.service;

import com.university.club.identity.entity.User;
import com.university.club.identity.entity.UserStatus;
import com.university.club.identity.repository.UserRepository;
import com.university.club.membership.entity.Membership;
import com.university.club.membership.entity.MembershipStatus;
import com.university.club.membership.entity.Role;
import com.university.club.membership.repository.MembershipRepository;
import com.university.club.membership.repository.RoleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PresidentBootstrapService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;

    public PresidentBootstrapService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void bootstrapPresident(String email) {

        if (email == null || email.isBlank()) {
            return;
        }

        // A President already exists: do nothing.
        if (membershipRepository.existsByRoles_Name("PRESIDENT")) {
            return;
        }

        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalStateException(
                        "Bootstrap President account not found: " + email
                ));

        if (user.getAccountStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Bootstrap President account must be ACTIVE."
            );
        }

        Role presidentRole = roleRepository.findByNameIgnoreCase("PRESIDENT")
                .orElseThrow(() -> new IllegalStateException(
                        "PRESIDENT role does not exist. Check V2 migration."
                ));

        Membership membership = membershipRepository
                .findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Membership newMembership = new Membership();
                    newMembership.setUser(user);
                    return newMembership;
                });

        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException(
                    "The configured user's membership is not ACTIVE. "
                            + "Reactivate it manually before bootstrapping."
            );
        }

        membership.addRole(presidentRole);

        membershipRepository.save(membership);

        System.out.println(
                "Initial PRESIDENT role assigned to: " + user.getEmail()
        );
    }
}