package org.cunoc.pdfpedia.service.monetary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cunoc.pdfpedia.domain.dto.monetary.WalletDto;
import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.repository.monetary.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletDto toDto(WalletEntity walletEntity) {
        return new WalletDto(walletEntity.getId(), walletEntity.getBalance());
    }

    @Transactional(readOnly = true)
    public WalletDto findUserById(Long userId) {
        WalletEntity walletExist = walletRepository.findAllByUserId(userId)
                .orElseThrow(() -> new ValueNotFoundException("Cartera Digital no encontrada"));

        return toDto(walletExist);
    }

    @Transactional
    public WalletDto updateIncrease(Long id, @Valid WalletDto dto) {
        WalletEntity walletExist = walletRepository.findById(id)
                .orElseThrow(() -> new ValueNotFoundException("Cartera Digital no encontrada"));

        walletExist.setBalance(walletExist.getBalance().add(dto.balance()));

        WalletEntity updatedWallet = walletRepository.save(walletExist);
        return this.toDto(updatedWallet);
    }

    @Transactional
    public void updateDecrease(Long userId, BigDecimal balance) {
        WalletEntity walletExist = walletRepository.findAllByUserId(userId)
                .orElseThrow(() -> new ValueNotFoundException("Cartera Digital no encontrada"));

        BigDecimal balanceTotal = walletExist.getBalance().subtract(balance);

        if (balanceTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Saldo insuficiente para realizar la transacción");
        }

        walletExist.setBalance(balanceTotal);
        walletRepository.save(walletExist);
    }


}
