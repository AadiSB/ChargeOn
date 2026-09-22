package com.core2web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.core2web.dao.OwnerDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Owner;

public class OwnerController {

    private final OwnerDao ownerDao = new OwnerDao();

    public Owner getCurrentOwner() {
        return ownerDao.getCurrentOwner();
    }

    public List<Owner> getAllOwners() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return ownerDao.getAllOwners(session.getIdToken());
    }

    /**
     * One owner by uid — a single document read, not a scan of every customer.
     * Null if unknown or not readable by this session.
     */
    public Owner getOwner(String uid) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        return ownerDao.getOwner(uid, session.getIdToken());
    }

    /**
     * Display names for a set of owner ids, as {@code ownerId -> name}.
     *
     * <p>One document read per <em>distinct</em> id. Deliberately not
     * {@link #getAllOwners()}: drivers are not allowed to enumerate customers, and
     * this has to work for the driver's own booking list as well as for admins.
     * Ids that cannot be read are simply absent from the map, so callers fall back
     * to whatever placeholder they prefer.
     */
    public Map<String, String> getOwnerNames(Collection<String> ownerIds) {
        Map<String, String> names = new HashMap<>();
        if (ownerIds == null) {
            return names;
        }

        for (String uid : new HashSet<>(ownerIds)) {
            if (uid == null || uid.isEmpty() || "null".equals(uid) || names.containsKey(uid)) {
                continue;
            }
            Owner owner = getOwner(uid);
            if (owner != null && owner.getName() != null && !owner.getName().isEmpty()) {
                names.put(uid, owner.getName());
            }
        }
        return names;
    }

    /** Name for one owner id, or {@code fallback} when unknown / unreadable. */
    public static String nameOr(Map<String, String> names, String ownerId, String fallback) {
        if (ownerId == null || ownerId.isEmpty() || "null".equals(ownerId)) {
            return fallback;
        }
        String name = names.get(ownerId);
        return name == null || name.isEmpty() ? fallback : name;
    }

    public boolean updatePhone(String phone) {
        return ownerDao.updatePhone(phone);
    }

    public boolean updateProfileImageUrl(String imageUrl) {
        return ownerDao.updateProfileImageUrl(imageUrl);
    }

    public boolean updateSubscription(String planId, String planName) {
        return ownerDao.updateSubscription(planId, planName);
    }
}
