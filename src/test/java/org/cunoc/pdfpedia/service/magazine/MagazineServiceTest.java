package org.cunoc.pdfpedia.service.magazine;

import static org.assertj.core.api.BDDAssertions.catchRuntimeException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.cunoc.pdfpedia.util.ThenMockAlias.thenMock;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.cunoc.pdfpedia.domain.dto.magazine.AddMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineCounts;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MagazineEditorPreviewDto;
import org.cunoc.pdfpedia.domain.dto.magazine.MinimalMagazineDto;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineBlockDto;
import org.cunoc.pdfpedia.domain.dto.magazine.UpdateMagazineDto;
import org.cunoc.pdfpedia.domain.entity.ConfigurationEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.CategoryEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.MagazineEntity;
import org.cunoc.pdfpedia.domain.entity.magazine.TagEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.PaymentEntity;
import org.cunoc.pdfpedia.domain.entity.monetary.WalletEntity;
import org.cunoc.pdfpedia.domain.entity.user.UserEntity;
import org.cunoc.pdfpedia.domain.exception.BadRequestException;
import org.cunoc.pdfpedia.domain.exception.ValueNotFoundException;
import org.cunoc.pdfpedia.domain.type.PaymentType;
import org.cunoc.pdfpedia.repository.ConfigurationRepository;
import org.cunoc.pdfpedia.repository.interaction.SubscriptionRepository;
import org.cunoc.pdfpedia.repository.magazine.CategoryRepository;
import org.cunoc.pdfpedia.repository.magazine.MagazineRepository;
import org.cunoc.pdfpedia.repository.magazine.TagRepository;
import org.cunoc.pdfpedia.repository.monetary.PaymentRepository;
import org.cunoc.pdfpedia.repository.monetary.WalletRepository;
import org.cunoc.pdfpedia.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MagazineServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ConfigurationRepository configurationRepository;

    @InjectMocks
    private MagazineService magazineService;

    @Test
    void canFindEditorMagazine() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        MagazineDto expectedMagazine = MagazineDto.builder()
                .id(magazineId)
                .title("amazing title")
                .description("amazing description")
                .adBlockingExpirationDate(LocalDate.now().plusDays(15))
                .disableLikes(true)
                .disableComments(true)
                .disableSuscriptions(true)
                .categoryId(450L)
                .tagIds("123,941")
                .build();

        MagazineDto magazine = expectedMagazine.toBuilder().build();

        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId))
                .willReturn(Optional.of(magazine));

        // when
        MagazineDto actualMagazine = magazineService.findEditorMagazine(editorId, magazineId);

        // then
        then(actualMagazine)
                .usingRecursiveComparison()
                .isEqualTo(magazine);
    }

    @Test
    void cantFindEditorMagazineIfMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId))
                .willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.findEditorMagazine(editorId, magazineId));

        // then
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void canFindMinimalEditorMagazines() {
        // given
        long editorId = 501L;

        List<MinimalMagazineDto> expectedMagazines = List.of(
                MinimalMagazineDto.builder()
                        .id(150L)
                        .title("amazing title")
                        .build(),
                MinimalMagazineDto.builder()
                        .id(151L)
                        .title("amazing title 2")
                        .build());

        List<MinimalMagazineDto> magazines = expectedMagazines.stream()
                .map(magazine -> magazine.toBuilder().build())
                .toList();

        given(magazineRepository.findAllSimpleByEditorIdAndIsDeletedFalseOrderById(editorId, MinimalMagazineDto.class))
                .willReturn(magazines);

        // when
        List<MinimalMagazineDto> actualMagazines = magazineService.findMinimalEditorMagazines(editorId);

        // then
        then(actualMagazines)
                .usingRecursiveComparison()
                .isEqualTo(magazines);
    }

    @Test
    void canFindEditorMagazines() {
        // given
        long editorId = 501L;

        List<MagazineEditorPreviewDto> expectedMagazines = List.of(
                MagazineEditorPreviewDto.builder()
                        .id(150L)
                        .title("amazing title")
                        .description("amazing description")
                        .costPerDay(BigDecimal.valueOf(100))
                        .categoryName("category name")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .counts(MagazineCounts.builder()
                                .issuesCount(100L)
                                .subscriptionsCount(10L)
                                .likesCount(10L)
                                .commentsCount(10L)
                                .tagNames("tag1,tag2")
                                .build())
                        .build(),
                MagazineEditorPreviewDto.builder()
                        .id(151L)
                        .title("amazing title 2")
                        .description("amazing description 2")
                        .costPerDay(BigDecimal.valueOf(200))
                        .categoryName("category name 2")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .counts(MagazineCounts.builder()
                                .issuesCount(200L)
                                .subscriptionsCount(20L)
                                .likesCount(20L)
                                .commentsCount(20L)
                                .tagNames("tag3,tag4")
                                .build())
                        .build());

        List<MagazineEditorPreviewDto> magazines = expectedMagazines.stream()
                .map(magazine -> magazine.toBuilder()
                        .counts(magazine.counts()
                                .toBuilder()
                                .build())
                        .build())
                .toList();

        given(magazineRepository.findAllByEditorIdAndIsDeletedFalse(editorId))
                .willReturn(magazines);

        // when
        List<MagazineEditorPreviewDto> actualMagazines = magazineService.findEditorMagazines(editorId);

        // then
        then(actualMagazines)
                .usingRecursiveComparison()
                .isEqualTo(magazines);
    }

    @Test
    void canSaveMagazine() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);
        LocalDate createdAt = LocalDate.now();

        UserEntity editor = mock(UserEntity.class);
        CategoryEntity category = mock(CategoryEntity.class);
        Set<TagEntity> tags = Set.of(mock(TagEntity.class), mock(TagEntity.class));

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .category(category)
                .tags(tags)
                .editor(editor)
                .createdAt(createdAt.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        given(tagRepository.existsAllByIdIn(tagIds)).willReturn(true);

        given(userRepository.findById(editorId)).willReturn(Optional.of(editor));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(tagRepository.findAllByIdIn(tagIds)).willReturn(tags);

        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title(expectedMagazine.getTitle())
                .description(expectedMagazine.getDescription())
                .adBlockingExpirationDate(expectedMagazine.getAdBlockingExpirationDate())
                .disableLikes(expectedMagazine.isDisableLikes())
                .disableComments(expectedMagazine.isDisableComments())
                .disableSuscriptions(expectedMagazine.isDisableSuscriptions())
                .createdAt(createdAt)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .createdAt(createdAt)
                .build();

        // when
        magazineService.saveMagazine(editorId, inMagazine);

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(categoryRepository).should().existsById(categoryId);
        thenMock(tagRepository).should().existsAllByIdIn(refEq(tagIds));
        thenMock(magazineRepository).should().save(refEq(expectedMagazine));
    }

    @Test
    void cantSaveMagazineIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        given(userRepository.existsById(editorId)).willReturn(false);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.saveMagazine(editorId, inMagazine));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantSaveMagazineIfCategoryDoesntExist() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(categoryRepository.existsById(categoryId)).willReturn(false);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.saveMagazine(editorId, inMagazine));

        // then
        thenMock(categoryRepository).should().existsById(categoryId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantSaveMagazineIfTagsDoesntExist() {
        // given
        long editorId = 501L;
        long categoryId = 150L;
        List<Long> tagIds = List.of(10L, 40L);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(categoryRepository.existsById(categoryId)).willReturn(true);
        given(tagRepository.existsAllByIdIn(tagIds)).willReturn(false);

        AddMagazineDto inMagazine = AddMagazineDto.builder()
                .title("title")
                .description("description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .categoryId(categoryId)
                .tagIds(tagIds)
                .build();

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.saveMagazine(editorId, inMagazine));

        // then
        thenMock(tagRepository).should().existsAllByIdIn(refEq(tagIds));
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void canUpdateMagazine() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .id(magazineId)
                .title("amazing title modified")
                .description("amazing description modified")
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .category(mock(CategoryEntity.class))
                .tags(Set.of(mock(TagEntity.class), mock(TagEntity.class)))
                .editor(mock(UserEntity.class))
                .build();

        UpdateMagazineDto magazineIn = UpdateMagazineDto.builder()
                .title(Optional.of(expectedMagazine.getTitle()))
                .description(Optional.of(expectedMagazine.getDescription()))
                .disableLikes(Optional.of(expectedMagazine.isDisableLikes()))
                .disableComments(Optional.of(expectedMagazine.isDisableComments()))
                .disableSuscriptions(Optional.of(expectedMagazine.isDisableSuscriptions()))
                .categoryId(Optional.empty())
                .tagIds(Optional.empty())
                .build();

        MagazineEntity magazineEntity = expectedMagazine.toBuilder()
                .title("amazing title not modified")
                .description("amazing description not modified")
                .disableLikes(true)
                .disableComments(true)
                .disableSuscriptions(true)
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazineEntity));

        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        // when
        MinimalMagazineDto actualMagazine = magazineService.updateMagazine(editorId, magazineId, magazineIn);

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().save(refEq(expectedMagazine));
        then(actualMagazine)
                .usingRecursiveComparison()
                .isEqualTo(expectedMagazine);
    }

    @Test
    void cantUpdateMagazineIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        UpdateMagazineDto magazineIn = UpdateMagazineDto.builder()
                .title(Optional.of("amazing title modified"))
                .description(Optional.empty())
                .disableLikes(Optional.empty())
                .disableComments(Optional.empty())
                .disableSuscriptions(Optional.empty())
                .categoryId(Optional.empty())
                .tagIds(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazine(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineIfMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        UpdateMagazineDto magazineIn = UpdateMagazineDto.builder()
                .title(Optional.of("amazing title modified"))
                .description(Optional.empty())
                .disableLikes(Optional.empty())
                .disableComments(Optional.empty())
                .disableSuscriptions(Optional.empty())
                .categoryId(Optional.empty())
                .tagIds(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazine(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        UpdateMagazineDto magazineIn = UpdateMagazineDto.builder()
                .title(Optional.of("amazing title modified"))
                .description(Optional.empty())
                .disableLikes(Optional.empty())
                .disableComments(Optional.empty())
                .disableSuscriptions(Optional.empty())
                .categoryId(Optional.empty())
                .tagIds(Optional.empty())
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazine(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void canUpdateMagazineAds() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        LocalDate adBlock = LocalDate.now().plusDays(10);
        UserEntity editor = mock(UserEntity.class);

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .id(magazineId)
                .title("amazing title not modified")
                .description("amazing description not modified")
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .adBlockingExpirationDate(LocalDate.from(adBlock))
                .category(mock(CategoryEntity.class))
                .tags(Set.of(mock(TagEntity.class), mock(TagEntity.class)))
                .editor(editor)
                .build();

        WalletEntity expectedWallet = WalletEntity.builder()
                .user(editor)
                .balance(BigDecimal.valueOf(150))
                .build();

        PaymentEntity expectedPayment = PaymentEntity.builder()
                .magazine(expectedMagazine)
                .paymentType(PaymentType.BLOCK_ADS)
                .amount(BigDecimal.valueOf(100))
                .build();

        MagazineEntity magazine = expectedMagazine.withAdBlockingExpirationDate(null);
        WalletEntity wallet = expectedWallet.withBalance(BigDecimal.valueOf(250));
        ConfigurationEntity configuration = new ConfigurationEntity(BigDecimal.valueOf(10), BigDecimal.valueOf(100));

        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.from(adBlock));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazine));

        given(walletRepository.findAllByUserId(editorId)).willReturn(Optional.of(wallet));
        given(configurationRepository.findById(1L)).willReturn(Optional.of(configuration.toBuilder().build()));
        given(paymentRepository.save(refEq(expectedPayment))).willReturn(expectedPayment);
        given(walletRepository.save(refEq(expectedWallet))).willReturn(expectedWallet);
        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        // when
        MinimalMagazineDto actualMagazine = magazineService.updateMagazineAds(editorId, magazineId, magazineIn);

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().save(refEq(expectedMagazine));
        thenMock(walletRepository).should().save(refEq(expectedWallet));
        thenMock(paymentRepository).should().save(refEq(expectedPayment));
        then(actualMagazine)
                .usingRecursiveComparison()
                .isEqualTo(expectedMagazine);
    }

    @Test
    void canUpdateMagazineAdsIfBlockingDateIsNotNull() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        LocalDate adBlock = LocalDate.now().plusDays(10);
        UserEntity editor = mock(UserEntity.class);

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .id(magazineId)
                .title("amazing title not modified")
                .description("amazing description not modified")
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .adBlockingExpirationDate(LocalDate.from(adBlock))
                .category(mock(CategoryEntity.class))
                .tags(Set.of(mock(TagEntity.class), mock(TagEntity.class)))
                .editor(editor)
                .build();

        WalletEntity expectedWallet = WalletEntity.builder()
                .user(editor)
                .balance(BigDecimal.valueOf(200))
                .build();

        PaymentEntity expectedPayment = PaymentEntity.builder()
                .magazine(expectedMagazine)
                .paymentType(PaymentType.BLOCK_ADS)
                .amount(BigDecimal.valueOf(50))
                .build();

        MagazineEntity magazine = expectedMagazine.withAdBlockingExpirationDate(LocalDate.now().plusDays(5));
        WalletEntity wallet = expectedWallet.withBalance(BigDecimal.valueOf(250));
        ConfigurationEntity configuration = new ConfigurationEntity(BigDecimal.valueOf(10), BigDecimal.valueOf(100));

        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.from(adBlock));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazine));

        given(walletRepository.findAllByUserId(editorId)).willReturn(Optional.of(wallet));
        given(configurationRepository.findById(1L)).willReturn(Optional.of(configuration.toBuilder().build()));
        given(paymentRepository.save(refEq(expectedPayment))).willReturn(expectedPayment);
        given(walletRepository.save(refEq(expectedWallet))).willReturn(expectedWallet);
        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        // when
        MinimalMagazineDto actualMagazine = magazineService.updateMagazineAds(editorId, magazineId, magazineIn);

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().save(refEq(expectedMagazine));
        thenMock(walletRepository).should().save(refEq(expectedWallet));
        thenMock(paymentRepository).should().save(refEq(expectedPayment));
        then(actualMagazine)
                .usingRecursiveComparison()
                .isEqualTo(expectedMagazine);
    }

    @Test
    void canUpdateMagazineAdsIfCurrentBlockingDateIsPreviousCurrentDate() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        LocalDate adBlock = LocalDate.now().plusDays(10);
        UserEntity editor = mock(UserEntity.class);

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .id(magazineId)
                .title("amazing title not modified")
                .description("amazing description not modified")
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .adBlockingExpirationDate(LocalDate.from(adBlock))
                .category(mock(CategoryEntity.class))
                .tags(Set.of(mock(TagEntity.class), mock(TagEntity.class)))
                .editor(editor)
                .build();

        WalletEntity expectedWallet = WalletEntity.builder()
                .user(editor)
                .balance(BigDecimal.valueOf(150))
                .build();

        PaymentEntity expectedPayment = PaymentEntity.builder()
                .magazine(expectedMagazine)
                .paymentType(PaymentType.BLOCK_ADS)
                .amount(BigDecimal.valueOf(100))
                .build();

        MagazineEntity magazine = expectedMagazine.withAdBlockingExpirationDate(LocalDate.now().minusDays(5));
        WalletEntity wallet = expectedWallet.withBalance(BigDecimal.valueOf(250));
        ConfigurationEntity configuration = new ConfigurationEntity(BigDecimal.valueOf(10), BigDecimal.valueOf(100));

        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.from(adBlock));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazine));

        given(walletRepository.findAllByUserId(editorId)).willReturn(Optional.of(wallet));
        given(configurationRepository.findById(1L)).willReturn(Optional.of(configuration.toBuilder().build()));
        given(paymentRepository.save(refEq(expectedPayment))).willReturn(expectedPayment);
        given(walletRepository.save(refEq(expectedWallet))).willReturn(expectedWallet);
        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        // when
        MinimalMagazineDto actualMagazine = magazineService.updateMagazineAds(editorId, magazineId, magazineIn);

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().save(refEq(expectedMagazine));
        thenMock(walletRepository).should().save(refEq(expectedWallet));
        thenMock(paymentRepository).should().save(refEq(expectedPayment));
        then(actualMagazine)
                .usingRecursiveComparison()
                .isEqualTo(expectedMagazine);
    }

    @Test
    void cantUpdateMagazineAdsIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.now().plusDays(10));

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.now().plusDays(10));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfBlockingDateIsBeforeCurrentDate() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        LocalDate blockingDateToUpdate = LocalDate.now().minusDays(1);
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(blockingDateToUpdate);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfEditorMagazineDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.now().plusDays(10));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfBlockingDateIsBeforeCurrentBlockingDate() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        LocalDate blockingDateToUpdate = LocalDate.now().plusDays(2);
        LocalDate currentBlockingDate = blockingDateToUpdate.plusDays(1);

        MagazineEntity magazine = MagazineEntity.builder()
                .id(magazineId)
                .title("amazing title not modified")
                .description("amazing description not modified")
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .adBlockingExpirationDate(currentBlockingDate)
                .category(mock(CategoryEntity.class))
                .tags(Set.of(mock(TagEntity.class), mock(TagEntity.class)))
                .editor(mock(UserEntity.class))
                .build();

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazine));

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId,
                        new UpdateMagazineBlockDto(blockingDateToUpdate)));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId,
                MagazineEntity.class);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfWalletDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.now().plusDays(10));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(mock(MagazineEntity.class)));

        given(walletRepository.findAllByUserId(editorId)).willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId,
                MagazineEntity.class);
        thenMock(walletRepository).should().findAllByUserId(editorId);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfConfigurationDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.now().plusDays(10));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(mock(MagazineEntity.class)));

        given(walletRepository.findAllByUserId(editorId)).willReturn(Optional.of(mock(WalletEntity.class)));
        given(configurationRepository.findById(1L)).willReturn(Optional.empty());

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId,
                MagazineEntity.class);
        thenMock(walletRepository).should().findAllByUserId(editorId);
        thenMock(configurationRepository).should().findById(1L);
        then(actualException).isInstanceOf(ValueNotFoundException.class);
    }

    @Test
    void cantUpdateMagazineAdsIfWaletBalanceIsNotEnough() {
        // given
        long editorId = 501L;
        long magazineId = 150L;
        UpdateMagazineBlockDto magazineIn = new UpdateMagazineBlockDto(LocalDate.now().plusDays(10));

        WalletEntity wallet = WalletEntity.builder()
                .user(mock(UserEntity.class))
                .balance(BigDecimal.valueOf(50))
                .build();

        ConfigurationEntity configuration = new ConfigurationEntity(BigDecimal.valueOf(10), BigDecimal.valueOf(100));

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.existsById(magazineId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(mock(MagazineEntity.class)));

        given(walletRepository.findAllByUserId(editorId)).willReturn(Optional.of(wallet));
        given(configurationRepository.findById(1L)).willReturn(Optional.of(configuration.toBuilder().build()));

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.updateMagazineAds(editorId, magazineId, magazineIn));

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().existsById(magazineId);
        thenMock(magazineRepository).should().findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId,
                MagazineEntity.class);
        thenMock(walletRepository).should().findAllByUserId(editorId);
        thenMock(configurationRepository).should().findById(1L);
        then(actualException).isInstanceOf(BadRequestException.class);
    }

    @Test
    void canDeleteMagazine() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        MagazineEntity expectedMagazine = MagazineEntity.builder()
                .id(magazineId)
                .title("amazing title")
                .description("amazing description")
                .adBlockingExpirationDate(null)
                .disableLikes(false)
                .disableComments(false)
                .disableSuscriptions(false)
                .category(mock(CategoryEntity.class))
                .tags(Set.of(mock(TagEntity.class), mock(TagEntity.class)))
                .editor(mock(UserEntity.class))
                .isDeleted(true)
                .build();

        MagazineEntity magazine = expectedMagazine.withDeleted(false);

        given(userRepository.existsById(editorId)).willReturn(true);
        given(magazineRepository.findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId, MagazineEntity.class))
                .willReturn(Optional.of(magazine));

        given(magazineRepository.save(refEq(expectedMagazine))).willReturn(expectedMagazine);

        // when
        magazineService.deleteMagazine(editorId, magazineId);

        // then
        thenMock(userRepository).should().existsById(editorId);
        thenMock(magazineRepository).should().findByIdAndEditorIdAndIsDeletedFalse(magazineId, editorId,
                MagazineEntity.class);
    }

    @Test
    void cantDeleteMagazineIfUserDoesntExist() {
        // given
        long editorId = 501L;
        long magazineId = 150L;

        given(userRepository.existsById(editorId)).willReturn(false);

        // when
        RuntimeException actualException = catchRuntimeException(
                () -> magazineService.deleteMagazine(editorId, magazineId));

        // then
        thenMock(userRepository).should().existsById(editorId);
        then(actualException).isInstanceOf(BadRequestException.class);
    }
}
