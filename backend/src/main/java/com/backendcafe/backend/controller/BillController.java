package com.backendcafe.backend.controller;

import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.controllerImpl.BillControllerIMPL;
import com.backendcafe.backend.entity.Bill;
import com.backendcafe.backend.exception.BaseException;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.service.BillService;
import com.backendcafe.backend.untils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class BillController implements BillControllerIMPL {
    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @Override
    public ResponseEntity<JsonModel> generateReport(Map<String, Object> body) {
        log.info("Inside generate controller");
        try {
            return billService.generateReport(body);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {
            return billService.getBills();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> body) throws BaseException {
        try {
            return billService.getPdf(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new BaseException(CafeConstants.DEFAULT_ERROR);
    }

    @Override
    public ResponseEntity<JsonModel> deleteById(Integer billId) {
        try {
            return  billService.deleteById(billId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }
}
