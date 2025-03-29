package org.cunoc.pdfpedia.service.monetary;

import jakarta.validation.Valid;
import org.cunoc.pdfpedia.domain.dto.monetary.WalletDto;
import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;

import java.math.BigDecimal;

public interface IWalletService {

    WalletDto toDto(WalletEntity walletEntity);

    WalletDto findUserById(Long userId);

    WalletDto updateIncrease(Long id, @Valid WalletDto dto);

    void updateDecrease(Long userId, BigDecimal balance);
}
