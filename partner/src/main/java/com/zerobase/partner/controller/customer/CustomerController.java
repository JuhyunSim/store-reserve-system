package com.zerobase.partner.controller.customer;

import com.zerobase.partner.domain.dto.StoreDto;
import com.zerobase.partner.service.customer.CustomerSearchService;
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
public class CustomerController {

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

//    @GetMapping("/details")
//    public ResponseEntity<?> getDetails(@RequestParam Long partnerId,
//                                        @RequestParam String name
//    ) {
//        return null;
//    }


}
