package com.core2web.controller;

import com.core2web.dao.AdminDao;
import com.core2web.model.Admin;

public class AdminController {

    private final AdminDao adminDao = new AdminDao();

    public Admin getCurrentAdmin() {
        return adminDao.getCurrentAdmin();
    }

    public boolean updatePhone(String phone) {
        return adminDao.updatePhone(phone);
    }

    public boolean updateProfileImageUrl(String imageUrl) {
        return adminDao.updateProfileImageUrl(imageUrl);
    }
}
