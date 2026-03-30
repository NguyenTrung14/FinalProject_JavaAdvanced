package service;

import model.Coupon;

import java.util.List;

import dao.impl.CouponDAO;

public class CouponService {
    private final CouponDAO couponDAO = new CouponDAO();

    public boolean addCoupon(Coupon coupon) {
        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) {
            return false;
        }
        if (coupon.getDiscountPercent() <= 0 || coupon.getDiscountPercent() > 100) {
            return false;
        }
        if (coupon.getQuantity() < 0) {
            return false;
        }
        if (coupon.getMinOrderAmount() < 0) {
            return false;
        }
        if (coupon.getStartTime() == null || coupon.getStartTime().trim().isEmpty()) {
            return false;
        }
        if (coupon.getEndTime() == null || coupon.getEndTime().trim().isEmpty()) {
            return false;
        }

        coupon.setCode(coupon.getCode().trim().toUpperCase());

        if (coupon.getStatus() == null || coupon.getStatus().trim().isEmpty()) {
            coupon.setStatus("ACTIVE");
        }

        return couponDAO.insert(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponDAO.findAll();
    }
}