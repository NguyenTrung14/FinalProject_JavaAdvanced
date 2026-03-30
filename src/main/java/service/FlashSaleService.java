package service;

import model.FlashSale;

import java.util.List;

import dao.impl.FlashSaleDAO;

public class FlashSaleService {
    private final FlashSaleDAO flashSaleDAO = new FlashSaleDAO();

    public boolean addFlashSale(FlashSale flashSale) {
        if (flashSale.getProductId() <= 0) {
            return false;
        }
        if (flashSale.getDiscountPercent() <= 0 || flashSale.getDiscountPercent() > 100) {
            return false;
        }
        if (flashSale.getMaxQuantity() <= 0) {
            return false;
        }
        if (flashSale.getStartTime() == null || flashSale.getStartTime().trim().isEmpty()) {
            return false;
        }
        if (flashSale.getEndTime() == null || flashSale.getEndTime().trim().isEmpty()) {
            return false;
        }
        if (flashSale.getStatus() == null || flashSale.getStatus().trim().isEmpty()) {
            flashSale.setStatus("ACTIVE");
        }

        return flashSaleDAO.insert(flashSale);
    }

    public List<FlashSale> getAllFlashSales() {
        return flashSaleDAO.findAll();
    }
}