package org.cunoc.pdfpedia.controller.monetary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.announcer.ChargePeriodAdDto;
import org.cunoc.pdfpedia.domain.dto.monetary.WalletDto;
import org.cunoc.pdfpedia.service.monetary.WalletService;
import org.cunoc.pdfpedia.util.annotation.CurrentUserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/byUser")
    public ResponseEntity<WalletDto> getWalletByUserId(@CurrentUserId long userId) {
        return ResponseEntity.ok(this.walletService.findUserById(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletDto> update(@PathVariable Long id, @Valid @RequestBody WalletDto dto) {
        return ResponseEntity.ok(this.walletService.update(id, dto));
    }



}
