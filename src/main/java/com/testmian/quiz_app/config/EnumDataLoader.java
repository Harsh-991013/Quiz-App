package com.testmian.quiz_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.testmian.quiz_app.entity.*;
import com.testmian.quiz_app.repository.*;

@Component
public class EnumDataLoader implements CommandLineRunner {

    @Autowired
    private AuthMethodRepository authMethodRepo;

    @Autowired
    private InviteStatusRepository inviteStatusRepo;

    @Autowired
    private UserStatusRepository userStatusRepo;

    @Autowired
    private MagicLinkPurposeRepository magicLinkPurposeRepo;

    @Autowired
    private DeliveryStatusRepository deliveryStatusRepo;

    @Override
    public void run(String... args) throws Exception {

        // ✅ Auth Methods
        if(authMethodRepo.count() == 0) {
            authMethodRepo.save(new AuthMethod("PASSWORD", "Password"));
            authMethodRepo.save(new AuthMethod("MAGIC_LINK", "Magic Link"));
            authMethodRepo.save(new AuthMethod("OAUTH", "OAuth"));
        }

        // ✅ Invite Status
        if(inviteStatusRepo.count() == 0) {
            inviteStatusRepo.save(new InviteStatus("PENDING", "Pending"));
            inviteStatusRepo.save(new InviteStatus("ACTIVATED", "Activated"));
            inviteStatusRepo.save(new InviteStatus("EXPIRED", "Expired"));
        }

        // ✅ User Status
        if(userStatusRepo.count() == 0) {
            userStatusRepo.save(new UserStatus("ACTIVE", "Active"));
            userStatusRepo.save(new UserStatus("INACTIVE", "Inactive"));
        }

        // ✅ Magic Link Purpose
        if(magicLinkPurposeRepo.count() == 0) {
            magicLinkPurposeRepo.save(new MagicLinkPurpose("LOGIN", "Login"));
            magicLinkPurposeRepo.save(new MagicLinkPurpose("RESET_PASSWORD", "Reset Password"));
            magicLinkPurposeRepo.save(new MagicLinkPurpose("INVITE", "Invite"));
            magicLinkPurposeRepo.save(new MagicLinkPurpose("ACTIVATE_ADMIN", "Activate Admin"));
            magicLinkPurposeRepo.save(new MagicLinkPurpose("QUIZ_ACCESS", "Quiz Access"));
        }

        // ✅ Delivery Status
        if(deliveryStatusRepo.count() == 0) {
            deliveryStatusRepo.save(new DeliveryStatus("SENT", "Sent"));
            deliveryStatusRepo.save(new DeliveryStatus("FAILED", "Failed"));
        }

        System.out.println("✅ EnumDataLoader: All master/enum tables seeded successfully.");
    }
}
