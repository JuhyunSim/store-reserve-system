package com.zerobase.reserve.controller;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.reserve.service.CustomerSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final CustomerSearchService customerSearchService;

    @GetMapping
    public ResponseEntity<List<StoreDto>> search(@RequestParam String keyWord) {
        return ResponseEntity.ok(customerSearchService.searchStore(keyWord));
    }

    @GetMapping("/search/autocomplete")
    public ResponseEntity<?> getAutoComplete(@RequestParam String keyWord) {
        List<String> keywords =
                customerSearchService.getAutoCompleteKeywords(keyWord);

        return ResponseEntity.ok(keywords);
    }

    @GetMapping("/details")
    public ResponseEntity<?> getDetails(@RequestParam Long storeId) {
        return ResponseEntity.ok(customerSearchService.getStoreDetail(storeId));
    }
}
