package co.za.vendingmachineapi.service;

import co.za.vendingmachineapi.entity.Cash;
import co.za.vendingmachineapi.repository.CashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock
    private CashRepository cashRepository;

    @InjectMocks
    private CashService cashService;

    private Cash cash2;
    private Cash cash3;
    private Cash cash5;

    @BeforeEach
    void setUp() {
        cash2 = new Cash();
        cash2.setDenomination(2);
        cash2.setQuantity(10);

        cash3 = new Cash();
        cash3.setDenomination(3);
        cash3.setQuantity(10);

        cash5 = new Cash();
        cash5.setDenomination(5);
        cash5.setQuantity(10);
    }

    @Test
    void dispenseChange_shouldDeductAndPersistAnyValidCombinationForSum() {
        when(cashRepository.findAll()).thenReturn(Arrays.asList(cash2, cash3, cash5));

        when(cashRepository.findDistinctByDenomination(2)).thenAnswer(inv -> cloneCash(cash2));

        ArgumentCaptor<Cash> saveCaptor = ArgumentCaptor.forClass(Cash.class);
        when(cashRepository.save(saveCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        int sum = 8;

        cashService.dispenseChange(sum);

        List<Cash> saved = saveCaptor.getAllValues();
        assertThat(saved).isNotEmpty();

        Map<Integer, Integer> original = Map.of(
                2, 10,
                3, 10,
                5, 10
        );
        Map<Integer, Integer> lastSavedQtyByDenom = saved.stream()
                .collect(Collectors.toMap(Cash::getDenomination, Cash::getQuantity, (a, b) -> b)); // keep last

        int totalValueDeducted = lastSavedQtyByDenom.entrySet().stream()
                .mapToInt(e -> {
                    int denom = e.getKey();
                    int newQty = e.getValue();
                    int oldQty = original.getOrDefault(denom, 0);
                    int deducted = Math.max(0, oldQty - newQty);
                    return deducted * denom;
                })
                .sum();

        assertThat(totalValueDeducted).isEqualTo(sum);
        assertThat(saved).allSatisfy(c ->
                assertThat(c.getDenomination()).isIn(2, 3, 5)
        );
        assertThat(saved).allSatisfy(c ->
                assertThat(c.getQuantity()).isLessThanOrEqualTo(10)
        );

        verify(cashRepository, atLeastOnce()).findAll();
        verify(cashRepository, atLeastOnce()).save(any(Cash.class));
        verify(cashRepository, atLeastOnce()).findDistinctByDenomination(anyInt());
        verifyNoMoreInteractions(cashRepository);
    }

    @Test
    void addPaymentCash_shouldIncreaseQuantityByMatchingCoinsAndPersist() {
        // Arrange
        Cash fiveRand = new Cash();
        fiveRand.setDenomination(5);
        fiveRand.setQuantity(2);

        List<Integer> payment = Arrays.asList(5, 1, 5, 10); // two coins match denom 5

        cashService.addPaymentCash(fiveRand, payment);

        assertThat(fiveRand.getQuantity()).isEqualTo(4);
        verify(cashRepository).save(fiveRand);
        verifyNoMoreInteractions(cashRepository);
    }

    @Test
    void getCashByDenomination_shouldDelegateToRepository() {
        Cash expected = new Cash();
        expected.setDenomination(10);
        expected.setQuantity(7);
        when(cashRepository.findDistinctByDenomination(10)).thenReturn(expected);

        Cash actual = cashService.getCashByDenomination(10);

        assertThat(actual).isSameAs(expected);
        verify(cashRepository).findDistinctByDenomination(10);
        verifyNoMoreInteractions(cashRepository);
    }

    private static Cash cloneCash(Cash original) {
        Cash c = new Cash();
        c.setDenomination(original.getDenomination());
        c.setQuantity(original.getQuantity());
        return c;
    }
}