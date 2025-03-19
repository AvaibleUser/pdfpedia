package org.cunoc.pdfpedia.service.monetary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.monetary.WalletDto;
import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.monetary.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    private WalletDto toDto(WalletEntity walletEntity) {
        return new WalletDto(walletEntity.getId(), walletEntity.getBalance());
    }

    @Transactional(readOnly = true)
    public WalletDto findUserById(Long userId) {
        WalletEntity walletExist = walletRepository.findAllByUserId(userId)
                .orElseThrow(() -> new ValueNotFoundException("Cartera Digital no encontrada"));

        return toDto(walletExist);
    }

    @Transactional
    public WalletDto update(Long id, @Valid WalletDto dto) {
        WalletEntity walletExist = walletRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Cartera Digital no encontrada"));

        walletExist.setBalance(walletExist.getBalance().add(dto.balance()));

        WalletEntity updatedWallet = walletRepository.save(walletExist);
        return this.toDto(updatedWallet);
    }


}
