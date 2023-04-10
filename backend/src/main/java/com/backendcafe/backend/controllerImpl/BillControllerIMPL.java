package com.backendcafe.backend.controllerImpl;

import com.backendcafe.backend.entity.Bill;
import com.backendcafe.backend.exception.BaseException;
import com.backendcafe.backend.models.JsonModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequestMapping(path = "/bill")
public interface BillControllerIMPL {

    @PostMapping("/generateReport")
    public ResponseEntity<JsonModel> generateReport(@RequestBody(required = true) Map<String, Object> body);

    @GetMapping("/getBills")
    public ResponseEntity<List<Bill>> getBills();

    @PostMapping("/getPdf")
    public ResponseEntity<byte[]> getPdf(@RequestBody Map<String,Object> body) throws BaseException;

    @DeleteMapping("/delete/{billId}")
    public ResponseEntity<JsonModel> deleteById(@PathVariable(required = true) Integer billId);
}
