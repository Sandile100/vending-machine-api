package co.za.vendingmachineapi.service;

import co.za.vendingmachineapi.entity.Cash;
import co.za.vendingmachineapi.exception.NoChangeException;
import co.za.vendingmachineapi.repository.CashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CashService {

    /*
     * Calculate possible combinations of calculating change based on available coins.
     * If Possible Combination array is empty then there is no change available on Petty cash or available cash
     */
    private final CashRepository cashRepository;

    public CashService(CashRepository cashRepository) {
        this.cashRepository = cashRepository;
    }

    public void dispenseChange(final int sum) {
        List<Map<Object, Long>> validCombination = calculateChange(sum);
        Map<Object, Long> anyCombination = validCombination.stream().findAny().orElse(null);

        if(anyCombination == null) {
            throw new NoChangeException("Sorry!! No change available at this moment");
        }
        if(anyCombination.isEmpty()) {
            throw new NoChangeException("Sorry!! No change available at this moment");
        }

        anyCombination.forEach((key, value) -> {
            Cash cash = getCashByDenomination((Integer) key);
            if (cash.getDenomination().equals(key)) {
                cash.setQuantity(cash.getQuantity() - value.intValue());
                cashRepository.save(cash);
            }
        });
    }

    @Cacheable(value = "CashCache", cacheManager = "myCacheManager", unless="#result == null")
    public Cash getCashByDenomination(final Integer denomination){
        return cashRepository.findDistinctByDenomination(denomination);
    }

    public void addPaymentCash(Cash cash, final List<Integer> paymentCash) {
        int coinsToAdd = Math.toIntExact(paymentCash.stream().filter(i -> i.equals(cash.getDenomination())).count());
            cash.setQuantity(cash.getQuantity() + coinsToAdd);
            log.info("Update available cash, add : {}  R{} Notes", coinsToAdd, cash.getDenomination() );
            cashRepository.save(cash);
    }

    private List<Map<Object, Long>> calculateChange(final int sum) {
        List<Map<Object, Long>> validCombination = new ArrayList<>();

        List<Cash> cashList = cashRepository.findAll();
        Map<Integer, Integer> availableOnPettyCash = cashList.stream().filter(cash -> cash.getDenomination() != null).collect(Collectors.toMap(Cash::getDenomination, Cash::getQuantity));
        int[] denominations = cashList.stream().filter(cash -> cash.getDenomination() != null).mapToInt(Cash::getDenomination).toArray();
        List<List<Integer>> possibleCombinations = combinationSum(denominations, sum);

        for (List<Integer> l : possibleCombinations) {
            validCombination.addAll(possibleCombination(l, availableOnPettyCash));
        }

        return validCombination;
    }

    //Possible combination of the given sum
    private List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        recursive(result, new ArrayList<>(), candidates, target, 0, 0);
        return result;
    }

    //Possible combination of the given sum and available on petty cash
    private List<Map<Object, Long>> possibleCombination(final List<Integer> combinationSum, final Map<Integer, Integer> availableOnPettyCash) {
        List<Map<Object, Long>> possibleCombinations = new ArrayList<>(Collections.singletonList(combinationSum.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()))));
        if (combinationSum.isEmpty()) throw new NoChangeException("Sorry!! No change available at this moment");

        List<Map<Object, Long>> invalidCombination = new ArrayList<>();
        possibleCombinations.forEach(map -> map.forEach((key, value) -> {
            if (availableOnPettyCash.containsKey(key)) {
                if (availableOnPettyCash.get(key) < value) {
                    invalidCombination.add(map);
                }
            }
        }));

        possibleCombinations.removeAll(invalidCombination);
        return possibleCombinations;
    }

    //backtracking
    private void recursive(List<List<Integer>> result, List<Integer> combination, int[] candidates, int target, int sum, int start) {
        if (sum == target) {
            result.add(new ArrayList<>(combination));
            return;
        }
        if (sum > target) return;
        for (int i = start; i < candidates.length; i++) {
            combination.add(candidates[i]);
            recursive(result, combination, candidates, target, sum + candidates[i], i);
            combination.remove(combination.size() - 1);
        }
    }
}
